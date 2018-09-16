package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import sonar.core.api.energy.ISonarEnergyStorage;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.energy.InternalEnergyStorageWrapper;
import sonar.core.helpers.NBTHelper.SyncType;

import javax.annotation.Nullable;

public class SyncEnergyStorage extends DirtyPart implements ISonarEnergyStorage, INBTSerializable<NBTTagCompound>, ISyncPart {

	private long energy;
	private long capacity;
	private long maxReceive;
	private long maxExtract;
	private InternalEnergyStorageWrapper[] wrappers = new InternalEnergyStorageWrapper[7];

	private String tagName = "energyStorage";

	public SyncEnergyStorage(int capacity) {
		this(capacity, capacity, capacity);
	}

	public SyncEnergyStorage(int capacity, int maxTransfer) {
		this(capacity, maxTransfer, maxTransfer);
	}

	public SyncEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	public InternalEnergyStorageWrapper getOrCreateWrapper(@Nullable EnumFacing face){
		int id = face == null ? 6 : face.getIndex();
		if(wrappers[id] != null){
			return wrappers[id];
		}else{
			return wrappers[id] = new InternalEnergyStorageWrapper(this, face);
		}
	}

	public InternalEnergyStorageWrapper getInternalWrapper(){
		return getOrCreateWrapper(null);
	}

	public SyncEnergyStorage setCapacity(int capacity) {
		this.capacity = capacity;

		if (energy > capacity) {
			energy = capacity;
		}
		markChanged();
		return this;
	}

	public SyncEnergyStorage setMaxTransfer(int maxTransfer) {
		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
		return this;
	}

	public SyncEnergyStorage setMaxReceive(int maxReceive) {
		this.maxReceive = maxReceive;
		markChanged();
		return this;
	}

	public SyncEnergyStorage setMaxExtract(int maxExtract) {
		this.maxExtract = maxExtract;
		markChanged();
		return this;
	}

	public long getMaxReceive() {
		return maxReceive;
	}

	public long getMaxExtract() {
		return maxExtract;
	}

	public void setEnergyStored(long energy) {
		this.energy = energy;
		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
		this.markChanged();
	}

	public void modifyEnergyStored(long energy) {
		this.energy += energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
		this.markChanged();
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		if (energy < 0) {
			energy = 0;
		}
		buf.writeLong(energy);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.energy = buf.readLong();

		if (energy > capacity) {
			energy = capacity;
		}
	}

	public SyncEnergyStorage readFromNBT(NBTTagCompound nbt) {
		this.energy = nbt.getLong("Energy");

		if (energy > capacity) {
			energy = capacity;
		}
		return this;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (energy < 0) {
			energy = 0;
		}
		nbt.setLong("Energy", energy);
		return nbt;
	}

    @Override
	public final NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound energyTag = new NBTTagCompound();
		this.writeToNBT(energyTag);
		nbt.setTag(getTagName(), energyTag);
		return nbt;
	}

    @Override
	public final void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName())) {
			this.readFromNBT(nbt.getCompoundTag(getTagName()));
		}
	}

    @Override
	public String getTagName() {
		return tagName;
	}

	public SyncEnergyStorage setTagName(String tagName) {
		this.tagName = tagName;
		return this;
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE);
	}

	///// * SONAR *//////
    @Override
	public long addEnergy(long maxReceive, EnumFacing face, ActionType action) {
		long add = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!action.shouldSimulate()) {
			energy += add;
			this.markChanged();
		}
		return add;
	}

    @Override
	public long removeEnergy(long maxExtract, EnumFacing face, ActionType action) {
		long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!action.shouldSimulate()) {
			energy -= energyExtracted;
			this.markChanged();
		}
		return energyExtracted;
	}

	public boolean canExtract(EnumFacing face) {
		return true;
	}

	public boolean canReceive(EnumFacing face) {
		return true;
	}

    @Override
	public long getEnergyLevel() {
		return energy;
	}

    @Override
	public long getFullCapacity() {
		return capacity;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.readData(nbt, SyncType.SAVE);
	}
}