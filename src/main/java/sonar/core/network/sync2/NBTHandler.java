package sonar.core.network.sync2;

import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncableListener;

public class NBTHandler implements ISyncHandler {

	@Override
	public boolean canHandle(Class clazz) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void writeToNBT(ISyncableListener syncable, Field field, Object obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException {
		field.set(syncable, nbt.getTag(tagName));
		
	}

	@Override
	public void readFromNBT(ISyncableListener syncable, Field field, Object obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException {
		NBTTagCompound tag = (NBTTagCompound) field.get(syncable);
		nbt.setTag(tagName, tag);
		
	}

}
