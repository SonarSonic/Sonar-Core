package sonar.core.network.sync2;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncableListener;

import java.lang.reflect.Field;

public class NullHandler implements ISyncHandler {

    @Override
    public boolean canHandle(Class clazz) {
        return false;
    }

    @Override
    public void writeToNBT(ISyncableListener syncable, Field field, Object obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readFromNBT(ISyncableListener syncable, Field field, Object obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException {
        // TODO Auto-generated method stub

    }

}