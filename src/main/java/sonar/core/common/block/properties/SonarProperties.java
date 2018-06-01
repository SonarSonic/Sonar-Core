package sonar.core.common.block.properties;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

public class SonarProperties {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyDirection ORIENTATION = PropertyDirection.create("facing");
	public static final PropertyDirection ROTATION = PropertyDirection.create("rotation");
}
