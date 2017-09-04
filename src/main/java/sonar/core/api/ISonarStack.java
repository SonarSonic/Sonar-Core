package sonar.core.api;

import sonar.core.api.nbt.INBTSyncable;

public interface ISonarStack<T extends ISonarStack> extends INBTSyncable  {

    enum StorageTypes {
        ITEMS, ENERGY, FLUIDS
	}

    StorageTypes getStorageType();

    T copy();

    void add(T stack);

    void remove(T stack);

    T setStackSize(long size);

    long getStackSize();
}
