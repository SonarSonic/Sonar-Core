package sonar.core.common.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;

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

@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyEmitter", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2", striprefs = true) })
public class TileEntityInventorySender extends TileEntityInventory implements IEnergyHandler, IEnergySource, ISyncTile {

	public SyncEnergyStorage storage;
	public int maxTransfer = 5000;

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
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
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

	// Industrial Craft Energy Methods

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
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return this.canConnectEnergy(direction);
	}

	@Method(modid = "IC2")
	@Override
	public double getOfferedEnergy() {
		return this.storage.extractEnergy(maxTransfer, true) / 4;
	}

	@Method(modid = "IC2")
	@Override
	public void drawEnergy(double amount) {
		this.storage.extractEnergy((int) (amount * 4), false);

	}

	@Method(modid = "IC2")
	@Override
	public int getSourceTier() {
		return 4;
	}

}
