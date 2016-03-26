package sonar.core.common.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.SonarLoader;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncEnergyStorage;

public class TileEntityEnergy extends TileEntitySonar implements IEnergyTile {

	public SyncEnergyStorage storage;
	public int maxTransfer;

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

	public void onLoaded() {
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

}
