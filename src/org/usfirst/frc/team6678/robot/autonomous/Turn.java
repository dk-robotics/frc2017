package org.usfirst.frc.team6678.robot.autonomous;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import org.usfirst.frc.team6678.robot.CustomMotorDrive;

/**
 * Staar for at dreje robotten autonomt
 * Created by viktorstrate on 2/22/17.
 */
public class Turn implements Autonomous {

    private boolean running;
    private double degreesToTurn;
    private CustomMotorDrive customMotorDrive;
    private ADXRS450_Gyro gyro;

    /**
     *
     * @param degrees Grader som robotten skal dreje, positivt drejer til højre
     */
    public Turn(double degrees, ADXRS450_Gyro gyro, CustomMotorDrive customMotorDrive) {
        this.degreesToTurn = degrees;
        this.gyro = gyro;
        this.customMotorDrive = customMotorDrive;
    }

    @Override
    public void start() {
        gyro.reset();
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void loop() {
        if(!running) return;

        if(Math.abs(gyro.getAngle()) > Math.abs(degreesToTurn)){
            stop();
        } else {
            customMotorDrive.tankTurn(Math.signum(degreesToTurn));
        }
    }
}
