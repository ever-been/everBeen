/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.cuni.mff.d3s.been.mapstore.mongodb;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.hazelcast.core.*;
import com.mongodb.*;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * Implementation of {@link MapStore} for MongoDB.
 * 
 * The implementation is based on hazelcast-spring with modification for the
 * BEEN project. Licence honored.
 * 
 */
public class MongoMapStore implements MapStore, MapLoaderLifecycleSupport {

	protected static final Logger log = LoggerFactory.getLogger(MongoMapStore.class);

	private static final String FAILED_STORE_MIRROR_MAP_SUFFIX = "FAILOVER";

	// name of the Hazelcast List where are stored all names of already loaded maps
	private static final String LOADED_LIST = "LOADED_LIST";

	// TODO - consider configurable delay and period
	private static final long FLUSH_INITIAL_DELAY_SECONDS = 180;
	private static final long FLUSH_PERIOD_SECONDS = 180;

	private HazelcastInstance hazelcastInstance;

	// name of the map which is covered by this mapstore
	private String mapName;

	// name of the failover map for map covered by this mapstore
	private String failedStoreMapName;

	private MongoDBConverter converter;
	private DBCollection coll;
	private MongoTemplate mongoTemplate;

	// tells us if map covered by this mapstore has been already loaded into memory
	private volatile boolean mapIsLoaded;

	public MongoMapStore(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}


    private IMap failedStoreMap;

	public IMap getFailedStoreMap() {
		return failedStoreMap;
	}

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Override
	public void store(Object key, Object value) {
		if (!storePersistent(key, value)) {
			getFailedStoreMap().put(key, value);
		} else {
			resolveFailedObjects();
		}
	}

	@Override
	public void storeAll(Map map) {
		for (Object key : map.keySet()) {
			store(key, map.get(key));
		}
		resolveFailedObjects();
	}

	@Override
	public void delete(Object key) {
		if (!deletePersistent(key)) {
			getFailedStoreMap().put(key, null);
		}
	}

	@Override
	public void deleteAll(Collection keys) {
		BasicDBList dbo = new BasicDBList();
		for (Object key : keys) {
			dbo.add(new BasicDBObject("_id", key));
		}
		BasicDBObject dbb = new BasicDBObject("$or", dbo);
		coll.remove(dbb);
	}

	@Override
	public Object load(Object key) {
		try {
			DBObject dbo = new BasicDBObject();
			dbo.put("_id", key);
			DBObject obj = coll.findOne(dbo);
			if (obj == null)
				return null;

			try {
				Class clazz = Class.forName(obj.get("_class").toString());
				Object object = converter.toObject(clazz, obj);

				if (object instanceof TaskEntry) {
					setLoadedFromMapStore((TaskEntry) object);
				}

				return object;
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
			}
			return null;
		} catch (Throwable t) {
			String msg = String.format("Cannot load key '%s' of map '%s'", key.toString(), mapName);
			log.error(msg, t);
			return null;
		}
	}

	@Override
	public Map loadAll(Collection keys) {
		try {
			Map map = new HashMap();
			BasicDBList dbo = new BasicDBList();
			for (Object key : keys) {
				dbo.add(new BasicDBObject("_id", key));
			}
			BasicDBObject dbb = new BasicDBObject("$or", dbo);
			DBCursor cursor = coll.find(dbb);
			while (cursor.hasNext()) {
				try {
					DBObject obj = cursor.next();
					Class clazz = null;
					clazz = Class.forName(obj.get("_class").toString());

					Object object = converter.toObject(clazz, obj);

					if (object instanceof TaskEntry) {
						setLoadedFromMapStore((TaskEntry) object);
					}

					map.put(obj.get("_id"), object);
				} catch (ClassNotFoundException e) {
					log.error(e.getMessage(), e);
				}
			}
			return map;
		} catch (Throwable t) {
			log.warn("Cannot load collection from MongoDB", t);
		} finally {
			mapIsLoaded = true;
		}
		return null;
	}

	@Override
	public Set loadAllKeys() {

		boolean loaded = false;
		ILock l = hazelcastInstance.getLock("LOADING_" + mapName);
		l.lock();

		IList loadedList = hazelcastInstance.getList(LOADED_LIST);
		if (loadedList.contains(mapName)) {
			loaded = true;
		}

		Set keyset = Collections.emptySet();

		if (!loaded) {
			try {
				keyset = new HashSet();
				BasicDBList dbo = new BasicDBList();
				dbo.add("_id");
				DBCursor cursor = coll.find(null, dbo);
				while (cursor.hasNext()) {
					keyset.add(cursor.next().get("_id"));
				}
				loadedList.add(mapName);
			} catch (Throwable t) {
				keyset = Collections.emptySet();
				log.warn("Cannot load keys from MongoDB", t);
			}
		}
		l.unlock();

		return keyset;
	}

	@Override
	public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
		if (properties.get("collection") != null) {
			this.mapName = (String) properties.get("collection");
		} else {
			this.mapName = mapName;
		}
		this.failedStoreMapName = String.format("%s_%s", this.mapName, FAILED_STORE_MIRROR_MAP_SUFFIX);
        this.failedStoreMap = hazelcastInstance.getMap(failedStoreMapName);
		this.hazelcastInstance = hazelcastInstance;
		this.coll = mongoTemplate.getCollection(this.mapName);
		this.converter = new SpringMongoDBConverter(mongoTemplate);

		scheduleFailoverMapFlusher();
	}

	@Override
	public void destroy() {
		// the last attempt to save the cluster from this mapstore. If this is the last node in the cluster and
		// failover map is not empty, cluster will be in inconsistent state after restart - maybe recoverable,
		// maybe not.
		resolveFailedObjects();
	}

	private boolean deletePersistent(Object key) {
		try {
			DBObject dbo = new BasicDBObject();
			dbo.put("_id", key);
			coll.remove(dbo);
		} catch (Throwable e) {
			String message = String.format(
					"Item with key '%s' couldn't be deleted from hazelcast persistent mapstore, reason: %s",
					key.toString(),
					e.getMessage());
			log.debug(message, e);
			return false;
		}
		return true;
	}

	private synchronized void resolveFailedObjects() {
		IMap failedStoreMap = getFailedStoreMap();

		if (failedStoreMap.size() > 0 && failedStoreMap.lockMap(5, TimeUnit.SECONDS)) {
			try {
				for (Object failedKey : failedStoreMap.keySet()) {
					Object failedValue = failedStoreMap.get(failedKey);
					if (failedValue == null) {
						if (deletePersistent(failedKey)) {
							break;
						}
						failedStoreMap.remove(failedKey);
					} else {
						if (storePersistent(failedKey, failedValue)) {
							break;
						}
						failedStoreMap.remove(failedKey);
					}
				}
			} finally {
				failedStoreMap.unlockMap();
			}
		}
	}

	private void scheduleFailoverMapFlusher() {
		final Runnable flusher = new Runnable() {
			public void run() {
				if (mapIsLoaded) {
					try {
						resolveFailedObjects();
					} catch (Exception e) {
						log.error(String.format("Periodic flushing of map '%s' failed", mapName), e);
					}
				}
			}
		};
		scheduler.scheduleAtFixedRate(flusher, FLUSH_INITIAL_DELAY_SECONDS, FLUSH_PERIOD_SECONDS, TimeUnit.SECONDS);
	}

	private boolean storePersistent(Object key, Object value) {
		try {
			DBObject dbo = converter.toDBObject(value);
			dbo.put("_id", key);
			coll.save(dbo);
		} catch (Throwable e) {
			String message = String.format(
					"Item with key '%s' couldn't be stored in hazelcast persistent mapstore, reason: %s",
					key.toString(),
					e.getMessage());
			log.debug(message, e);
			return false;
		}
		return true;
	}

	private void setLoadedFromMapStore(final TaskEntry entry) {
		entry.setLoadedFromPersistence(true);
	}
}
