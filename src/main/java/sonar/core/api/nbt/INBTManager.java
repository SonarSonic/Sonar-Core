package sonar.core.api.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTManager<T extends INBTObject> {
    T readFromNBT(NBTTagCompound tag);

    NBTTagCompound writeToNBT(NBTTagCompound tag, T object);
	
    boolean areTypesEqual(T target, T current);
}
