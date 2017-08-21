package org.usfirst.frc.team6678.robot;

import org.usfirst.frc.team6678.robot.autonomous.Autonomous;
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
	private Autonomous runningAutonomous = null;
	private StraightDrive straightDriver;
	
	private final double yThreshold = 0.05;
	private final double xThreshold = 0.15;

	Driving(Joystick joystick){
		Log.message("Driving", "Initializing");
		long initStartTime = System.currentTimeMillis();

		driver.invertRightMotors(true);
		stick = joystick;

		invertSwitchButton = new ButtonSwitchState(stick, 8);
		BackgroundTaskHandler.handleBackgroundTask(invertSwitchButton);
		BackgroundTaskHandler.handleBackgroundTask(frontDistance);
		BackgroundTaskHandler.handleBackgroundTask(runningAutonomous);
		straightDriver = new StraightDrive(0, gyro, driver, false);
		BackgroundTaskHandler.handleBackgroundTask(straightDriver);

		gyro.calibrate(); //Dette tager maaske en 'evighed' og delay'er opstarten af koden?

        Log.info("Driving", "Initializing finished");
        Log.debug("Driving", "Constructor time: " + (System.currentTimeMillis()-initStartTime));
    }
	
	/**
	 * Bliver kaldt fra {@link Robot#teleopPeriodic()}
	 */
	public void loop () {
		//Stop runningAutonomous (eg Turn or StraightDrive) and stop movement
        if(stick.getRawButton(12)) {
            Log.info("Driving", "Annullerer autonomous (herunder fx turn)");
            runningAutonomous.stop();
            runningAutonomous = null;
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
		
		/*if(stick.getRawButton(2)) {
			if(!calibrated) {
				driver.alignAccelerationValues();
				calibrated = true;
				gyro.reset();
			}
			if(frontDistance.getDistance() > 300)
				driver.driveXY(-gyro.getAngle()/45*(invertedControls ? -1 : 1), throttle*(invertedControls ? -1 : 1)); //Tilfaeldig koefficient der virker :D
			return;
		} else {
			calibrated = false;
		}*/

		if(stick.getRawButton(2) && !straightDriver.isRunning())
			straightDriver.start();
		else if(!stick.getRawButton(2) && straightDriver.isRunning())
			straightDriver.stop();
		if(straightDriver.isRunning()) {
			straightDriver.setInvertedControls(invertedControls);
			straightDriver.setThrottle(throttle);
		}

		handleButtonsForAutoTurn();

		/*if(runningAutonomous != null && runningAutonomous.isRunning()) {
			runningAutonomous.loop();
			return;
		}

		if(runningAutonomous != null && !runningAutonomous.isRunning())
			runningAutonomous = null;
		*/

		if(runningAutonomous != null && runningAutonomous.isRunning())
			return; //Prevent the autonomous action from being overruled

		handleStickDriving(x, y, twist, throttle);
	}

	private void handleStickDriving(double x, double y, double twist, double sensitivity) {
		if(Math.abs(twist) < Math.abs(x)*2 || Math.abs(twist) < Math.abs(y)*1.5) {
			Log.debug("Driving", "Driving using driveXY");
			double xScalingCoefficient = 1-0.75*sensitivity*x*sensitivity*x; //1-0.75*(x*sensitivity)^2)
			if(frontDistance.getDistance() > 300 || y < 0)
				driver.driveXY(x*sensitivity*xScalingCoefficient, y*sensitivity);
			else
				driver.driveXY(0, 0);
		} else {
			Log.debug("Driving", "Driving using tankTurn");
			driver.tankTurn(twist*sensitivity);
		}
	}

	private void handleButtonsForAutoTurn() {
		double degrees = 0;
		if(stick.getRawButton(3)) degrees = - 90;
		if(stick.getRawButton(4)) degrees = + 90;
		if(stick.getRawButton(5)) degrees = -180;
		if(stick.getRawButton(6)) degrees = +180;

		if(degrees != 0 && (runningAutonomous == null || !runningAutonomous.isRunning())) {
			runningAutonomous = new Turn(degrees, gyro, driver);
			Log.error("Driving", "Turning + " + degrees + "degrees");
			runningAutonomous.start();
		}

		/*if(stick.getRawButton(3)) {
			if(runningAutonomous == null || !runningAutonomous.isRunning()) {
				runningAutonomous = new Turn(-90, gyro, driver);
				runningAutonomous.start();
			}
		} else if(stick.getRawButton(4)) {
			if(runningAutonomous == null || !runningAutonomous.isRunning()) {
				runningAutonomous = new Turn(90, gyro, driver);
				runningAutonomous.start();
			}
		} else if(stick.getRawButton(5)) {
			if(runningAutonomous == null || !runningAutonomous.isRunning()) {
				runningAutonomous = new Turn(-180, gyro, driver);
				runningAutonomous.start();
			}
		} else if(stick.getRawButton(6)) {
			if(runningAutonomous == null || !runningAutonomous.isRunning()) {
				runningAutonomous = new Turn(180, gyro, driver);
				runningAutonomous.start();
			}
		}*/
	}

}
