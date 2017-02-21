package org.usfirst.frc.team6678.robot.sensors;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class GyroSensor {
	private AnalogGyro gyro;
	
	private boolean threadRunning;
	
	private double deltaAngle;
	private double lockedGyroAngle;
	
	GyroSensor(){
		this.gyro = new AnalogGyro(0);
	}
	
	public void lockAngle(){
		lockedGyroAngle = gyro.getAngle();
	}
	
	public double getDeltaAngle() {
		return lockedGyroAngle - gyro.getAngle();
	}
		
}
