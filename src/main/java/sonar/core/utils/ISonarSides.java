package sonar.core.utils;

import net.minecraft.util.EnumFacing;


public interface ISonarSides {

	boolean decrSide(EnumFacing side);

	boolean incrSide(EnumFacing side);

	boolean setSide(EnumFacing side, MachineSide config);

	boolean resetSides();

	MachineSide getSideConfig(EnumFacing side);

}
