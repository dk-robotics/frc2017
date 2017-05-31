package org.usfirst.frc.team6678.robot.backgroundTasks;

import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import org.usfirst.frc.team6678.robot.Log;

/**
 * A class for using the the Ultra Sonic Distance Sensor (LV-MaxSonar® -EZ)
 * Using the RS-232 (Serial) protocol
 */
public class UltraSonicDistanceSensor extends SensorBase implements BackgroundTask {
	
	SerialPort sp;
	double distance;
	boolean hit;
	
	public UltraSonicDistanceSensor() {
		sp = new SerialPort(9600, Port.kOnboard, 8, Parity.kNone, StopBits.kOne);
		distance = -1;
	}

	@Override
	public void loop() {
		//The output is an ASCII capital “R”, followed by three ASCII character digits representing the
		//range in inches up to a maximum of 255, followed by a carriage return (ASCII 13). 
		String rawData = sp.readString();

		// *** THIS PART IS NOT TESTED YET ***
		if(rawData == null || rawData.isEmpty() || rawData.length() < 4)
			return;
		
		Log.debug("UltraSonicDistanceSensor", "Raw data input: " + rawData);
		
		rawData = rawData.substring(1, 3);

		int inchValue = -1;

		try {
			inchValue = Integer.parseInt(rawData);
		} catch (NumberFormatException e){
			Log.error("UltraSonicDistanceSensor", "Could not parse raw data from sensor:");
			e.printStackTrace();
		}

		if (inchValue > 0) {
			hit = inchValue == 255;
			distance = inchValue * 2.54;
		}

		Log.debug("UltraSonicDistanceSensor", "Distance: "+distance+" cm");
	}

	/**
	 *
	 * @return The distance from the Sonic Distance Sensor,
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 *
	 * @return True if sensor hit an object
	 */
	public boolean getHit() {
		return hit;
	}
	
}
