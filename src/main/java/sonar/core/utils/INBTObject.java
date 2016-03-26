package sonar.core.utils;

import sonar.core.api.IRegistryObject;
import net.minecraft.nbt.NBTTagCompound;

public interface INBTObject<T> extends IRegistryObject {

	public String getName();
	
	public void writeToNBT(NBTTagCompound tag);

	public void readFromNBT(NBTTagCompound tag);
	
	public T instance();
}
