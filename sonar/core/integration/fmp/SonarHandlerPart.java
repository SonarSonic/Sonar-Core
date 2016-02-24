package sonar.core.integration.fmp;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.integration.IWailaInfo;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public abstract class SonarHandlerPart extends SonarTilePart implements IWailaInfo, ITileHandler {

	public SonarHandlerPart() {
	}

	public SonarHandlerPart(int meta) {
		this.meta = (byte) meta;
	}

	public void update() {
		getTileHandler().update(tile());
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		getTileHandler().readData(nbt, type);
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		getTileHandler().writeData(nbt, type);
	}

	@Override
	public void onWorldSeparate() {
		getTileHandler().removed(world(), x(), y(), z(), meta);
	}
	@Override
	public List<String> getWailaInfo(List<String> currenttip) {
		if (this.getTileHandler() instanceof IWailaInfo) {
			return ((IWailaInfo) this.getTileHandler()).getWailaInfo(currenttip);
		}
		return currenttip;
	}

}
