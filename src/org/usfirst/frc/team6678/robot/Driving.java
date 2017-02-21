package org.usfirst.frc.team6678.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;

public class Driving {

	CustomMotorDrive driver = new CustomMotorDrive(0, 1, 2, 3);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	private Joystick stick;
	
	final double yThreshold = 0.05;
	final double xThreshold = 0.15;
	boolean firstPush = true;
	
	Driving(Joystick s){
		driver.invertRightMotors(true);
		stick = s;
		gyro.calibrate();
	}
	
	/**
	 * Bliver kaldt fra {@link Robot#teleopPeriodic()}
	 */
	public void loop () {
		double sensitivity = 1-(stick.getThrottle()+1)/2;
		double x = stick.getX(), y = -stick.getY(), twist = stick.getTwist();
		if(x < xThreshold*sensitivity && x > -xThreshold*sensitivity) x = 0;
		if(y < yThreshold && y > -yThreshold) y = 0;
		
		if(stick.getRawButton(2)) {
			if(firstPush) {
				firstPush = false;
				gyro.reset();
			}
			// TODO opdater til drivePolar naar METODEN :) er faerdig implementeret
			driver.driveXY(-gyro.getAngle()/45, sensitivity);
			return;
		} else {
			firstPush = true;
		}
		
		if(Math.abs(twist) < Math.abs(x) || Math.abs(twist) < Math.abs(y)) {
			driver.driveXY(x*(1-0.7*sensitivity*sensitivity), y*sensitivity);
		} else {
			driver.tankTurn(twist*sensitivity);
		}
	}
	
}
