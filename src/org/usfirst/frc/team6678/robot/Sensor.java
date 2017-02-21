package org.usfirst.frc.team6678.robot;

import java.util.Dictionary;
import java.util.HashMap;

public class Sensor {

	private HashMap<SensorType, Double> references = new HashMap<>();
	
	public enum SensorType {
		GYRO,
		DISTANCE,
	}
	
	public Sensor() {
		references.put(SensorType.GYRO, new Double(0));
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
