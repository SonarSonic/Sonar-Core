package sonar.core.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.IWailaInfo;
import sonar.core.inventory.ISonarInventoryTile;
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

	public boolean isClient() {
		return getWorld() != null && getWorld().isRemote;
	}

	public boolean isServer() {
		return getWorld() == null || !getWorld().isRemote;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability && this instanceof ISonarInventoryTile) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability && this instanceof ISonarInventoryTile) {
			return (T) ((ISonarInventoryTile) this).inv().getItemHandler(facing);
		}
		return super.getCapability(capability, facing);
	}

	public void onFirstTick() {
		// this.markBlockForUpdate();
		// markDirty();
	}

	@Override
	public BlockCoords getCoords() {
		if (coords == BlockCoords.EMPTY) {
			coords = new BlockCoords(this);
		}
		return coords;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readData(nbt, SyncType.SAVE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeData(nbt, SyncType.SAVE);
		return nbt;
	}
	
	/**things like inventories are generally only sent with SyncType.SAVE*/
	public SyncType getUpdateTagType(){
		return SyncType.SYNC_OVERRIDE;
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		return writeData(super.getUpdateTag(), getUpdateTagType());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		readData(tag, getUpdateTagType());
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

	public void onSyncPacketRequested(EntityPlayer player) {}

	public void requestSyncPacket() {
		SonarCore.network.sendToServer(new PacketRequestSync(pos));
	}

	public void sendSyncPacket(EntityPlayer player) {
		sendSyncPacket(player, SyncType.SYNC_OVERRIDE);
	}

	public void sendSyncPacket(EntityPlayer player, SyncType type) {
		if (world.isRemote) {
			return;
		}
		if (player != null && player instanceof EntityPlayerMP) {
			NBTTagCompound tag = new NBTTagCompound();
			writeData(tag, type);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendTo(createSyncPacket(tag, type), (EntityPlayerMP) player);
			}
		}
	}

	public IMessage createRequestPacket() {
		return new PacketRequestSync(pos);
	}

	public IMessage createSyncPacket(NBTTagCompound tag, SyncType type) {
		return new PacketTileSync(pos, tag, type);
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

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
