package cz.cuni.mff.d3s.been.jackson.serialize;

enum DataType {
	SMALL_POJO(new SmallPojoData()), ARRAY(new ArrayData());

	private final DataGenerator dataGenerator;

	DataType(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	public DataGenerator getDataGenerator() {
		return dataGenerator;
	}
}
