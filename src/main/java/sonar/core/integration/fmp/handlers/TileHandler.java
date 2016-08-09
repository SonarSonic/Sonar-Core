package sonar.core.integration.fmp.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;

@Deprecated
public abstract class TileHandler extends DirtyPart implements INBTSyncable {

	public final TileEntity tile;
	public final World world;
	public final BlockPos pos;
	public SyncTagType.BOOLEAN isMultipart = new SyncTagType.BOOLEAN(-1);
	public ArrayList<ISyncPart> syncParts = new ArrayList();
	public ArrayList<IDirtyPart> dirtyParts = new ArrayList();
	public boolean loaded = true;

	public TileHandler(boolean isMultipart, TileEntity tile) {
		this.isMultipart.setObject(isMultipart);
		this.tile = tile;
		this.world = this.tile.getWorld();
		this.pos = this.tile.getPos();
	}

	public void addDirtyParts(List<IDirtyPart> parts) {
		parts.add(isMultipart);
	}

	public void addSyncParts(List<ISyncPart> parts) {
		parts.add(isMultipart);
	}

	public void validate() {
	}

	public void invalidate() {
	}

	public abstract void update(TileEntity te);

	public void removed(World world, BlockPos pos, IBlockState state) {
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		return nbt;
	}

	public void markDirty() {
		this.setChanged(true);
	}

	public boolean isClient() {
		return world.isRemote;
	}

	public boolean isServer() {
		return !world.isRemote;
	}

	public BlockCoords getCoords() {
		return new BlockCoords(pos);
	}
}
