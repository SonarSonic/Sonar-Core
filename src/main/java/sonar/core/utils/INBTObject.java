package sonar.core.utils;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTObject<T> extends IRegistryObject, INBTSaveable {

	public String getName();

	public T instance();
}
