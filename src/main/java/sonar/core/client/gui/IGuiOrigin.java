package sonar.core.client.gui;

public interface IGuiOrigin {

	void setOrigin(Object origin);
	
	static <T extends IGuiOrigin> T withOrigin(T gui, Object origin) {
		gui.setOrigin(origin);
		return gui;
	}
	
}
