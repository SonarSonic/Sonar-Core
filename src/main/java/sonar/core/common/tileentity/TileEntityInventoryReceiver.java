package sonar.core.common.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.energy.ChargingUtils;
import sonar.core.energy.EnergyCharge;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.SonarLoader;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.core.network.utils.ISyncTile;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;

@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyAcceptor", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2", striprefs = true) })
public class TileEntityInventoryReceiver extends TileEntityInventory implements IEnergyHandler, IEnergySink, ISyncTile {
	
	public int maxTransfer = 5000;
	public SyncEnergyStorage storage;

	public void onLoaded() {
		if (!this.worldObj.isRemote && SonarLoader.ic2Loaded()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		}
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

	public void discharge(int id) {
		if (ChargingUtils.canDischarge(slots[id], this.storage)) {
			EnergyCharge discharge = ChargingUtils.discharge(slots[id], storage);
			if (discharge.getEnergyStack() != null && discharge.getEnergyUsage() != 0) {
				slots[id] = discharge.getEnergyStack();
				this.storage.modifyEnergyStored(discharge.getEnergyUsage());
				if (discharge.stackUsed()) {
					slots[id].stackSize--;
					if (slots[id].stackSize <= 0) {
						slots[id] = null;
					}
				}
			}
		}
	}

	public void charge(int id) {
		if (ChargingUtils.canCharge(slots[id], this.storage)) {
			EnergyCharge charge = ChargingUtils.charge(slots[id], storage, maxTransfer);
			if (charge.getEnergyStack() != null && charge.getEnergyUsage() != 0) {
				slots[id] = charge.getEnergyStack();
				this.storage.modifyEnergyStored(charge.getEnergyUsage());
			}
		}
	}

	// CoFH Energy Methods
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

		return this.storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {

		return this.storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {

		return this.storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {

		return this.storage.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection dir) {

		return true;
	}

	@Method(modid = "IC2")
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
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}

	@Method(modid = "IC2")
	@Override
	public double getDemandedEnergy() {

		return this.storage.receiveEnergy(this.storage.getMaxReceive(), true) / 4;
	}

	@Method(modid = "IC2")
	@Override
	public int getSinkTier() {
		return 4;
	}

	@Method(modid = "IC2")
	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amountEU, double voltage) {
		int addRF = this.storage.receiveEnergy((int) amountEU * 4, true);
		this.storage.receiveEnergy((int) addRF, false);
		return amountEU - (addRF / 4);
	}
}
