package sonar.core.integration.fmp.handlers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.network.sync.SyncBoolean;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;

/**
 * used for creating embedded handlers for blocks to allow easier alteration for
 * Forge Multipart components
 */
public abstract class TileHandler implements ISyncTile {

	public SyncBoolean isMultipart = new SyncBoolean(0);

	public TileHandler(boolean isMultipart) {
		this.isMultipart.setBoolean(isMultipart);
	}

	public abstract void update(TileEntity te);

	public void readData(NBTTagCompound nbt, SyncType type) {
		isMultipart.readFromNBT(nbt, type);
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		isMultipart.writeToNBT(nbt, type);
	}

	public abstract void removed(World world, int x, int y, int z, int meta);

}
