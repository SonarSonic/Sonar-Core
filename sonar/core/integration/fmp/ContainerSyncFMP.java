package sonar.core.integration.fmp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.network.PacketTileSync;
import sonar.core.network.SonarPackets;
import sonar.core.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper;

public abstract class ContainerSyncFMP extends Container {

	public SonarTilePart part;

	public ContainerSyncFMP(SonarTilePart tile) {
		this.part = tile;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (part instanceof SonarTilePart) {
			if (part != null) {
				if (crafters != null) {
					for (Object o : crafters) {
						if (o != null && o instanceof EntityPlayerMP) {
							part.sendAdditionalPackets((EntityPlayerMP) o);
						}
					}

				}
			}
		}
	}
}
