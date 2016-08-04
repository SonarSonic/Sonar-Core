package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.IWorldPosition;

public abstract class ContainerSync extends Container {

	public INBTSyncable sync;
	public IWorldPosition tile;

	public ContainerSync(INBTSyncable sync, TileEntitySonar tile) {
		this.sync = sync;
		this.tile = tile;
	}

	public ContainerSync(TileEntitySonar tile) {
		if (tile instanceof INBTSyncable) {
			sync = (INBTSyncable) tile;
		}
		this.tile = tile;
	}

	@Override
	public void detectAndSendChanges() {
		if (syncInventory()){
			super.detectAndSendChanges();
		}
		if (sync != null && this.listeners != null) {
			NBTTagCompound syncData = new NBTTagCompound();
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				sync.writeData(syncData, type);
				if (!syncData.hasNoTags()) {
					for (IContainerListener o : listeners) {
						if (o != null && o instanceof EntityPlayerMP) {
							SonarCore.network.sendTo(new PacketTileSync(tile.getCoords().getBlockPos(), syncData, type), (EntityPlayerMP) o);
						}
					}
				}
			}
		}
	}

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.DEFAULT_SYNC };
	}

	public boolean syncInventory() {
		return true;
	}
}
