package sonar.core.client.gui;

public interface IGuiOrigin {

	public void setOrigin(Object origin);
	
	public static <T extends IGuiOrigin> T withOrigin(T gui, Object origin) {
		gui.setOrigin(origin);
		return gui;
	}
	
}
