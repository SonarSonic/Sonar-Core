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
public class TileEntitySender extends TileEntityEnergy implements IEnergyHandler, IEnergySource, ISyncTile {

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
