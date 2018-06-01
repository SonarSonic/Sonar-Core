package sonar.core.handlers.inventories.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
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
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				NBTTagCompound syncData = sync.writeData(new NBTTagCompound(), type);
				if (!syncData.hasNoTags()) {
					sendPacketToListeners(new PacketTileSync(tile.getCoords().getBlockPos(), syncData, type));
				}
			}
		}
	}

	public final void sendPacketToListeners(IMessage packet){
		for (IContainerListener o : listeners) {
			if (o instanceof EntityPlayerMP) {
				SonarCore.network.sendTo(packet, (EntityPlayerMP) o);
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
	public boolean canInteractWith(EntityPlayer player) {
		return !(tile instanceof IInventory) || ((IInventory) tile).isUsableByPlayer(player);
	}
}
