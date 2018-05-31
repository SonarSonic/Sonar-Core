package sonar.core.inventory.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.TileSonarMultipart;
import sonar.core.network.PacketMultipartSync;

import javax.annotation.Nonnull;

public class ContainerMultipartSync extends Container {

	SyncType[] types = new SyncType[] { SyncType.DEFAULT_SYNC };
	public TileSonarMultipart multipart;

	public ContainerMultipartSync(TileSonarMultipart multipart) {
		this.multipart = multipart;
	}

	@Override
	public void detectAndSendChanges() {
		if (syncInventory()) {
			super.detectAndSendChanges();
		}
		if (multipart != null && this.listeners != null) {
			NBTTagCompound syncData = new NBTTagCompound();
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				multipart.writeData(syncData, type);
				if (!syncData.hasNoTags()) {
					for (IContainerListener o : listeners) {
						if (o instanceof EntityPlayerMP) {
							SonarCore.network.sendTo(new PacketMultipartSync(multipart.getPos(), syncData, type, multipart.getSlotID()), (EntityPlayerMP) o);
						}
					}
				}
			}
		}
	}

	@Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return ItemStack.EMPTY;
	}

	public SyncType[] getSyncTypes() {
		return types;
	}

	public boolean syncInventory() {
		return true;
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
		return true;
	}

	public ContainerMultipartSync setTypes(SyncType[] syncTypes) {
		this.types = syncTypes;
		return this;
	}
}
