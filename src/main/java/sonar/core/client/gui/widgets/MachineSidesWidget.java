package sonar.core.client.gui.widgets;

import net.minecraft.util.ResourceLocation;
import sonar.core.utils.MachineSides;

/**WIP, may never be finished*/
public class MachineSidesWidget extends SonarWidget {

	public final MachineSides sides;
	public final ResourceLocation[] icons;

	public MachineSidesWidget(MachineSides sides, ResourceLocation[] icons, int left, int top, int width, int height) {
		super(left, top, width, height);
		this.sides = sides;
		this.icons = icons;
	}

}
