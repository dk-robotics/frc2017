package org.usfirst.frc.team6678.robot;

public class WebServerHandler {
	
	/**
	 * Denne klasse er tiltaenkt at opretholde samt kontrollere al kommunikation
	 * mellem denne kode paa robottens centrale enhed (RoboRIO) og eventuelle
	 * opkoblede enheder, saasom Raspberry Pi's og lignende...
	 */
	
	public String handleInput(String input) {
		String output = "";
		
		//TO DO: Implementer noget reel kode...
		output = "Svar fra robot: " + input;
		
		return output;
	}
	
}
