package sonar.core.api.energy;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.ActionType;

import javax.annotation.Nullable;

public interface ISonarEnergyStorage {
	
    long addEnergy(long maxReceive, @Nullable EnumFacing face, ActionType action);

    long removeEnergy(long maxExtract, @Nullable EnumFacing face, ActionType action);

    long getEnergyLevel();

    long getFullCapacity();
}
