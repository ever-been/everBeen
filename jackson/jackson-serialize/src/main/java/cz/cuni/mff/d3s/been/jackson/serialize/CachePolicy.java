package cz.cuni.mff.d3s.been.jackson.serialize;

public enum CachePolicy {
	NO_CACHE(new NoCacheUnit()), OM_CACHE(new CacheOMUnit());

	CachePolicy(SerializationUnit serializationUnit) {
		this.serializationUnit = serializationUnit;
	}

	private final SerializationUnit serializationUnit;

	SerializationUnit getSerializationUnit() {
		return serializationUnit;
	}
}
