package sonar.core.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.IWailaInfo;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.IWorldPosition;

public class TileEntitySonar extends TileEntity implements ITickable, INBTSyncable, IWailaInfo, IWorldPosition {

	public ArrayList<ISyncPart> syncParts = new ArrayList();
	public ArrayList<IDirtyPart> dirtyParts = new ArrayList();
	protected boolean forceSync;
	protected BlockCoords coords = BlockCoords.EMPTY;
	public boolean loaded = true;
	protected DirtyPart isDirty = new DirtyPart();

	public TileEntitySonar() {
	}

	public boolean isClient() {
		return worldObj.isRemote;
	}

	public boolean isServer() {
		return !worldObj.isRemote;
	}

	public final void onLoad() {
		isDirty.setChanged(true);
	}

	public void onFirstTick() {
		this.markBlockForUpdate();
	}

	public void update() {
		boolean markDirty = false;
		if (loaded) {
			onFirstTick();
			loaded = !loaded;
		}

		for (ISyncPart part : syncParts) {
			if (part != null && part.hasChanged()) {
				markDirty = true;
				break;
			}
		}
		for (IDirtyPart part : dirtyParts) {
			if (part != null && part.hasChanged()) {
				markDirty = true;
				part.setChanged(false);
			}
		}
		if (markDirty) {
			markDirty();
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeData(nbt, SyncType.SAVE);
		return nbt;
	}

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		writeToNBT(nbtTag);
		return new SPacketUpdateTileEntity(pos, 0, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	public void validate() {
		super.validate();
		coords = new BlockCoords(this);
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, this.syncParts);
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (forceSync && type == SyncType.DEFAULT_SYNC) {
			type = SyncType.SYNC_OVERRIDE;
			forceSync = false;
		}
		NBTHelper.writeSyncParts(nbt, type, this.syncParts, forceSync);
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip, IBlockState state) {
		return currenttip;
	}

	public void forceNextSync() {
		forceSync = true;
		isDirty.setChanged(true);
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

	public void markBlockForUpdate() {
		if (this.isServer()){
			isDirty.setChanged(true);
			SonarCore.sendFullSyncAroundWithRenderUpdate(this, 128);
		}else{
			getWorld().markBlockRangeForRenderUpdate(pos, pos);
			getWorld().getChunkFromBlockCoords(getPos()).setChunkModified();
		}
		// may need some more stuff, here to make life easier
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
