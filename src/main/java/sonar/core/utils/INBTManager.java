package sonar.core.utils;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTManager<T extends INBTObject> {
	public T readFromNBT(NBTTagCompound tag);

	public void writeToNBT(NBTTagCompound tag, T object);
	
	public boolean areTypesEqual(T target, T current);
}
