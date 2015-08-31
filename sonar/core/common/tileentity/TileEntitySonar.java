package sonar.core.common.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.SonarPackets;
import sonar.core.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySonar extends TileEntity implements ISyncTile {

	protected boolean load;

	public void onLoaded() {

	}

	public void updateEntity() {
		if (load) {
			load = false;
			this.onLoaded();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readData(nbt, SyncType.SAVE);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeData(nbt, SyncType.SAVE);
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

	public void validate() {
		super.validate();
		this.load = true;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			this.load = nbt.getBoolean("loaded");
		}

	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			nbt.setBoolean("loaded", this.load);
		}
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		return currenttip;
	}

	public void requestSyncPacket() {
		SonarPackets.network.sendToServer(new PacketRequestSync(xCoord, yCoord, zCoord));
	}

}
