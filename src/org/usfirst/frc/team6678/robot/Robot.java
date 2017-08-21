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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.usfirst.frc.team6678.robot.autonomous.AutonomousHandler;
import org.usfirst.frc.team6678.robot.backgroundTasks.BackgroundTaskHandler;
import org.usfirst.frc.team6678.robot.backgroundTasks.ButtonSwitchState;
import org.usfirst.frc.team6678.robot.backgroundTasks.UltraSonicDistanceSensor;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	private Timer timer = new Timer();
	private Joystick stick = new Joystick(0);
	private Driving driving = new Driving(stick);
	private AutonomousHandler autonomous = new AutonomousHandler(timer, driving.driver);
	
	private Compressor compressor = new Compressor(0);
	private DoubleSolenoid actuator = new DoubleSolenoid(0, 1);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		Log.message("Robot", "Robot Init started");

		//Saetter kompressoren til at automatisk koere naar noedvendigt
		compressor.setClosedLoopControl(true);

		for(int i = 0; i < 10; i++)
			setupStreamingCamera(i);
		setupServer(4444);

		Log.info("Robot", "Robot Init finish");
	}

	
	/**
	 * This function is called periodically while the robot is enabled
	 * Code that shall be run in all modes, e.g. {@link #autonomousPeriodic()}, {@link #teleopPeriodic()},
	 * {@link #testPeriodic()} and {@link #disabledPeriodic()}, can be placed here,
	 * instead of providing the same code in every of the aforementioned methods. 
	 */
	@Override
	public void robotPeriodic() {
		BackgroundTaskHandler.handle();
	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {
	    Log.message("Robot", "Autonomous Init started");
		timer.reset();
		timer.start();
		autonomous.init();
		Log.info("Robot", "Autonomous Init finished");
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() { //Skal laves fuldkommen om fra bunden...
		autonomous.loop();
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
		//Triggeren paa joysticket styrer solenoid'en til tandhjulene
		if(stick.getRawButton(1)) {
			actuator.set(Value.kForward);
		} else {
			actuator.set(Value.kReverse);
		}

		/*
		* The device shuts down outputs when the voltage goes below 6.8V.
		* Device blackout should happen at about 4.5V, but might happen earlier.
		* See https://wpilib.screenstepslive.com/s/4485/m/24166/l/289498 for more details.
		* 7.5V is chosen to have a relatively large safety margin, but it could be lowered a bit.
		*/
		double voltage = DriverStation.getInstance().getBatteryVoltage();
		Log.debug("Voltage", voltage + " V");
		if(voltage >= 7.5) {
			driving.loop();
		} else {
			driving.driver.stopMotors();
			Log.warn("Voltage", "Voltage is critically low! " + voltage + "V");
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
	private void setupServer(int port) {
		new Thread(() -> {
			try(	//Auto-closing objects in the new try-catch-statement
	                ServerSocket serverSocket = new ServerSocket(port);
	                Socket clientSocket = serverSocket.accept();
	                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        ) {
				WebServerHandler handler = new WebServerHandler();
				String input;
				while(clientSocket.isConnected() && !clientSocket.isClosed()) {
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
	private boolean setupStreamingCamera(int index) {
		UsbCameraInfo[] detectedCameras = UsbCamera.enumerateUsbCameras();
		if(detectedCameras != null && detectedCameras.length > index) {
			System.out.println(detectedCameras[index].path);
			CameraServer.getInstance().startAutomaticCapture(detectedCameras[index].dev);
			return true;
		}
		
		return false;
	}
}
