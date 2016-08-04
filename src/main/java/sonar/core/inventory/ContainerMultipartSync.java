package sonar.core.inventory;

import mcmultipart.multipart.ISlottedPart;
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
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.network.PacketMultipartSync;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.IWorldPosition;

public abstract class ContainerMultipartSync extends Container {

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
						if (o != null && o instanceof EntityPlayerMP) {
							SonarCore.network.sendTo(new PacketMultipartSync(multipart.getPos(), syncData, type, ((ISlottedPart) multipart).getSlotMask().iterator().next()), (EntityPlayerMP) o);
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
