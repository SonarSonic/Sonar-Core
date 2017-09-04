package sonar.core.network.sync2;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncableListener;

import java.lang.reflect.Field;

public interface ISyncHandler<T> {

    boolean canHandle(Class<?> clazz);

    void writeToNBT(ISyncableListener syncable, Field field, T obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException;

    void readFromNBT(ISyncableListener syncable, Field field, T obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException;

}