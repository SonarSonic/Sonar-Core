package sonar.core.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.IWailaInfo;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.utils.ISyncTile;

public class TileEntitySonar extends TileEntity implements ITickable, ISyncTile, IWailaInfo {

	protected boolean load;
	protected BlockCoords coords = BlockCoords.EMPTY;

	public boolean isClient() {
		return worldObj.isRemote;
	}

	public boolean isServer() {
		return !worldObj.isRemote;
	}

	public void onLoaded() {
	}

	public void update() {
		if (load) {
			load = false;
			this.onLoaded();
		}
	}

	public BlockCoords getCoords() {
		return coords;
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

	public void addSyncParts(List<ISyncPart> parts) {
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(pos, 0, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	public void validate() {
		super.validate();
		coords = new BlockCoords(this);
		this.load = true;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		List<ISyncPart> parts = new ArrayList();
		this.addSyncParts(parts);
		for (ISyncPart part : parts) {
			if (part.canSync(type))
				part.readFromNBT(nbt, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		List<ISyncPart> parts = new ArrayList();
		this.addSyncParts(parts);
		for (ISyncPart part : parts) {
			if (part.canSync(type))
				part.writeToNBT(nbt, type);
		}
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		return currenttip;
	}

	public void requestSyncPacket() {
		SonarCore.network.sendToServer(new PacketRequestSync(pos));
	}

	public void sendSyncPacket(EntityPlayer player) {
		if (worldObj.isRemote) {
			return;
		}
		if (player != null && player instanceof EntityPlayerMP) {
			NBTTagCompound tag = new NBTTagCompound();
			writeData(tag, SyncType.SYNC);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendTo(new PacketTileSync(pos, tag), (EntityPlayerMP) player);
			}
		}
	}

	public boolean maxRender() {
		return false;

	}

	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return maxRender() ? 65536.0D : super.getMaxRenderDistanceSquared();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return maxRender() ? INFINITE_EXTENT_AABB : super.getRenderBoundingBox();
	}

}
