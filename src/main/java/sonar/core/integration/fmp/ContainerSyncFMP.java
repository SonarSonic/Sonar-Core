package sonar.core.integration.fmp;
/*
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

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
*/