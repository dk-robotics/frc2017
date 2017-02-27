package backgroundTasks;

import edu.wpi.first.wpilibj.Joystick;

public class ButtonSwitchState implements BackgroundTask {

	private boolean state, isDown;
	private final Joystick stick;
	private final int button;
	
	public ButtonSwitchState(Joystick stick, int button) {
		this.stick = stick;
		this.button = button;
		state = stick.getRawButton(button);
		isDown = state;
	}

	@Override
	public void loop() {
		boolean clicked = stick.getRawButton(button); 
		if(clicked != isDown) {
			isDown = clicked;
			if(clicked)
				state = !state;
		}
	}
	
	public boolean getState() {
		return state;
	}
	
	
	
}
