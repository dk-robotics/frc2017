package org.usfirst.frc.team6678.robot.autonomous;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team6678.robot.CustomMotorDrive;
import org.usfirst.frc.team6678.robot.Robot;

/**
 * Klassen indeholder materiale for at haandtere autonom styring
 * Created by viktorstrate on 2/22/17.
 */

// TODO finish class
public class AutonomousHandler {
    private Timer timer;
    private CustomMotorDrive customMotorDrive;

    private ADXRS450_Gyro gyro;

    public AutonomousHandler(Timer timer, CustomMotorDrive customMotorDrive) {
        this.timer = timer;
        this.customMotorDrive = customMotorDrive;
        gyro = new ADXRS450_Gyro();
    }

    /**
     * Kaldt af {@link Robot#autonomousInit()}
     */
    public void init() {
        gyro.reset();
    }

    /**
     * Kaldt af {@link Robot#autonomousPeriodic()}
     */
    public void loop() {

    }

    /**
     *
     * @param degrees antal grader robotten skal dreje
     */
    private void rotate(double degrees){

    }

}
