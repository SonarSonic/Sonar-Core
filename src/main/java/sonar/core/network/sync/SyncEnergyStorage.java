package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cofh.api.energy.EnergyStorage;

public class SyncEnergyStorage extends EnergyStorage implements ISyncPart {

	private String tagName = "energyStorage";
	private int lastEnergy = 0;

	public SyncEnergyStorage(int capacity) {
		super(capacity);
	}

	public SyncEnergyStorage(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}

	public SyncEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
	}

	@Override
	public boolean equal() {
		return this.getEnergyStored() == lastEnergy;
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
		if (type == SyncType.SYNC) {
			if (!equal()) {
				this.writeToNBT(energyTag);
				lastEnergy = this.getEnergyStored();
			}
		} else if (type == SyncType.SAVE) {
			this.writeToNBT(energyTag);
			lastEnergy = this.getEnergyStored();
		}
		if (!energyTag.hasNoTags())
			nbt.setTag(getTagName(), energyTag);
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
		return sync == SyncType.SYNC || sync == SyncType.SAVE;
	}
	
}
