package org.usfirst.frc.team6678.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;

public class Driving {

	CustomMotorDrive driver = new CustomMotorDrive(0, 1, 2, 3);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	private Joystick stick;
	
	final double yThreshold = 0.05;
	final double xThreshold = 0.15;
	boolean calibrated = false;
	
	Driving(Joystick s){
		driver.invertRightMotors(true);
		stick = s;
		gyro.calibrate(); //Dette tager maaske en 'evighed' og delay'er opstarten af koden?
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
			if(!calibrated) {
				calibrated = true;
				gyro.reset();
			}
			// TODO opdater til drivePolar naar METODEN :) er faerdig implementeret
			driver.driveXY(-gyro.getAngle()/45, sensitivity); //Tilfaeldig koefficient der virker :D
			return;
		} else {
			calibrated = false;
		}
		
		//Drej hhv 90 grader mod uret, 90 grader med uret og 180 grader ved tryk på en knap:
		//Maaske skal prioriteterne byttes om, men foerst skal det bare tjekkes om det virker...
		if(stick.getRawButton(2)) { //Er det den rigtige knap?
			if(!calibrated) {
				calibrated = true;
				gyro.reset();
			}
			if(gyro.getAngle() > -90) //Er den den rigtige vej?
				driver.tankTurn(-1);
		} else if(stick.getRawButton(4)) { //Er det den rigtige knap?
			if(!calibrated) {
				calibrated = true;
				gyro.reset();
			}
			if(gyro.getAngle() < 90) //Er den den rigtige vej?
				driver.tankTurn(1);
		} else if(stick.getRawButton(3)) { //Er det den rigtige knap?
			if(!calibrated) {
				calibrated = true;
				gyro.reset();
			}
			if(gyro.getAngle() < 180) //Er den den rigtige vej?
				driver.tankTurn(1);
		} else {
			calibrated = false;
		}
		
		if(Math.abs(twist) < Math.abs(x) || Math.abs(twist) < Math.abs(y)) {
			//x*(1-0.75*sensitivity*sensitivity) //Den gamle version
			double xScalingCoefficient = 1-0.75*sensitivity*x*sensitivity*x; //1-0.75*(x*sensitivity)^2)
			driver.driveXY(x*sensitivity*xScalingCoefficient, y*sensitivity); //Ny scaling factor, der skal testes
		} else {
			driver.tankTurn(twist*sensitivity);
		}
	}
	
}
