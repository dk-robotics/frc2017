package org.usfirst.frc.team6678.robot.simulations;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * For at teste acceleration
 * Created by viktorstrate on 2/27/17.
 */
public class AccelerationTest {



    public static void main(String[] args) {
        AccelerationTest test = new AccelerationTest();

        JTextField textField = new JTextField();

        final Keychecker keychecker = new Keychecker();
        textField.addKeyListener(keychecker);

        JFrame jframe = new JFrame();

        jframe.add(textField);

        jframe.setSize(400, 350);

        jframe.setVisible(true);

        /*while(true){
            System.out.println("Pressed: "+IsKeyPressed.isWPressed());
            if (IsKeyPressed.isWPressed()) {
                test.updateAcceleration(1,1);
            } else {
                test.updateAcceleration(0,0);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(keychecker.lastKey=='1'){
                        keychecker.updateAcceleration(1, 1);
                    } else {
                        keychecker.updateAcceleration(0, 0);
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }


}

class Keychecker extends KeyAdapter {

    // Acceleration for left and right motor
    private double lastAccelerationTimestamp = 0;
    private double acceleration = 0.05; // Percentage acceleration
    private double lastLeftSpeed = 0;
    private double lastRightSpeed = 0;
    public char lastKey = ' ';

    @Override
    public void keyPressed(KeyEvent event) {

        char ch = event.getKeyChar();

        System.out.println(event.getKeyChar());

        lastKey = event.getKeyChar();

    }

    /**
     * Updates the acceleration variables according to the motor speeds, should be called approximately every 20ms
     * @param leftSpeed The speed for the left motor, value from -1 to 1
     * @param rightSpeed The speed for the right motor, value from -1 to 1
     */
    public void updateAcceleration(double leftSpeed, double rightSpeed) {

        /*double timeDelta = System.currentTimeMillis() - lastAccelerationTimestamp;
        double accelerationDecrease = timeDelta / accelerationDamp;
        double accelerationIncrease = timeDelta / accelerationDrag;
        lastAccelerationTimestamp = System.currentTimeMillis();

        // Gange 1000 for at matche accelerationDecrease, som er i millisekunder
        accelerationLeft += leftSpeed / accelerationIncrease;
        accelerationRight += rightSpeed / accelerationIncrease;

        accelerationLeft -= accelerationDecrease;
        accelerationRight -= accelerationDecrease;

        if(accelerationLeft < 0) accelerationLeft = 0;
        if(accelerationRight < 0) accelerationRight = 0;

        if(accelerationLeft > 1) accelerationLeft = 1;
        if(accelerationRight > 1) accelerationRight = 1;*/

        double timeDelta = (System.currentTimeMillis() - lastAccelerationTimestamp) / 20;
        lastAccelerationTimestamp = System.currentTimeMillis();
        lastLeftSpeed = lastLeftSpeed + acceleration * (leftSpeed - lastLeftSpeed) * timeDelta;
        lastRightSpeed = lastLeftSpeed + acceleration * (leftSpeed - lastLeftSpeed) * timeDelta;

        if(lastLeftSpeed > 0.95) lastLeftSpeed = 1;
        if(lastRightSpeed > 0.95) lastRightSpeed = 1;

        if(lastLeftSpeed < 0.05) lastLeftSpeed = 0;
        if(lastRightSpeed < 0.05) lastLeftSpeed = 0;

        System.out.println("timeDelta = " + timeDelta);
        System.out.println("lastKey = " + lastKey);
        System.out.println("lastLeftSpeed = " + lastLeftSpeed);
        System.out.println("lastRightSpeed = " + lastRightSpeed);
        System.out.println("--------------------------------");

    }
}

