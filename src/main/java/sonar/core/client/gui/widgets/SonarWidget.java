package sonar.core.client.gui.widgets;

import java.awt.Rectangle;

import sonar.core.client.gui.GuiSonarWidget;
import sonar.core.client.gui.GuiSonarWidget.WidgetPos;

/**WIP, may never be finished*/
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

	@Override
	public void setGui(GuiSonarWidget gui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPosition(WidgetPos pos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClicked(int x, int y, int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocused(boolean bool) {
		// TODO Auto-generated method stub
		
	}

}
