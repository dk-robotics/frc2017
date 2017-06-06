package org.usfirst.frc.team6678.robot.backgroundTasks;

import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import org.usfirst.frc.team6678.robot.Log;

/**
 * A class for using the the Ultra Sonic Distance Sensor (LV-MaxSonar® -EZ)
 * Using the RS-232 (Serial) protocol.
 */
public class UltraSonicDistanceSensor extends SensorBase implements BackgroundTask {
	
	SerialPort sp;
	int distance;
	boolean hit, min;
	
	public UltraSonicDistanceSensor() {
		sp = new SerialPort(9600, Port.kOnboard, 8, Parity.kNone, StopBits.kOne);
		distance = -1;
	}

	@Override
	public void loop() {
		String rawData = sp.readString();
		if(rawData == null || rawData.isEmpty() || rawData.length() < 5)
			return;
		
		rawData = rawData.substring(1, 5);

		try {
			distance = Integer.parseInt(rawData);
		} catch (NumberFormatException e){
			Log.error("UltraSonicDistanceSensor", "Could not parse raw data from sensor: " + rawData);
		}

		hit = distance != 5000;
		min = distance == 300;
	}

	/**
	 * The distance is a value in millimeters ranging from 300mm as the minimum, and a maximum range up to 5000mm (5m)
	 * @return The distance from the Sonic Distance Sensor, in millimeters.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * The sensor has a maximum range of 5m. This method returns whether or not the sensor detects anything inside the range.
	 * @return True if the sensor detects an object, false if not.
	 */
	public boolean isObjectDetected() {
		return hit;
	}
	
	/**
	 * The sensor has a minimum range of 30cm. This method returns whether or not the sensor detects anything within the minimun range.
	 * @return True if the sensor detects an object within 30cm, false if not.
	 */
	public boolean isObjectWithinMinimumRange() {
		return min;
	}
	
}
