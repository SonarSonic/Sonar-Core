package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.SonarCore;
import sonar.core.network.PacketTileSync;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public abstract class ContainerSync extends Container {

	public ISyncTile sync;
	public TileEntity tile;

	public ContainerSync(ISyncTile sync, TileEntity tile) {
		this.sync = sync;
		this.tile = tile;
	}

	public ContainerSync(TileEntity tile) {
		if (tile instanceof ISyncTile) {
			sync = (ISyncTile) tile;
		}
		this.tile = tile;
	}

	@Override
	public void detectAndSendChanges() {
		if (syncInventory()){
			super.detectAndSendChanges();
		}
		if (sync != null && crafters != null) {
			NBTTagCompound syncData = new NBTTagCompound();
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				sync.writeData(syncData, type);
				if (!syncData.hasNoTags()) {
					for (Object o : crafters) {
						if (o != null && o instanceof EntityPlayerMP) {
							SonarCore.network.sendTo(new PacketTileSync(tile.getPos(), syncData, type), (EntityPlayerMP) o);
						}
					}
				}
			}
		}
	}

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.SYNC };
	}

	public boolean syncInventory() {
		return true;
	}
}
