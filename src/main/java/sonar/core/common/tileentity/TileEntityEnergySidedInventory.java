package sonar.core.common.tileentity;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.integration.EUHelper;
import sonar.core.integration.SonarLoader;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.core.network.sync.SyncSidedEnergyStorage;

public class TileEntityEnergySidedInventory extends TileEntitySidedInventory implements IEnergyReceiver, IEnergyProvider, ISonarEnergyTile, IEnergySource, IEnergySink {

	public TileEntityEnergySidedInventory() {
		super();
		syncList.addPart(storage);
	}

	public EnergyMode energyMode = EnergyMode.RECIEVE;
	public final SyncSidedEnergyStorage storage = new SyncSidedEnergyStorage(this, 0);
	public int maxTransfer;

	public void setEnergyMode(EnergyMode mode) {
		energyMode = mode;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type.isType(SyncType.DROP)) {
			this.storage.setEnergyStored(nbt.getInteger("energy"));
		}
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type.isType(SyncType.DROP)) {
			nbt.setInteger("energy", this.storage.getEnergyStored());
		}
		return nbt;
	}

	public void discharge(int id) {
		slots()[id] = SonarAPI.getEnergyHelper().dischargeItem(slots()[id], this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public void charge(int id) {
		slots()[id] = SonarAPI.getEnergyHelper().chargeItem(slots()[id], this, maxTransfer != 0 ? Math.min(maxTransfer, getStorage().getMaxExtract()) : getStorage().getMaxExtract());
	}

	public void addEnergy(EnumFacing... faces) {
		for (EnumFacing dir : faces) {
			TileEntity entity = SonarHelper.getAdjacentTileEntity(this, dir);
			SonarAPI.getEnergyHelper().transferEnergy(this, entity, dir.getOpposite(), dir, maxTransfer);
		}
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		EnergyMode mode = getModeForSide(facing);
		if (CapabilityEnergy.ENERGY == capability) {
			return true;
		}
		if (SonarLoader.teslaLoaded && mode.canConnect()) {
			if ((capability == TeslaCapabilities.CAPABILITY_CONSUMER && mode.canRecieve()) || (capability == TeslaCapabilities.CAPABILITY_PRODUCER && mode.canSend()) || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		EnergyMode mode = getModeForSide(facing);
		if (CapabilityEnergy.ENERGY == capability) {
			return (T)storage.setCurrentFace(facing);
		}
		if (SonarLoader.teslaLoaded && mode.canConnect()) {
			if ((capability == TeslaCapabilities.CAPABILITY_CONSUMER && mode.canRecieve()) || (capability == TeslaCapabilities.CAPABILITY_PRODUCER && mode.canSend()) || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return (T) storage;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public EnergyMode getModeForSide(EnumFacing side) {
		if (side == null) {
			return EnergyMode.SEND_RECIEVE;
		}
		return energyMode;
	}

	@Override
	public SyncEnergyStorage getStorage() {
		return storage;
	}

	@Override
	public EnergyMode getMode() {
		return energyMode;
	}

	///// * CoFH *//////
	@Override
	public final boolean canConnectEnergy(EnumFacing from) {
		return getModeForSide(from).canConnect();
	}

	@Override
	public final int getEnergyStored(EnumFacing from) {
		return storage.getEnergyStored();
	}

	@Override
	public final int getMaxEnergyStored(EnumFacing from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public final int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return getModeForSide(from).canSend() ? storage.extractEnergy(maxExtract, simulate) : 0;
	}

	@Override
	public final int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return getModeForSide(from).canRecieve() ? storage.receiveEnergy(maxReceive, simulate) : 0;
	}

	public void onFirstTick() {
		super.onFirstTick();
		if (isServer() && SonarLoader.ic2Loaded()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (isServer() && SonarLoader.ic2Loaded()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		}
	}

	///// * IC2 *//////
	@Override
	public double getDemandedEnergy() {
		return Math.min(EUHelper.getVoltage(this.getSinkTier()), this.storage.addEnergy(this.storage.getMaxReceive(), ActionType.getTypeForAction(true)) / 4);
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return this.getModeForSide(side).canRecieve();
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		int addRF = this.storage.receiveEnergy((int) amount * 4, true);
		this.storage.addEnergy((int) addRF, ActionType.getTypeForAction(false));
		return amount - (addRF / 4);
	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
		return getModeForSide(side).canSend();
	}

	@Override
	public double getOfferedEnergy() {
		return Math.min(EUHelper.getVoltage(this.getSourceTier()), this.storage.removeEnergy(maxTransfer, ActionType.getTypeForAction(true)) / 4);
	}

	@Override
	public void drawEnergy(double amount) {
		this.storage.removeEnergy((long) (amount * 4), ActionType.getTypeForAction(false));

	}

	@Override
	public int getSourceTier() {
		return 4;
	}

}
