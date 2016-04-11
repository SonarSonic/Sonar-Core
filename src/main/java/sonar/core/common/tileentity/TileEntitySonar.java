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
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.IWailaInfo;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.ISyncPart;

public class TileEntitySonar extends TileEntity implements ITickable, INBTSyncable, IWailaInfo {

	protected boolean load, forceSync;
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
		/*List<ISyncPart> parts = new ArrayList(); this.addSyncParts(parts); boolean markDirty = false; for (ISyncPart part : parts) { if (part.hasChanged()) { markDirty = true; break; } } if (markDirty) { markDirty(); } */
		markDirty();
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
			if (part != null && part.canSync(type)) {
				part.readFromNBT(nbt, type);
				part.setChanged(false);
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		if (forceSync && type == SyncType.DEFAULT_SYNC) {
			type = SyncType.SYNC_OVERRIDE;
			forceSync = false;
		}
		List<ISyncPart> parts = new ArrayList();
		this.addSyncParts(parts);
		for (ISyncPart part : parts) {
			if (part != null && (forceSync || type.mustSync() || part.hasChanged()) && part.canSync(type)) {
				part.writeToNBT(nbt, type);
				part.setChanged(false);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		return currenttip;
	}

	public void forceNextSync() {
		forceSync = true;
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
			writeData(tag, SyncType.SYNC_OVERRIDE);
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
