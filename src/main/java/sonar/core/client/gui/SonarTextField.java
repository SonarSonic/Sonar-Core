package sonar.core.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class SonarTextField extends GuiTextField {

	private String defString = "";
    private boolean digitsOnly;
	private int outlineColour = -6250336, boxColour = -16777216;
	
	public SonarTextField(int id, FontRenderer renderer, int x, int y, int width, int height) {
		super(id, renderer, x, y, width, height);
	}

	public void setDefault(String defString) {
		this.defString = defString;
	}

	public SonarTextField setDigitsOnly(boolean digitsOnly) {
		this.digitsOnly = digitsOnly;
		return this;
	}

	public SonarTextField setBoxOutlineColour(int outlineColour) {
		this.outlineColour = outlineColour;
		return this;
	}

	public SonarTextField setBoxColour(int boxColour) {
		this.boxColour = boxColour;
		return this;
	}
	
    @Override
	public boolean textboxKeyTyped(char c, int i) {
		if (digitsOnly) {
			switch (c) {
			case '\001':
				return super.textboxKeyTyped(c, i);
			case '\003':
				return super.textboxKeyTyped(c, i);
			case '\026':
				return false;
			case '\030':
				return super.textboxKeyTyped(c, i);
			}
			switch (i) {
			case 14:
				return super.textboxKeyTyped(c, i);
			case 199:
				return super.textboxKeyTyped(c, i);
			case 203:
				return super.textboxKeyTyped(c, i);
			case 205:
				return super.textboxKeyTyped(c, i);
			case 207:
				return super.textboxKeyTyped(c, i);
			case 211:
				return super.textboxKeyTyped(c, i);
			}
            return Character.isDigit(c) && super.textboxKeyTyped(c, i);
		}
		return super.textboxKeyTyped(c, i);
	}

	public int getIntegerFromText() {
		return Integer.valueOf(getText().isEmpty() ? "0" : getText());
	}

	public long getLongFromText() {
		return Long.valueOf(getText().isEmpty() ? "0" : getText());
	}

    @Override
	public void drawTextBox() {
		this.setEnableBackgroundDrawing(true);
		if (this.getVisible()) {
			if (this.getEnableBackgroundDrawing()) {
                drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, outlineColour);
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, boxColour);
			}
		}
		this.setEnableBackgroundDrawing(false);
        x += 4;
        this.y += (this.height - 8) / 2;
		super.drawTextBox();
        x -= 4;
        this.y -= (this.height - 8) / 2;
	}
}
