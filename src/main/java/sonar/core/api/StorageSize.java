package sonar.core.api;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public class StorageSize implements INBTSyncable {

	public static final StorageSize EMPTY = new StorageSize(0, 0);

	private long stored, max;

	public StorageSize(long stored, long max) {
		this.stored = stored;
		this.max = max;
	}

	public long getStored() {
		return stored;
	}

	public long getMaxStored() {
		return max;
	}

	public void add(long add) {
		stored += add;
	}

	public void addToMax(long add) {
		max += add;
	}

	public void add(StorageSize size) {
		stored += size.stored;
		max += size.max;

	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		stored = nbt.getLong("stored");
		max = nbt.getLong("max");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setLong("stored", stored);
		nbt.setLong("max", max);
		return nbt;
	}
}