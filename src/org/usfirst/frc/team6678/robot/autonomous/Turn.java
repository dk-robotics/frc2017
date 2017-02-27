package org.usfirst.frc.team6678.robot.autonomous;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import org.usfirst.frc.team6678.robot.CustomMotorDrive;
import org.usfirst.frc.team6678.robot.Log;

/**
 * Denne klasse drejer robotten 'autonomt' et vist antal grader.
 * Praecisionen er dog relativt daarlig.
 */
public class Turn implements Autonomous {

    private boolean running;
    private double degreesToTurn;
    private CustomMotorDrive customMotorDrive;
    private ADXRS450_Gyro gyro;

    /**
     *
     * @param degrees Grader som robotten skal dreje, positivt drejer til hoejre
     */
    public Turn(double degrees, ADXRS450_Gyro gyro, CustomMotorDrive customMotorDrive) {
        Log.message("AutonomousTurn", String.format("Initializing new Turn with %s degrees", degrees));
        this.degreesToTurn = degrees;
        this.gyro = gyro;
        this.customMotorDrive = customMotorDrive;
    }

    @Override
    public void start() {
        Log.info("AutonomousTurn", "Starting turn");
        gyro.reset();
        running = true;
    }

    @Override
    public void stop() {
        Log.info("AutonomousTurn", "Stopping turn");
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void loop() {
        if(!running) return;

        if(Math.abs(gyro.getAngle()) > Math.abs(degreesToTurn)) {
            customMotorDrive.stopMotors();
            stop();
        } else {
            customMotorDrive.tankTurn(Math.signum(degreesToTurn)/2);
        }
    }
}
