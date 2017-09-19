package sonar.core.utils;

import net.minecraft.util.math.AxisAlignedBB;

/**
 * just for identifying what axis aligned BB it is, from a ray trace.
 */
public class LabelledAxisAlignedBB extends AxisAlignedBB {
    public String label;

	public LabelledAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
		super(x1, y1, z1, x2, y2, z2);
	}

	public LabelledAxisAlignedBB(String label, AxisAlignedBB axis) {
		this(axis.minX, axis.minY, axis.minZ, axis.maxX, axis.maxY, axis.maxZ);
		this.label = label;
	}

	public LabelledAxisAlignedBB labelAxis(String label) {
		this.label = label;
		return this;
	}
}
