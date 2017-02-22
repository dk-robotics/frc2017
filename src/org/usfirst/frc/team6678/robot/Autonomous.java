package org.usfirst.frc.team6678.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Timer;

/**
 * Klassen indeholder materiale for at haandtere autonom styring
 * Created by viktorstrate on 2/22/17.
 */

// TODO finish class
public class Autonomous {
    private Timer timer;

    private ADXRS450_Gyro gyro;

    public Autonomous(Timer timer) {
        this.timer = timer;
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
