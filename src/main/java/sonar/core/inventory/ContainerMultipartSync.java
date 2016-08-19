package sonar.core.inventory;

import mcmultipart.multipart.ISlottedPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.network.PacketMultipartSync;

public class ContainerMultipartSync extends Container {

	SyncType[] types = new SyncType[] { SyncType.DEFAULT_SYNC };
	public SonarMultipart multipart;

	public ContainerMultipartSync(SonarMultipart multipart) {
		this.multipart = multipart;
	}

	@Override
	public void detectAndSendChanges() {
		if (syncInventory()) {
			super.detectAndSendChanges();
		}
		if (multipart != null && multipart instanceof ISlottedPart && this.listeners != null) {
			NBTTagCompound syncData = new NBTTagCompound();
			SyncType[] types = getSyncTypes();
			for (SyncType type : types) {
				multipart.writeData(syncData, type);
				if (!syncData.hasNoTags()) {
					for (IContainerListener o : listeners) {
						if (o != null && o instanceof EntityPlayerMP && multipart.getUUID() != null) {
							SonarCore.network.sendTo(new PacketMultipartSync(multipart.getPos(), syncData, type, multipart.getUUID()), (EntityPlayerMP) o);
						}
					}
				}
			}
		}
	}

	public SyncType[] getSyncTypes() {
		return types;
	}

	public boolean syncInventory() {
		return true;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public ContainerMultipartSync setTypes(SyncType[] syncTypes) {
		this.types = syncTypes;
		return this;
	}
}
