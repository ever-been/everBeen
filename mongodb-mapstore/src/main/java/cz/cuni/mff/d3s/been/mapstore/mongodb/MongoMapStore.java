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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.core.MapStore;
import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MongoMapStore implements MapStore, MapLoaderLifecycleSupport {

    private static final String FAILED_STORE_MIRROR_MAP_SUFFIX = "STORE_FAILED_MIRROR";

    private HazelcastInstance hazelcastInstance;

    private String mapName;
    private String failedStoreMapName;
    private MongoDBConverter converter;
    private DBCollection coll;
    private MongoTemplate mongoTemplate;

    protected static final Logger logger = LoggerFactory.getLogger(MongoMapStore.class);

    public MongoMapStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public IMap getFailedStoreMap() {
        return hazelcastInstance.getMap(failedStoreMapName);
    }

    @Override
    public void store(Object key, Object value) {
        IMap failedStoreMap = getFailedStoreMap();
        if (!storePersistent(key, value)) {
            failedStoreMap.put(key, value);
        } else {
            if (failedStoreMap.size() > 0) {
                if (failedStoreMap.lockMap(5, TimeUnit.SECONDS)) {
                    resolveFailedObjects(failedStoreMap);
                    failedStoreMap.unlockMap();
                }
            }
        }
    }

    private void resolveFailedObjects(IMap failedStoreMap) {
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
            logger.warn(message, e);
            return false;
        }
        return true;
    }

    @Override
    public void storeAll(Map map) {
        for (Object key : map.keySet()) {
            store(key, map.get(key));
        }
    }

    @Override
    public void delete(Object key) {
        if (!deletePersistent(key)) {
            getFailedStoreMap().put(key, null);
        }
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
            logger.warn(message, e);
            return false;
        }
        return true;
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

    public Object load(Object key) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", key);
        DBObject obj = coll.findOne(dbo);
        if (obj == null)
            return null;

        try {
            Class clazz = Class.forName(obj.get("_class").toString());
            return converter.toObject(clazz, obj);
        } catch (ClassNotFoundException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    public Map loadAll(Collection keys) {
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
                map.put(obj.get("_id"), converter.toObject(clazz, obj));
            } catch (ClassNotFoundException e) {
                logger.warn(e.getMessage(), e);
            }
        }
        return map;
    }

    public Set loadAllKeys() {
        Set keyset = new HashSet();
        BasicDBList dbo = new BasicDBList();
        dbo.add("_id");
        DBCursor cursor = coll.find(null, dbo);
        while (cursor.hasNext()) {
            keyset.add(cursor.next().get("_id"));
        }
        return keyset;
    }

    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        if (properties.get("collection") != null) {
            this.mapName = (String) properties.get("collection");
        } else {
            this.mapName = mapName;
        }
        this.failedStoreMapName = String.format("%s_%s", this.mapName, FAILED_STORE_MIRROR_MAP_SUFFIX);
        this.hazelcastInstance = hazelcastInstance;
        this.coll = mongoTemplate.getCollection(this.mapName);
        this.converter = new SpringMongoDBConverter(mongoTemplate);
    }

    public void destroy() {
    }
}
