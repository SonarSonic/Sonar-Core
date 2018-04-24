package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.IWorldPosition;

public class ContainerSync extends ContainerSonar {

	SyncType[] types = new SyncType[] { SyncType.DEFAULT_SYNC };
	public INBTSyncable sync;
	public IWorldPosition tile;

	public ContainerSync(INBTSyncable sync, IWorldPosition tile) {
		this.sync = sync;
		this.tile = tile;
	}

	public ContainerSync(TileEntitySonar tile) {
		if (tile instanceof INBTSyncable) {
            sync = tile;
		}
		this.tile = tile;
	}

	@Override
	public void detectAndSendChanges() {
		if (syncInventory()) {
			super.detectAndSendChanges();
		}
		if (sync != null && this.listeners != null) {
			NBTTagCompound syncData = new NBTTagCompound();
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				sync.writeData(syncData, type);
				if (!syncData.hasNoTags()) {
					for (IContainerListener o : listeners) {
						if (o instanceof EntityPlayerMP) {
							SonarCore.network.sendTo(new PacketTileSync(tile.getCoords().getBlockPos(), syncData, type), (EntityPlayerMP) o);
						}
					}
				}
			}
		}
	}

	public SyncType[] getSyncTypes() {
		return types;
	}

	public ContainerSync setTypes(SyncType[] types) {
		this.types = types;
		return this;
	}

	public boolean syncInventory() {
		return true;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
