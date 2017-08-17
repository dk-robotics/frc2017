package org.usfirst.frc.team6678.robot.backgroundTasks;

import org.usfirst.frc.team6678.robot.Robot;

import edu.wpi.first.wpilibj.IterativeRobot;

import java.util.ArrayList;

/**
 * A convenient singleton class for handling {@link BackgroundTask}s automatically.
 * Simply invoke the {@link #handleBackgroundTask(BackgroundTask task)} to have the {@code task}
 * being handled automatically, ie to have its {@link BackgroundTask#loop()} method being called.
 * 
 * This does, however, require this singletons {@link #handle()} method to be called
 * centrally one time each time it should be looped. It would be natural to have this
 * method called from the {@link Robot#robotPeriodic()} method.
 */

public class BackgroundTaskHandler {
	
	private static ArrayList<BackgroundTask> tasks = new ArrayList<>();

	/**
	 * This constructor is being held private, as to prevent it from being called,
	 * since this class is designed as a singleton, where the same instance will be
	 * invoked every time one of its methods is being called.
	 */
	private BackgroundTaskHandler() {}
	
	
	/**
	 * Registers an instance of {@link BackgroundTask} to be handled.
	 * @param task The {@link BackgroundTask} to be handled.
	 */
	public synchronized static void handleBackgroundTask(BackgroundTask task) {
		tasks.add(task);
	}
	
	/**
	 * Deregisters an instance of {@link BackgroundTask} to be handled,
	 * ie makes sure it will not be handled any longer.
	 * @param task The {@link BackgroundTask} to deregister.
	 * @return Whether or not the {@code task} was successfully deregistered. 
	 */
	public synchronized static boolean unhandleBackgroundTask(BackgroundTask task) {
		return tasks.remove(task);
	}
	
	/**
	 * This method shall be called exactly one time every loop, to ensure
	 * that every registered {@link BackgroundTask} will be handled/looped.
	 * 
	 * This would most naturally be called from {@link IterativeRobot#robotPeriodic()}.
	 * 
	 * This method internally loops through all of the registered {@link BackgroundTask}s,
	 * and calls each of their respective {@link BackgroundTask#loop()} methods.
	 */
	public synchronized static void handle() {
		for(BackgroundTask bt : tasks)
			bt.loop();
	}
	
}
