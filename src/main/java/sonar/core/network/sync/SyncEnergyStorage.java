package sonar.core.network.sync;

import cofh.api.energy.IEnergyStorage;
import io.netty.buffer.ByteBuf;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.ISonarEnergyStorage;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.NBTHelper.SyncType;

@Optional.InterfaceList({ @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"), @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"), @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla") })
public class SyncEnergyStorage extends DirtyPart implements ISonarEnergyStorage, net.minecraftforge.energy.IEnergyStorage, IEnergyStorage, ISyncPart, ITeslaConsumer, ITeslaProducer, ITeslaHolder, INBTSerializable<NBTTagCompound> {

	private long energy;
	private long capacity;
	private long maxReceive;
	private long maxExtract;

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

	public void setEnergyStored(int energy) {
		this.energy = energy;
		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
		this.markChanged();
	}

	public void modifyEnergyStored(int energy) {
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

	public final NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound energyTag = new NBTTagCompound();
		this.writeToNBT(energyTag);
		nbt.setTag(getTagName(), energyTag);
		return nbt;

	}

	public final void readData(NBTTagCompound nbt, SyncType type) {
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
		return sync.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE);
	}

	///// * SONAR *//////
	public long addEnergy(long maxReceive, ActionType action) {
		long add = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));

		if (!action.shouldSimulate()) {
			energy += add;
			this.markChanged();
		}
		return add;
	}

	public long removeEnergy(long maxExtract, ActionType action) {

		long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

		if (!action.shouldSimulate()) {
			energy -= energyExtracted;
			this.markChanged();
		}
		return energyExtracted;
	}

	public long getEnergyLevel() {
		return energy;
	}

	public long getFullCapacity() {
		return capacity;
	}

	///// * CoFH *//////
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return (int) addEnergy(maxReceive, ActionType.getTypeForAction(simulate));
	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		return (int) removeEnergy(maxExtract, ActionType.getTypeForAction(simulate));
	}

	public int getEnergyStored() {
		return (int) Math.min(getEnergyLevel(), Integer.MAX_VALUE);
	}

	public int getMaxEnergyStored() {
		return (int) Math.min(getFullCapacity(), Integer.MAX_VALUE);
	}

	///// * TESLA *//////
	@Override
	public long getStoredPower() {
		return getEnergyStored();
	}

	@Override
	public long getCapacity() {
		return getMaxEnergyStored();
	}

	@Override
	public long takePower(long power, boolean simulated) {
		return removeEnergy(Math.min(Integer.MAX_VALUE, power), ActionType.getTypeForAction(simulated));
	}

	@Override
	public long givePower(long power, boolean simulated) {
		return addEnergy(power, ActionType.getTypeForAction(simulated));
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.readData(nbt, SyncType.SAVE);
	}

	@Override
	public boolean canExtract() {
		return true;// may need to link to something else
	}

	@Override
	public boolean canReceive() {
		return true;// may need to link to something else
	}
}