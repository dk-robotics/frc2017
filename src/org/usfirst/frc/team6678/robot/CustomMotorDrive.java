package org.usfirst.frc.team6678.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

/**
 * A low level class for motor driving mechanics
 */
public class CustomMotorDrive {

	public enum Direction {
		LEFT,
		RIGHT,
	}
	
	private Talon leftMotor0, leftMotor1, rightMotor0, rightMotor1;
	private boolean invertRightMotors = false, invertLeftMotors = false;

    // Acceleration for left and right motor
    private long lastAccelerationTimestamp = -1;
    final private double acceleration = 0.08; // Percentage acceleration
    private double acceleratedLeftMotor = 0;
    private double acceleratedRightMotor = 0;

	/**
	 * Instantierer klassen med referencer til de fire PWM-porte, som de fire motorer,
	 * der skal kontrolleres og styres fra denne klasse, er kablet til.
	 * Denne klasse bruger pt {@link Talon}-motorcontrollere til at styre motorerne.
	 * Disse er ogsaa kompatible med SPARK motorcontrollere.
	 * 
	 * @param lefMotor0Port Porten til den ene motor i venstre side.
	 * @param lefMotor1Port Porten til den anden motor i venstre side.
	 * @param rightMotor0Port Porten til den ene motor i hoejre side.
	 * @param rightMotor1Port Porten til den anden motor i hoejre side.
	 */
	public CustomMotorDrive(int lefMotor0Port, int lefMotor1Port, int rightMotor0Port, int rightMotor1Port) {
		Log.message("CustomMotorDrive", "constructor initializing");
		leftMotor0 = new Talon(lefMotor0Port);
		leftMotor1 = new Talon(lefMotor1Port);
		rightMotor0 = new Talon(rightMotor0Port);
		rightMotor1 = new Talon(rightMotor1Port);
	}
	
	/**
	 * Inverter rotationsretningen for de venstre motorer.
	 * 
	 * @param invert Hvorvidt retningen skal inverteres. True for ja, false for nej.
	 */
	public void invertLeftMotors(boolean invert) {
		invertLeftMotors = invert;
	}
	
	/**
	 * Inverter rotationsretningen for de hoejre motorer.
	 * 
	 * @param invert Hvorvidt retningen skal inverteres. True for ja, false for nej.
	 */
	public void invertRightMotors(boolean invert) {
		invertRightMotors = invert;
	}
	
	public void stopMotors() {
		Log.message("CustomMotorDrive", "Stop motors");
		acceleratedLeftMotor = 0;
		acceleratedRightMotor = 0;
		lastAccelerationTimestamp = -1;
		leftMotor0.set(0);
		leftMotor1.set(0);
		rightMotor0.set(0);
		rightMotor1.set(0);
	}
	
	public void alignAccelerationValues() {
		acceleratedLeftMotor = acceleratedRightMotor;
	}
	
	/**
	 * Koerer robotten som angivet i de to parametre herunder. Metoden styrer de motorcontrollere,
	 * der parvist driver to hjul/hjulsaet. Bemaerk hvordan denne klasse antager at to motorer
	 * er sammenkoblet (og med samme rotationsretning) for hvert tandhjul/hulsaet.
	 * 
	 * @param leftMotorPower Hastigheden for de hoejre motorer,
	 * fra 1 (fuld fart fremad) til -1 (fuld fart bagud).
	 * 
	 * @param rightMotorPower Hastigheden for de venstre motorer,
	 * fra 1 (fuld fart fremad) til -1 (fuld fart bagud).
	 */
	public void driveTank(double leftMotorPower, double rightMotorPower) {
	    Log.info("CustomMotorDrive", "Drive tank, inputs left: "+leftMotorPower+" right: "+rightMotorPower);
		if(leftMotorPower > 1) leftMotorPower = 1;
		else if(leftMotorPower < -1) leftMotorPower = -1;
		if(rightMotorPower > 1) rightMotorPower = 1;
		else if(rightMotorPower < -1) rightMotorPower = -1;

		updateAcceleration(leftMotorPower, rightMotorPower);
		
		leftMotor0.set((invertLeftMotors ? -acceleratedLeftMotor : acceleratedLeftMotor));
		leftMotor1.set((invertLeftMotors ? -acceleratedLeftMotor : acceleratedLeftMotor));
		rightMotor0.set((invertRightMotors ? -acceleratedRightMotor : acceleratedRightMotor));
		rightMotor1.set((invertRightMotors ? -acceleratedRightMotor : acceleratedRightMotor));
	}
	
	/**
	 * Koerer robotten fremad eller baglaens som om den er en bil. Dette skal forstaaes som,
	 * at motorerne i begge sider som udganspunkt koerer lige hurtigt, men ved at justere
	 * vaerdien {@param x} kan den ene af motorernes hastighed nedsaettes. Dog vil begge sider altid
	 * enten koere i samme retning eller slet ikke koere - de to motorer kan altsaa ikke i denne
	 * metode koere hver sin vej.
	 * 
	 * @param x Hvor meget robotten skal dreje. [-1;0[ drejer robotten mod venstre
	 * og ]0;1] drejer robotten mod hoejre. Hvis {@param x} er lig 0, koerer robotten ligeud.
	 * @param y Robottens hastighed fra -1 (bak) til 1 (fremad).
	 */
	public void driveXY(double x, double y) {
	    Log.debug("CustomMotorDrive", String.format("driveXY, x: %s y: %s", x, y));
		double leftPower, rightPower;
		leftPower = rightPower = y;
		
		if(x < 0) {
			leftPower += x*Math.signum(y);
		} else if(x > 0) {
			rightPower -= x*Math.signum(y);
		}
		
		driveTank(leftPower, rightPower);
	}
	
	/**
	 * Koerer robotten fremad eller baglaens som om den er en bil. Dette skal forstaaes som,
	 * at motorerne i begge sider som udganspunkt koerer lige hurtigt, men ved at justere
	 * vaerdien {@param x} kan den ene af motorernes hastighed nedsaettes. Dog vil begge sider altid
	 * enten koere i samme retning eller slet ikke koere - de to motorer kan altsaa ikke i denne
	 * metode koere hver sin vej.
	 * 
	 * Denne metode kalder internt {@code driveXY(joystick.getX(), joystick.getY());}.
	 * 
	 * @param joystick Det {@link Joystick} som koden faar vaerdierne fra.
	 */
	public void driveXY(Joystick joystick) {
		//De returnerede vaerdier fra getX() og getY() skal valideres!
		driveXY(joystick.getX(), joystick.getY());
	}
	
	/**
	 * Endnu en metode til at koere robotten 'som en bil'. Denne metode tager
	 * polaere koordinater som input - altsaa en vinkel og en radius/laengde,
	 * hvor sidstnaevnte oversaettes til hastigheden.
	 * 
	 * Denne metode kalder internt {@link #driveXY(Joystick)}, hvor de polare
	 * koordinater oversættes til 'normale' kartesiske koordinater, via
	 * {@code driveXY(Math.cos(v)*r, Math.sin(v)*r);}.
	 * 
	 * Dermed er der reelt ikke den store forskel på denne metode
	 * og {@link #driveXY(Joystick)}, men denne metode er lidt langsommere,
	 * da den benytter trigonometrisk funktioner, ud over at kalde endnu en metode.
	 * 
	 * @param v Vinklen som robotten skal dreje med i radianer!
	 * @param r Hastigheden som robotten skal koere med, fra -1 til 1 (selvom det
	 * giver mest mening mellem 0 og 1).
	 */
	public void drivePolar(double v, double r) {
		driveXY(Math.cos(v)*r, Math.sin(v)*r);
	}
	
	/**
	 * Drej robotten paa stedet, ved at koere begge motorer med samme hastighed,
	 * men i modsatrettede retninger.
	 * 
	 * @param speed Hastigheden paa motorerne fra 0 til 1.
	 * @param direction Om robotten skal dreje mod hoejre eller mod venstre.
	 */
	public void tankTurn(double speed, Direction direction) {
		switch (direction) {
			case LEFT:
				driveTank(-speed, speed);
				break;
			case RIGHT:
				driveTank(speed, -speed);
				break;
		}
	}
	
	/**
	 * Drej robotten paa stedet, ved at koere begge motorer med samme hastighed,
	 * men i modsatrettede retninger.
	 * 
	 * @param speed Hastigheden hvormed motorerne koerer. [-1;0[ er mod venstre,
	 * ]0;1] er mod hoejre og 0 er ingen bevaegelse.
	 */
	public void tankTurn(double speed) {
		driveTank(speed, -speed);
	}

    /**
     * Updates the acceleration variables according to the motor speeds
     * @param leftSpeed The speed for the left motor, value from -1 to 1
     * @param rightSpeed The speed for the right motor, value from -1 to 1
     */
	private void updateAcceleration(double leftSpeed, double rightSpeed) {
		if(lastAccelerationTimestamp < 0)
			lastAccelerationTimestamp = System.currentTimeMillis();
        double timeDelta = (System.currentTimeMillis() - lastAccelerationTimestamp) / 20d;
        lastAccelerationTimestamp = System.currentTimeMillis();
        acceleratedLeftMotor += acceleration * (leftSpeed - acceleratedLeftMotor) * timeDelta;
        acceleratedRightMotor += acceleration * (rightSpeed - acceleratedRightMotor) * timeDelta;
        Log.info("CustomMotorDrive", String.format("Updated acceleration, accelerated values - left: %s right: %s",
                acceleratedLeftMotor, acceleratedRightMotor));
    }
	
}
