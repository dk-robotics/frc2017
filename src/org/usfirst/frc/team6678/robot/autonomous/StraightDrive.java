package org.usfirst.frc.team6678.robot.autonomous;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import org.usfirst.frc.team6678.robot.CustomMotorDrive;
import org.usfirst.frc.team6678.robot.Log;

/**
 *
 */
public class StraightDrive implements Autonomous {

    private boolean running;
    public boolean invertedControls;
    public double throttle;
    private CustomMotorDrive customMotorDrive;
    private ADXRS450_Gyro gyro;

    /**
     *
     */
    public StraightDrive(double throttle, ADXRS450_Gyro gyro, CustomMotorDrive customMotorDrive, boolean invertedControls) {
        Log.message("AutonomousStraightDrive", "Initializing");
        this.throttle = throttle;
        this.invertedControls = invertedControls;
        this.gyro = gyro;
        this.customMotorDrive = customMotorDrive;
    }

    @Override
    public void start() {
        Log.info("AutonomousStraightDrive", "Starting straight drive");
        customMotorDrive.alignAccelerationValues();
        gyro.reset();
        running = true;
    }

    @Override
    public void stop() {
        Log.info("AutonomousStraightDrive", "Stopping straight drive");
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void loop() {
        if(!running) return;
        customMotorDrive.driveXY(-gyro.getAngle()/45*(invertedControls ? -1 : 1), throttle*(invertedControls ? -1 : 1));
    }

    public double getThrottle() {
        return throttle;
    }

    public void setThrottle(double throttle) {
        this.throttle = throttle;
    }

    public boolean isInvertedControls() {
        return invertedControls;
    }

    public void setInvertedControls(boolean invertedControls) {
        this.invertedControls = invertedControls;
    }

}
