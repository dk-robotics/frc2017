package org.usfirst.frc.team6678.robot.backgroundTasks;

import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Parity;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.SerialPort.StopBits;

public class UltraSonicDistanceSensor extends SensorBase implements BackgroundTask {
	
	SerialPort sp;
	double distance;
	
	public UltraSonicDistanceSensor() {
		sp = new SerialPort(9600, Port.kOnboard, 8, Parity.kNone, StopBits.kOne);
	}

	@Override
	public void loop() {
		//The output is an ASCII capital “R”, followed by three ASCII character digits representing the
		//range in inches up to a maximum of 255, followed by a carriage return (ASCII 13). 
		sp.readString();
	}
	
	public double getDistance() {
		return distance;
	}
	
}
