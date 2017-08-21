package org.usfirst.frc.team6678.robot;

import org.usfirst.frc.team6678.robot.autonomous.StraightDrive;
import org.usfirst.frc.team6678.robot.autonomous.Turn;
import org.usfirst.frc.team6678.robot.backgroundTasks.BackgroundTaskHandler;
import org.usfirst.frc.team6678.robot.backgroundTasks.ButtonSwitchState;
import org.usfirst.frc.team6678.robot.backgroundTasks.UltraSonicDistanceSensor;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;

/**
 * A high level class for handling driving mechanics for the robot.
 * Refer to {@link CustomMotorDrive} for more low level driving mechanics.
 */
public class Driving {

	CustomMotorDrive driver = new CustomMotorDrive(0, 1, 2, 3);
	private ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	private Joystick stick;
	private ButtonSwitchState invertSwitchButton;
	private UltraSonicDistanceSensor frontDistance = new UltraSonicDistanceSensor();
	private StraightDrive straightDriver;
	private Turn turner;
	
	private final double yThreshold = 0.05;
	private final double xThreshold = 0.15;
	private final double minAllowedDistance = 1500;

	Driving(Joystick joystick){
		Log.message("Driving", "Initializing");
		long initStartTime = System.currentTimeMillis();

		driver.invertRightMotors(true);
		stick = joystick;

		invertSwitchButton = new ButtonSwitchState(stick, 8);
		BackgroundTaskHandler.handleBackgroundTask(invertSwitchButton);
		BackgroundTaskHandler.handleBackgroundTask(frontDistance);
		straightDriver = new StraightDrive(0, gyro, driver, false);
		BackgroundTaskHandler.handleBackgroundTask(straightDriver);
		turner = new Turn(gyro, driver);
		BackgroundTaskHandler.handleBackgroundTask(turner);

		gyro.calibrate(); //Dette tager maaske en 'evighed' og delay'er opstarten af koden?

        Log.info("Driving", "Initializing finished");
        Log.debug("Driving", "Constructor time: " + (System.currentTimeMillis()-initStartTime));
    }
	
	/**
	 * Bliver kaldt fra {@link Robot#teleopPeriodic()}
	 */
	public void loop () {
        if(stick.getRawButton(12)) {
            Log.info("Driving", "Annullerer autonomous (herunder fx turn)");
            straightDriver.stop();
            turner.stop();
            driver.stopMotors();
            return;
        }

        boolean invertedControls = invertSwitchButton.getState();

		double throttle = 1-(stick.getThrottle()+1)/2d;
		double x = stick.getX()*(invertedControls ? -1 : 1);
		double y = -stick.getY()*(invertedControls ? -1 : 1);
		double twist = stick.getTwist();

		if(x < xThreshold*throttle && x > -xThreshold*throttle)
			x = 0;
		else
			x -= xThreshold*throttle*Math.signum(x);
		if(y < yThreshold && y > -yThreshold) y = 0;

		Log.debug("Driving", String.format("Loop x: %s y: %s", x, y));

		handleButtonsForAutoTurn();
		if(turner.isRunning()) {
			straightDriver.stop();
			return; //Prevent overruling
		}

		handleStraightDrive(invertedControls, throttle);
		if(straightDriver.isRunning())
			return; //Prevent overruling

		handleStickDriving(x, y, twist, throttle);
	}

	private boolean isDistanceOK(double throttle) {
		return frontDistance.getDistance() > minAllowedDistance*(throttle+0.5);
	}

	private void handleButtonsForAutoTurn() {
		double degrees = 0;
		if(stick.getRawButton(3)) degrees = - 90;
		if(stick.getRawButton(4)) degrees = + 90;
		if(stick.getRawButton(5)) degrees = -180;
		if(stick.getRawButton(6)) degrees = +180;

		if(degrees != 0) {
			turner.setDegreesToTurn(degrees);
			turner.start();
		}
	}

	private void handleStraightDrive(boolean invertedControls, double throttle) {
		if(stick.getRawButton(2) && !straightDriver.isRunning())
			straightDriver.start();
		else if(!stick.getRawButton(2) && straightDriver.isRunning())
			straightDriver.stop();
		if(straightDriver.isRunning()) {
			throttle *= (invertedControls ? -1 : 1);
			if(isDistanceOK(throttle)/* || (invertedControls && throttle >= 0)*/) {
				straightDriver.setInvertedControls(invertedControls);
				straightDriver.setThrottle(throttle);
			} else {
				straightDriver.stop();
				driver.stopMotors();
			}
		}
	}

	private void handleStickDriving(double x, double y, double twist, double throttle) {
		if(Math.abs(twist) < Math.abs(x)*2 || Math.abs(twist) < Math.abs(y)*1.5) {
			Log.debug("Driving", "Driving using driveXY");
			double xScalingCoefficient = 1-0.75*throttle*x*throttle*x; //1-0.75*(x*throttle)^2)
			if(isDistanceOK(y*throttle)/* || y < 0*/)
				driver.driveXY(x*throttle*xScalingCoefficient, y*throttle);
			else
				driver.stopMotors();
		} else {
			Log.debug("Driving", "Driving using tankTurn");
			driver.tankTurn(twist*throttle);
		}
	}

}
