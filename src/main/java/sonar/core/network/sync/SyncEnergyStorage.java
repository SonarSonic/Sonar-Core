package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;
import cofh.api.energy.EnergyStorage;

public class SyncEnergyStorage extends EnergyStorage implements ISyncPart {

	private String tagName = "energyStorage";
	private boolean hasChanged;

	public SyncEnergyStorage(int capacity) {
		super(capacity);
	}

	public SyncEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}

	public SyncEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
	}

	public void setEnergyStored(int energy) {
		super.setEnergyStored(energy);
		hasChanged = true;
	}

	public void modifyEnergyStored(int energy) {
		super.modifyEnergyStored(energy);
		hasChanged = true;
	}

	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!simulate) {
			hasChanged = true;
		}
		return super.receiveEnergy(maxReceive, simulate);

	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!simulate) {
			hasChanged = true;
		}
		return super.extractEnergy(maxExtract, simulate);
	}
	
	
	@Override
	public void writeToBuf(ByteBuf buf) {
		if (energy < 0) {
			energy = 0;
		}
		buf.writeInt(energy);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.energy = buf.readInt();

		if (energy > capacity) {
			energy = capacity;
		}
	}

	public final void writeToNBT(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound energyTag = new NBTTagCompound();
		this.writeToNBT(energyTag);
		nbt.setTag(getTagName(), energyTag);
		hasChanged = false;
	}

	public final void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName())) {
			this.readFromNBT(nbt.getCompoundTag(getTagName()));
		}
	}

	public String getTagName() {
		return tagName;
	}

	public SyncEnergyStorage setTagName(String tagName) {
		this.tagName = tagName;
		return this;
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync.isType(SyncType.SYNC, SyncType.SAVE);
	}

	@Override
	public boolean equal() {
		return hasChanged;
	}

}