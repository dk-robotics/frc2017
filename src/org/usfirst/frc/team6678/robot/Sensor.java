package org.usfirst.frc.team6678.robot;

import java.util.HashMap;

/**
 * Klassen Sensor, er ikke i brug endu
 */

public class Sensor {

	private HashMap<SensorType, Double> references = new HashMap<>();
	
	public enum SensorType {
		GYRO,
	}
	
	public Sensor() {
		references.put(SensorType.GYRO, 0d);
	}
	
	public double getRawSensorValue(SensorType type) {
		return 0;
	}
	
	public void calibrate(SensorType sensor) {
		references.put(sensor, getRawSensorValue(sensor));
	}
	
	public double getCalibratedSensorValue(SensorType type) {
		return getRawSensorValue(type)-references.get(type);
	}
	
}
