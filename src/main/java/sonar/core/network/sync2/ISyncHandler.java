package sonar.core.network.sync2;

import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncableListener;

public interface ISyncHandler<T> {

	public boolean canHandle(Class<?> clazz);

	public void writeToNBT(ISyncableListener syncable, Field field, T obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException;
	
	public void readFromNBT(ISyncableListener syncable, Field field, T obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException;

}
