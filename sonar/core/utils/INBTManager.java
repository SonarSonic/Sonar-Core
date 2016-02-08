package sonar.core.utils;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTHelper;

public interface INBTManager<T extends INBTObject> {
	public T readFromNBT(NBTTagCompound tag);

	public void writeToNBT(NBTTagCompound tag, T object);
	
	public boolean areTypesEqual(T target, T current);
}
