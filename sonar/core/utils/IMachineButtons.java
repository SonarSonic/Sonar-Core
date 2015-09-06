package sonar.core.utils;

public interface IMachineButtons {
	/**
	 * @param buttonID ID of the button pressed
	 * @param value additional integer if required
	 */
	public void buttonPress(int buttonID, int value);
}
