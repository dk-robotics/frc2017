package org.usfirst.frc.team6678.robot.autonomous;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import org.usfirst.frc.team6678.robot.CustomMotorDrive;
import org.usfirst.frc.team6678.robot.Log;

/**
 * Denne klasse drejer robotten 'autonomt' et vist antal grader.
 * Praecisionen er dog relativt daarlig.
 */
public class Turn implements Autonomous {

    private boolean running = false;
    private double degreesToTurn = 0;
    private CustomMotorDrive customMotorDrive;
    private ADXRS450_Gyro gyro;

    public Turn(ADXRS450_Gyro gyro, CustomMotorDrive customMotorDrive) {
        Log.message("AutonomousTurn", "Initializing new Turn");
        this.gyro = gyro;
        this.customMotorDrive = customMotorDrive;
    }

    /**
     *
     * @param degrees Grader som robotten skal dreje, positivt drejer til hoejre
     */
    public Turn(double degrees, ADXRS450_Gyro gyro, CustomMotorDrive customMotorDrive) {
        this(gyro, customMotorDrive);
        setDegreesToTurn(degrees);
    }

    @Override
    public void start() {
        Log.info("AutonomousTurn", "Starting turn");
        gyro.reset();
        customMotorDrive.alignAccelerationValues();
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
            Log.info("Turn", "Turning " + degreesToTurn + " degrees is completed!");
        } else {
            customMotorDrive.tankTurn(Math.signum(degreesToTurn)/5);
            Log.info("Turn", "Turning. " + (degreesToTurn-gyro.getAngle()) + " degrees remaining");
        }
    }

    public double getDegreesToTurn() {
        return degreesToTurn;
    }

    public void setDegreesToTurn(double degreesToTurn) {
        this.degreesToTurn = degreesToTurn;
    }

}
