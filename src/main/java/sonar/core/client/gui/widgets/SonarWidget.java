package sonar.core.client.gui.widgets;

import java.awt.Rectangle;

import sonar.core.client.gui.GuiSonar;

public class SonarWidget implements IWidget {

	public boolean isFocused = false;
	public Rectangle sizing = getSizing();
		
	public SonarWidget(int left, int top, int width, int height){
		sizing = new Rectangle(left, top, width, height);
	}	
	
	@Override
	public void drawForegroundLayer(int x, int y) {}

	@Override
	public void drawBackgroundLayer(float val, int x, int y) {}

	@Override
	public void onClicked(GuiSonar sonar, int x, int y) {}

	@Override
	public void handleMouseInput() {}

	@Override
	public void keyTyped(char c, int i) {}

	@Override
	public boolean isFocused() {
		return isFocused;
	}

	@Override
	public Rectangle getSizing() {
		return sizing;
	}

}
