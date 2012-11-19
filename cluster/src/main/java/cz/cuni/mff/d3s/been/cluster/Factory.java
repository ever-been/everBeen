package cz.cuni.mff.d3s.been.cluster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Factory {
	public static Member createMember(String messagingSystem, Object... options) throws IllegalArgumentException {
		if (messagingSystem.equals("hazelcast")) {
			ClassLoader classLoader = Factory.class.getClassLoader();

			try {
				Class aClass = classLoader.loadClass("cz.cuni.mff.d3s.been.cluster.hazelcast.Factory");

				Method factory = aClass.getDeclaredMethod("createMember", Object[].class);

				return (Member) factory.invoke(null, (Object)options);

			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
				e.printStackTrace();
			}

		} else {
			throw new IllegalArgumentException("No such messaging system '" + messagingSystem + "'" );
		}

		return null;
	}
}
