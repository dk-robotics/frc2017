package org.usfirst.frc.team6678.robot;

import org.usfirst.frc.team6678.robot.autonomous.Autonomous;
import org.usfirst.frc.team6678.robot.autonomous.Turn;

import backgroundTasks.ButtonSwitchState;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;

public class Driving {

	CustomMotorDrive driver = new CustomMotorDrive(0, 1, 2, 3);
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	private Joystick stick;
	private ButtonSwitchState invertSwitchButton;
	Autonomous runningAutonomous = null;
	
	final double yThreshold = 0.05;
	final double xThreshold = 0.15;
	boolean calibrated = false, invertedControls = false;
	
	Driving(Joystick s){
		driver.invertRightMotors(true);
		stick = s;
		invertSwitchButton = new ButtonSwitchState(stick, 8);
		gyro.calibrate(); //Dette tager maaske en 'evighed' og delay'er opstarten af koden?
	}
	
	/**
	 * Bliver kaldt fra {@link Robot#teleopPeriodic()}
	 */
	public void loop () {

        if(stick.getRawButton(12)) { //Annuller Turn!
            runningAutonomous.stop();
            runningAutonomous = null;
        }
        
        invertSwitchButton.loop();
        invertedControls = invertSwitchButton.getState();
        
		double sensitivity = 1-(stick.getThrottle()+1)/2,
				x = stick.getX()*(invertedControls ? -1 : 1),
				y = -stick.getY()*(invertedControls ? -1 : 1),
				twist = stick.getTwist();//*(invertedControls ? -1 : 1);
		if(x < xThreshold*sensitivity && x > -xThreshold*sensitivity) x = 0;
		if(y < yThreshold && y > -yThreshold) y = 0;
		
		if(stick.getRawButton(2)) {
			if(!calibrated) {
				driver.alignAccelerationValues();
				calibrated = true;
				gyro.reset();
			}
			driver.driveXY(-gyro.getAngle()/45*(invertedControls ? -1 : 1), sensitivity*(invertedControls ? -1 : 1)); //Tilfaeldig koefficient der virker :D
			return;
		} else {
			calibrated = false;
		}
		
		//Drej hhv 90 grader mod uret, 90 grader med uret og 180 grader ved tryk paa en knap:
		//Maaske skal prioriteterne byttes om, men foerst skal det bare tjekkes om det virker...
		if(stick.getRawButton(3)) {
			if(runningAutonomous == null) {
				runningAutonomous = new Turn(-90, gyro, driver);
				System.out.println("Turing 90 degrees left...");
				runningAutonomous.start();
			}
		} else if(stick.getRawButton(4)) {
			if(runningAutonomous == null) {
				runningAutonomous = new Turn(90, gyro, driver);
				runningAutonomous.start();
			}
		} else if(stick.getRawButton(5)) {
			if(runningAutonomous == null) {
				runningAutonomous = new Turn(-180, gyro, driver);
				runningAutonomous.start();
			}
		} else if(stick.getRawButton(6)) {
			if(runningAutonomous == null) {
				runningAutonomous = new Turn(180, gyro, driver);
				runningAutonomous.start();
			}
		}
		
		if(runningAutonomous != null && runningAutonomous.isRunning()) {
			runningAutonomous.loop();
			return;
		}
		
		if(runningAutonomous != null && !runningAutonomous.isRunning())
			runningAutonomous = null;

		if(Math.abs(twist) < Math.abs(x)*2 || Math.abs(twist) < Math.abs(y)*1.5) {
			//x*(1-0.75*sensitivity*sensitivity) //Den gamle version
			double xScalingCoefficient = 1-0.75*sensitivity*x*sensitivity*x; //1-0.75*(x*sensitivity)^2)
			double offset = xThreshold*sensitivity*Math.signum(x);
			driver.driveXY(x*sensitivity*xScalingCoefficient-offset, y*sensitivity);
		} else {
			driver.tankTurn(twist*sensitivity);
		}
	}
	
}
