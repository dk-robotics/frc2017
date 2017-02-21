package org.usfirst.frc.team6678.robot.sensors;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class GyroSensor implements Runnable {
	private AnalogGyro gyro;
	
	private Thread calibrationThread;
	private boolean threadRunning;
	
	private double deltaAngle;
	private double lastGyroAngle;
	
	GyroSensor(){
		this.gyro = new AnalogGyro(0);
		calibrationThread = new Thread(this);
	}
	
	public void startCalibrationThread(){
		calibrationThread.start();
	}
	
	public void stopCalibrationThread(){
		threadRunning = false;
	}

	@Override
	public void run() {
		lastGyroAngle = gyro.getAngle();
		while(threadRunning){
			deltaAngle = lastGyroAngle - gyro.getAngle();
			
			System.out.println("Delta angle: "+deltaAngle);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			lastGyroAngle = gyro.getAngle();
		}
	}
	
	public double getDeltaAngle() {
		return deltaAngle;
	}
		
}
