package sonar.core.utils;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTPacket {

	public void writePacket(NBTTagCompound tag, int id);	

	public void readPacket(NBTTagCompound tag, int id);
}
