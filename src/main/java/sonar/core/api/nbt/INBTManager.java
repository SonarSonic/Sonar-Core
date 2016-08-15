package sonar.core.api.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTManager<T extends INBTObject> {
	public T readFromNBT(NBTTagCompound tag);

	public NBTTagCompound writeToNBT(NBTTagCompound tag, T object);
	
	public boolean areTypesEqual(T target, T current);
}
