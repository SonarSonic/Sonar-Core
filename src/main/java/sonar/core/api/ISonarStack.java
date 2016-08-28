package sonar.core.api;

import sonar.core.api.nbt.INBTSyncable;

public interface ISonarStack<T extends ISonarStack> extends INBTSyncable  {

	public enum StorageTypes {
		ITEMS, ENERGY, FLUIDS;
	}

	public StorageTypes getStorageType();

	public T copy();

	public void add(T stack);

	public void remove(T stack);

	public T setStackSize(long size);
	
	public long getStackSize();

}
