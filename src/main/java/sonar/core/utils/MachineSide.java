package sonar.core.utils;

import net.minecraft.util.IStringSerializable;

public enum MachineSide implements IStringSerializable {

	INPUT, INPUT_ANIMATE, OUTPUT, OUTPUT_ANIMATE, NONE;

	public boolean isInput() {
		return this == INPUT || this == INPUT_ANIMATE;
	}

	public boolean isOutput() {
		return this == OUTPUT || this == OUTPUT_ANIMATE;
	}

	public boolean isAnimated() {
		return this == INPUT_ANIMATE || this == OUTPUT_ANIMATE;
	}

	public MachineSide getAnimated() {
		switch (this) {
		case INPUT:
			return INPUT_ANIMATE;
		case OUTPUT:
			return OUTPUT_ANIMATE;
		default:
			return this;
		}
	}

	public MachineSide increase() {
		if (isInput()) {
			return OUTPUT;
		}
		if (isOutput()) {
			return INPUT;
		}
		return NONE;
	}

	public MachineSide decrease() {
		if (isInput()) {
			return OUTPUT;
		}
		if (isOutput()) {
			return INPUT;
		}
		return NONE;
	}

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}

}
