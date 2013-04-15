package cz.cuni.mff.d3s.been.jackson.serialize;

class SmallPojoData implements DataGenerator {

	SmallPojoData() {}

	@Override
	public Object generate() {
		return new TimeResult(System.currentTimeMillis());
	}

}
