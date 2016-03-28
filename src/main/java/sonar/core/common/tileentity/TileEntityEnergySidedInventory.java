package sonar.core.common.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.energy.ChargingUtils;
import sonar.core.energy.EnergyCharge;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.core.utils.MachineSides;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class TileEntityEnergySidedInventory extends TileEntitySidedInventory implements IEnergyReceiver, IEnergyProvider  {

	public static enum EnergyMode {
		RECIEVE, SEND, SEND_RECIEVE, BLOCKED;

		public boolean canSend() {
			return this == SEND || this == SEND_RECIEVE;
		}

		public boolean canRecieve() {
			return this == RECIEVE || this == SEND_RECIEVE;
		}
	}

	public EnergyMode energyMode = EnergyMode.RECIEVE;
	public SyncEnergyStorage storage;
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

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.DROP) {
			nbt.setInteger("energy", this.storage.getEnergyStored());
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.add(storage);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
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
			storage.extractEnergy(maxExtract, simulate);
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (energyMode.canRecieve())
			storage.receiveEnergy(maxReceive, simulate);
		return 0;
	}
	public void discharge(int id) {
		if (ChargingUtils.canDischarge(slots()[id], this.storage)) {
			EnergyCharge discharge = ChargingUtils.discharge(slots()[id], storage);
			if (discharge.getEnergyStack() != null && discharge.getEnergyUsage() != 0) {
				slots()[id] = discharge.getEnergyStack();
				this.storage.modifyEnergyStored(discharge.getEnergyUsage());
				if (discharge.stackUsed()) {
					slots()[id].stackSize--;
					if (slots()[id].stackSize <= 0) {
						slots()[id] = null;
					}
				}
			}
		}
	}

	public void charge(int id) {
		if (ChargingUtils.canCharge(slots()[id], this.storage)) {
			EnergyCharge charge = ChargingUtils.charge(slots()[id], storage, maxTransfer);
			if (charge.getEnergyStack() != null && charge.getEnergyUsage() != 0) {
				slots()[id] = charge.getEnergyStack();
				this.storage.modifyEnergyStored(charge.getEnergyUsage());
			}
		}
	}

}
