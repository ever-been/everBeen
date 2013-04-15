package cz.cuni.mff.d3s.been.jackson.serialize;

import java.util.Random;

class ArrayData implements DataGenerator {

	private static final int ARRAY_LENGTH = 100;
	private final Random random;

	ArrayData() {
		random = new Random(System.currentTimeMillis());
	}

	@Override
	public Object generate() {
		int[] data = new int[ARRAY_LENGTH];
		for (int i = 0; i < ARRAY_LENGTH; ++i) {
			data[i] = random.nextInt();
		}
		return data;
	}
}
