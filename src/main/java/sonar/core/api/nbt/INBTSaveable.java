package sonar.core.api.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSaveable {
	
    void writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);
}
