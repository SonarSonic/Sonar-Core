package sonar.core.common.tileentity;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.integration.SonarLoader;
import sonar.core.network.sync.SyncEnergyStorage;

@Optional.InterfaceList({ 
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2")})
public class TileEntityEnergy extends TileEntitySonar implements IEnergyReceiver, IEnergyProvider, ISonarEnergyTile, IEnergyTile, IEnergySink, IEnergySource {

	public TileEntityEnergy() {
		syncParts.add(storage);
	}

	public EnergyMode energyMode = EnergyMode.RECIEVE;
	public final SyncEnergyStorage storage = new SyncEnergyStorage(0);
	public int maxTransfer;

	public void setEnergyMode(EnergyMode mode) {
		energyMode = mode;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.DROP) {
			this.storage.setEnergyStored(nbt.getInteger("energy"));
		}
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.DROP) {
			nbt.setInteger("energy", this.storage.getEnergyStored());
		}
		return nbt;
	}

	public void addEnergy(EnumFacing... faces) {
		for (EnumFacing dir : faces) {
			TileEntity entity = SonarHelper.getAdjacentTileEntity(this, dir);
			SonarAPI.getEnergyHelper().transferEnergy(this, entity, dir, dir.getOpposite(), maxTransfer);
		}
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		EnergyMode mode = getModeForSide(facing);
		if (SonarLoader.teslaLoaded && mode.canConnect()) {
			if ((capability == TeslaCapabilities.CAPABILITY_CONSUMER && mode.canRecieve()) || (capability == TeslaCapabilities.CAPABILITY_PRODUCER && mode.canSend()) || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {	
		EnergyMode mode = getModeForSide(facing);
		if (SonarLoader.teslaLoaded && mode.canConnect()) {
			if ((capability == TeslaCapabilities.CAPABILITY_CONSUMER && mode.canRecieve()) || (capability == TeslaCapabilities.CAPABILITY_PRODUCER && mode.canSend()) || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return (T) storage;
		}
		return super.getCapability(capability, facing);
	}


	@Override
	public EnergyMode getModeForSide(EnumFacing side) {
		if(side==null){
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

	/////* CoFH *//////
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return getModeForSide(from).canConnect();
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (energyMode.canSend())
			return storage.extractEnergy(maxExtract, simulate);
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (energyMode.canRecieve()) {
			return storage.receiveEnergy(maxReceive, simulate);
		}
		return 0;
	}

	/////* IC2 *//////	
	public void onFirstTick() {
		super.onFirstTick();
		if (!this.worldObj.isRemote && SonarLoader.ic2Loaded()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.worldObj.isRemote) {
			if (SonarLoader.ic2Loaded()) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
		}
	}

	@Method(modid = "IC2")
	@Override
	public double getDemandedEnergy() {
		return this.storage.addEnergy(this.storage.getMaxReceive(), ActionType.getTypeForAction(true)) / 4;
	}

	@Method(modid = "IC2")
	@Override
	public int getSinkTier() {
		return 4;
	}

	@Method(modid = "IC2")
	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return this.getModeForSide(side).canRecieve();
	}

	@Method(modid = "IC2")
	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		int addRF = this.storage.receiveEnergy((int) amount * 4, true);
		this.storage.addEnergy((int) addRF, ActionType.getTypeForAction(false));
		return amount - (addRF / 4);
	}

	@Method(modid = "IC2")
	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
		return this.getModeForSide(side).canSend();
	}

	@Method(modid = "IC2")
	@Override
	public double getOfferedEnergy() {
		return this.storage.removeEnergy(maxTransfer, ActionType.getTypeForAction(true)) / 4;
	}

	@Method(modid = "IC2")
	@Override
	public void drawEnergy(double amount) {
		this.storage.removeEnergy((long) (amount * 4), ActionType.getTypeForAction(false));

	}

	@Method(modid = "IC2")
	@Override
	public int getSourceTier() {
		return 4;
	}

}
