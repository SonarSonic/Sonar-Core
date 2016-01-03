package sonar.core.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public abstract class TileEntityHandler extends TileEntitySonar implements ITileHandler {

	public void updateEntity() {
		super.updateEntity();
		this.getTileHandler().update(this);
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		this.getTileHandler().readData(nbt, type);
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		this.getTileHandler().writeData(nbt, type);

	}
}
