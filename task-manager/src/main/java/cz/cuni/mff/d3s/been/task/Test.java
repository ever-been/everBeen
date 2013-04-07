package cz.cuni.mff.d3s.been.task;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.IMap;

/**
 * @author Martin Sixta
 */
public class Test {
	public static void main(String[] args) {
		IMap<String, Serializable> aMap = Hazelcast.getMap("mymap");

		aMap.put("key1", "value1", 2, TimeUnit.SECONDS);
		System.out.println(aMap.getMapEntry("key1").getExpirationTime());

		System.out.println(aMap.getMapEntry("key1").getExpirationTime());
		aMap.put("key1", "value2");
		System.out.println(aMap.getMapEntry("key1").getExpirationTime());

		System.out.println(aMap.getMapEntry("key1").getExpirationTime());

		try {
			Thread.sleep(6300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("value that should be null= " + aMap.get("key1"));
	}
}
