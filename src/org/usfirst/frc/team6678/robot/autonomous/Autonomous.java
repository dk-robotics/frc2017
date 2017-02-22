package org.usfirst.frc.team6678.robot.autonomous;

import org.usfirst.frc.team6678.robot.Robot;

/**
 * Interface for en autonom funktion
 * Created by viktorstrate on 2/22/17.
 */
public interface Autonomous {
    /**
     * Kaldt for at starte funktionen
     */
    void start();

    /**
     * Kaldt for at stoppe funktionen
     */
    void stop();

    /**
     * Sand hvis funktion er i gang
     */
    boolean isRunning();

    /**
     * Skal kaldes af {@link Robot#autonomousPeriodic()}
     */
    void loop();
}
