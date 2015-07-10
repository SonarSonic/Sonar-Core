package sonar.core.common.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.ChargingUtils;
import sonar.core.utils.EnergyCharge;
import sonar.core.utils.ISyncTile;
import sonar.core.utils.SonarAPI;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;

@Optional.InterfaceList(value = { @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyEmitter", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2", striprefs = true) })
public abstract class TileEntitySidedInventorySender extends TileEntitySidedInventory implements IEnergyHandler, IEnergySource, ISyncTile {

	public EnergyStorage storage;
	public int maxTransfer = 5000;

	public void onLoaded() {
		if (!this.worldObj.isRemote && SonarAPI.ic2Loaded()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		}
	}


	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTHelper.readEnergyStorage(storage, nbt);
		}

		if (type == SyncType.DROP) {
			this.storage.setEnergyStored(nbt.getInteger("energy"));
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTHelper.writeEnergyStorage(storage, nbt);
		}
		if (type == SyncType.DROP) {
			nbt.setInteger("energy", this.storage.getEnergyStored());

		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
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

	@Method(modid = "IC2")
	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.worldObj.isRemote) {
			if (SonarAPI.ic2Loaded()) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
		}
	}

	public int getMaxItemTransfer() {
		return 4;
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
