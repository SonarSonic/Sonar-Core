package sonar.core.client.gui.widgets;

import java.awt.Rectangle;
import java.io.IOException;

import sonar.core.client.gui.GuiSonar;
import sonar.core.client.gui.GuiSonarWidget;
import sonar.core.client.gui.GuiSonarWidget.WidgetPos;

public interface IWidget {

	public void setGui(GuiSonarWidget gui);
	
	public void setPosition(WidgetPos pos);
	
	public void drawForegroundLayer(int x, int y);

	public void drawBackgroundLayer(float val, int x, int y);

	public void onClicked(int x, int y, int button);

	public void handleMouseInput();

	public void keyTyped(char c, int i);

	public void setFocused(boolean bool);
	
	public boolean isFocused();

	public Rectangle getSizing();
	
		
}
