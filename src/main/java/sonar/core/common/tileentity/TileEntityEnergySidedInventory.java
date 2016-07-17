package sonar.core.common.tileentity;

import java.util.List;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.energy.ISonarEnergyTile;
import sonar.core.energy.EnergyCharge;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.integration.SonarLoader;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncEnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

@Optional.InterfaceList({ 
	@Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = "CoFHAPI"), 
	@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHAPI") })
public class TileEntityEnergySidedInventory extends TileEntitySidedInventory implements IEnergyReceiver, IEnergyProvider, ISonarEnergyTile {

	public TileEntityEnergySidedInventory() {
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
}
