package sonar.core.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.ITileHandler;

public abstract class TileEntityHandler extends TileEntitySonar implements ITileHandler {

	public TileEntityHandler() {
		getTileHandler().addSyncParts(syncParts);
	}

	public void update() {
		super.update();
		this.getTileHandler().update(this);
		this.markDirty();
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		this.getTileHandler().readData(nbt, type);
	}

	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (forceSync) {
			type = SyncType.SYNC_OVERRIDE;
			forceSync = false;
		}
		this.getTileHandler().writeData(nbt, type);
		return nbt;
	}

	public void validate() {
		this.getTileHandler().validate();
	}

	public void invalidate() {
		this.getTileHandler().invalidate();
	}
}
