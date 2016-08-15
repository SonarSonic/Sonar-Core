package sonar.core.client.gui.widgets;

import org.lwjgl.input.Mouse;

public class SonarScroller {

	public float currentScroll;
	public boolean isScrolling;
	public boolean wasClicking;
	public int left, start, end, width;

	public SonarScroller(int scrollerLeft, int scrollerStart, int length, int width) {
		this.left = scrollerLeft;
		this.start = scrollerStart;
		this.end = scrollerStart + length;
		this.width = width;
	}

	public float getCurrentScroll() {
		return currentScroll;
	}

	public void handleMouse(boolean needsScrollBars, int listSize) {
		float lastScroll = currentScroll;
		int i = Mouse.getEventDWheel();

		if (i != 0 && needsScrollBars) {
			int j = listSize + 1;
			if (i > 0)
				i = 1;
			if (i < 0)
				i = -1;
			currentScroll = (float) ((double) currentScroll - (double) i / (double) j);
			if (currentScroll < 0.0F)
				currentScroll = 0.0F;
			if (currentScroll > 1.0F)
				currentScroll = 1.0F;
		}
	}
	
	public void drawScreen(int x, int y, boolean needsScrollBars){
		float lastScroll = currentScroll;
		boolean flag = Mouse.isButtonDown(0);

		if (!this.wasClicking && flag && x >= left && y >= start && x < left + width && y < end) {
			this.isScrolling = needsScrollBars;
		}
		if (!flag) {
			this.isScrolling = false;
		}
		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = ((float) (y - start) - 7.5F) / ((float) (end - start) - 15.0F);

			if (currentScroll < 0.0F)
				currentScroll = 0.0F;
			if (currentScroll > 1.0F)
				currentScroll = 1.0F;
		}
	}
}
