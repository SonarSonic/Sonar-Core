package sonar.core.common.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.api.ISyncTile;
import sonar.calculator.mod.api.SyncData;
import sonar.calculator.mod.api.SyncType;
import sonar.core.utils.ChargingUtils;
import sonar.core.utils.EnergyCharge;
import sonar.core.utils.IDropTile;
import sonar.core.utils.SonarAPI;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;

@Optional.InterfaceList(value = {
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyAcceptor", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2", striprefs = true) })
public class TileEntityInventoryReceiver extends TileEntityInventory implements
		IEnergyHandler, IDropTile, IEnergySink,ISyncTile {
	public int maxTransfer = 5000;
	public EnergyStorage storage;

	public void onLoaded(){
		if (!this.worldObj.isRemote &&SonarAPI.ic2Loaded()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));	
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("energyStorage")) {
			this.storage.readFromNBT(nbt.getCompoundTag("energyStorage"));
		}
		

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound energyTag = new NBTTagCompound();
		this.storage.writeToNBT(energyTag);
		nbt.setTag("energyStorage", energyTag);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		super.getDescriptionPacket();
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 0, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readFromNBT(packet.func_148857_g());
	}


	public void discharge(int id) {
		if (ChargingUtils.canDischarge(slots[id], this.storage)) {
			EnergyCharge discharge = ChargingUtils.discharge(slots[id], storage);
			if (discharge.getEnergyStack() != null&& discharge.getEnergyUsage() != 0) {
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
			if (charge.getEnergyStack() != null&& charge.getEnergyUsage() != 0) {
				slots[id] = charge.getEnergyStack();
				this.storage.modifyEnergyStored(charge.getEnergyUsage());
			}
		}
	}

	// CoFH Energy Methods
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {

		return this.storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {

		return 0;
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

	@Method(modid = "IC2")
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter,
			ForgeDirection direction) {
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
	public double injectEnergy(ForgeDirection directionFrom, double amountEU,
			double voltage) {
		int addRF = this.storage.receiveEnergy((int) amountEU * 4, true);
		this.storage.receiveEnergy((int) addRF, false);
		return amountEU - (addRF / 4);
	}

	@Override
	public void readInfo(NBTTagCompound tag) {
		this.storage.setEnergyStored(tag.getInteger("energy"));
	}

	@Override
	public void writeInfo(NBTTagCompound tag) {
		tag.setInteger("energy", this.storage.getEnergyStored());
	}
	@Override
	public void onSync(int data, int id) {
		switch(id){
		case SyncType.ENERGY: this.storage.setEnergyStored(data);
		}
	}

	@Override
	public SyncData getSyncData(int id) {
		switch(id){
		case SyncType.ENERGY: return new SyncData(true,storage.getEnergyStored());
		}
		return new SyncData(false,0);
	}
}
