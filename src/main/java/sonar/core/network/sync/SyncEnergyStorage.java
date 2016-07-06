package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import sonar.core.helpers.NBTHelper.SyncType;
import cofh.api.energy.EnergyStorage;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({		
	@Optional.Interface (iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "Tesla"),
	@Optional.Interface (iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "Tesla"),
	@Optional.Interface (iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "Tesla")})				
public class SyncEnergyStorage extends EnergyStorage implements ISyncPart, ITeslaConsumer, ITeslaProducer, ITeslaHolder, INBTSerializable<NBTTagCompound> {

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
		this.setChanged(true);
	}

	public void modifyEnergyStored(int energy) {
		super.modifyEnergyStored(energy);
		this.setChanged(true);
	}

	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!simulate) {
			this.setChanged(true);
		}
		return super.receiveEnergy(maxReceive, simulate);

	}

	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!simulate) {
			this.setChanged(true);
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
		/*NBTTagCompound energyTag = new NBTTagCompound(); if (type.isType(SyncType.DEFAULT_SYNC)) { if (type.mustSync() || !equal()) { this.writeToNBT(energyTag); lastEnergy = this.getEnergyStored(); } } else if (type == SyncType.SAVE) { this.writeToNBT(energyTag); lastEnergy = this.getEnergyStored(); } if (!energyTag.hasNoTags()) nbt.setTag(getTagName(), energyTag); */
		NBTTagCompound energyTag = new NBTTagCompound();
		this.writeToNBT(energyTag);
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
		return sync.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE);
	}

	@Override
	public void setChanged(boolean set) {
		hasChanged = set;
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	//TESLA
	@Override
	public long getStoredPower() {
		return this.getEnergyStored();
	}

	@Override
	public long getCapacity() {
		return this.getMaxEnergyStored();
	}

	@Override
	public long takePower(long power, boolean simulated) {
		return extractEnergy((int) Math.min(Integer.MAX_VALUE, power), simulated);
	}

	@Override
	public long givePower(long power, boolean simulated) {
		return receiveEnergy((int) Math.min(Integer.MAX_VALUE, power), simulated);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt, SyncType.SAVE);		
	}
}