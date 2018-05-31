package sonar.core;

import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sonar.core.api.blocks.IInteractBlock;

public class SonarEvents {

	@SubscribeEvent
	public void playerInteracts(PlayerInteractEvent event) {
		if (event instanceof PlayerInteractEvent.LeftClickBlock) {
			Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
			if (block instanceof IInteractBlock) {
				IInteractBlock interact = (IInteractBlock) block;
				if (interact.allowLeftClick()) {
					if (interact.isClickableSide(event.getWorld(), event.getPos(), event.getFace().getIndex())) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
}