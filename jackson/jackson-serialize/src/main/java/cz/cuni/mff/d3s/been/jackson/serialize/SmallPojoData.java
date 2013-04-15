package cz.cuni.mff.d3s.been.jackson.serialize;

import java.util.Calendar;

class SmallPojoData implements DataGenerator {

	private final Calendar calendar;

	SmallPojoData() {
		calendar = Calendar.getInstance();
	}

	@Override
	public Object generate() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return calendar.getTime();
	}
}
