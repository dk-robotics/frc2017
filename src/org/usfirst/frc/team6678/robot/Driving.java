package org.usfirst.frc.team6678.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Driving {

	CustomMotorDrive driver = new CustomMotorDrive(0, 1, 2, 3);
	private Joystick stick;
	
	final double yThreshold = 0.05;
	final double xThreshold = 0.15;
	
	Driving(Joystick s){
		driver.invertRightMotors(true);
		stick = s;
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
			// TODO opdater til drivePolar naar METODEN :) er faerdig implementeret
			driver.driveXY(0, sensitivity);
			return;
		}
		
		if(Math.abs(twist) < Math.abs(x) || Math.abs(twist) < Math.abs(y)) {
			driver.driveXY(x*(1-0.7*sensitivity*sensitivity), y*sensitivity);
		} else {
			driver.tankTurn(twist*sensitivity);
		}
	}
	
}
