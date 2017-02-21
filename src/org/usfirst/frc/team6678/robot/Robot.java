package org.usfirst.frc.team6678.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.UsbCameraInfo;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	//RobotDrive myRobot = new RobotDrive(0, 1);
	CustomMotorDrive driver = new CustomMotorDrive(0, 1, 2, 3);
	Joystick stick = new Joystick(0);
	final double yThreshold = 0.05;
	final double xThreshold = 0.15;
	
	Timer timer = new Timer();
	
	Compressor compressor = new Compressor(0);
	DoubleSolenoid actuator = new DoubleSolenoid(0, 1);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		System.out.println("Hello, World!");
		
		driver.invertRightMotors(true);
		
		compressor.setClosedLoopControl(true); //Saetter kompressoren til at koere naar noedvendigt
		
		System.out.println("Setup af camera: " + setupStreamingCamera(0));
		setupServer(4444);
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() { //Skal laves fuldkommen om fra bunden...
		// Drive for 2 seconds
		/*
		if (timer.get() < 2.0) {
			myRobot.drive(-0.5, 0.0); // drive forwards half speed
		} else {
			myRobot.drive(0.0, 0.0); // stop robot
		}*/
	}

	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		//myRobot.arcadeDrive(stick);
		double sensitivity = 1-(stick.getThrottle()+1)/2;
		double x = stick.getX(), y = -stick.getY(), twist = stick.getTwist();
		if(x < xThreshold*sensitivity && x > -xThreshold*sensitivity) x = 0;
		if(y < yThreshold && y > -yThreshold) y = 0;
		//if(twist < yThreshold && twist > -yThreshold) twist = 0;
		
		if(Math.abs(twist) < Math.abs(x) || Math.abs(twist) < Math.abs(y)) {
			driver.driveXY(x*sensitivity, y*sensitivity);
		} else {
			driver.tankTurn(twist*sensitivity);
		}
		
		//Triggeren paa joysticket styrer solenoid'en til tandhjulene
		if(stick.getRawButton(1)) {
			actuator.set(Value.kForward);
		} else {
			actuator.set(Value.kReverse);
		}
		
		if(stick.getRawButton(2)) {
			// TODO opdater til drivePolar naar METODEN :) er faerdig implementeret
			driver.driveXY(0, sensitivity);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
	
	//-----------------------------------------------------------------------//
	
	/*
	 * Our custom methods go below / Vores egne metoder herunder:
	 */
	
	
	/**
	 * Opsaetter en simpel server via ServerSocket, som kan modtage og sende kommandoer,
	 * som kan behandles i koden. Dette aabner op for interaktion mellem RoboRio'en,
	 * som denne kode koerer paa, og andre enheder, der er opkoblet paa samme netvaerk.
	 * Bemaerk at hele serveren og al haandteringen af data og kommandoer foregaar
	 * paa en seperat traad.
	 * Bemaerk desuden at den nuvaerende implementering kun tillader en enkelt client.
	 * 
	 * @param port Porten som serveren aabnes for.
	 */
	public void setupServer(int port) {
		new Thread(() -> {
			try(
	                ServerSocket serverSocket = new ServerSocket(port);
	                Socket clientSocket = serverSocket.accept();
	                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        ) {
				WebServerHandler handler = new WebServerHandler();
				String input;
				while(clientSocket.isConnected() && !clientSocket.isClosed()) { //Mangler at blive testet - hvis det ikke virker, kan det erstattes med 'true'
					if((input = in.readLine()) != null)
						out.println(handler.handleInput(input));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
	
	
	/**
	 * Opsaetter et USB-tilsluttet kamera, og streamer dets input
	 * videre til visning paa FRC Driver Station, uden nogen billedbehandling.
	 * 
	 * @param index Index'et for det kamera, der skal opsaettes.
	 * @return True hvis opsaetningen lykkedes; ellers false.
	 */
	public boolean setupStreamingCamera(int index) {
		UsbCameraInfo[] detectedCameras = UsbCamera.enumerateUsbCameras();
		if(detectedCameras != null && detectedCameras.length > index) {
			System.out.println(detectedCameras[0].path);
			CameraServer.getInstance().startAutomaticCapture(detectedCameras[index].dev);
			return true;
		}
		
		return false;
	}
}
