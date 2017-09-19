package sonar.core.common.tileentity;

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
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.core.network.sync.SyncableList;
import sonar.core.utils.IWorldPosition;

import java.util.List;

public class TileEntitySonar extends TileEntity implements ISyncableListener, ITickable, INBTSyncable, IWailaInfo, IWorldPosition {

	public SyncableList syncList = new SyncableList(this);
	protected boolean forceSync;
	protected BlockCoords coords = BlockCoords.EMPTY;
	public boolean loaded = true;
    public boolean isDirty;

	public TileEntitySonar() {
	}

	public boolean isClient() {
        return world != null && world.isRemote;
	}

	public boolean isServer() {
        return world == null || !world.isRemote;
	}

    @Override
	public void onLoad() {
	}

	public void onFirstTick() {
		this.markBlockForUpdate();
		markDirty();
	}

    @Override
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
	public NBTTagCompound getUpdateTag() {
		return writeData(super.getUpdateTag(), SyncType.SYNC_OVERRIDE);
	}

    @Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		this.readData(tag, SyncType.SYNC_OVERRIDE);
	}

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = writeToNBT(new NBTTagCompound());
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

    @Override
	public void validate() {
		super.validate();
		coords = new BlockCoords(this);
	}

    @Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, syncList);
	}

    @Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (forceSync && type == SyncType.DEFAULT_SYNC) {
			type = SyncType.SYNC_OVERRIDE;
			forceSync = false;
		}
		NBTHelper.writeSyncParts(nbt, type, syncList, forceSync);
		return nbt;
	}

    @Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip, IBlockState state) {
		return currenttip;
	}

	public void forceNextSync() {
		forceSync = true;
		markDirty();
	}

	public void onSyncPacketRequested(EntityPlayer player) {
	}

	public void requestSyncPacket() {
		SonarCore.network.sendToServer(new PacketRequestSync(pos));
	}

	public void sendSyncPacket(EntityPlayer player) {
		if (world.isRemote) {
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
		if (this.isServer()) {
			markDirty();
			SonarCore.sendFullSyncAroundWithRenderUpdate(this, 128);
		} else {
			getWorld().markBlockRangeForRenderUpdate(pos, pos);
            getWorld().getChunkFromBlockCoords(getPos()).setModified(true);
		}
		// may need some more stuff, here to make life easier
	}

	public boolean maxRender() {
		return false;
	}

    @Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return maxRender() ? 65536.0D : super.getMaxRenderDistanceSquared();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return maxRender() ? INFINITE_EXTENT_AABB : super.getRenderBoundingBox();
	}

	@Override
	public void markChanged(IDirtyPart part) {
		if (this.isServer()) {
			syncList.markSyncPartChanged(part);
			isDirty = true;
		}
	}

    @Override
    public void update() {
        if (loaded) {
            onFirstTick();
            loaded = !loaded;
        }
        if (isDirty) {
            this.markDirty();
            isDirty = !isDirty;
        }
    }
}
