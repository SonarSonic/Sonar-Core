package sonar.core;

import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sonar.core.utils.IInteractBlock;

public class SonarEvents {
	@SubscribeEvent
	public void playerInteracts(PlayerInteractEvent event) {
		if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
			Block block = event.world.getBlockState(event.pos).getBlock();
			if (block instanceof IInteractBlock) {
				IInteractBlock interact = (IInteractBlock) block;
				if (interact.allowLeftClick()) {
					if (interact.isClickableSide(event.world, event.pos, event.face.getIndex())) {
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}
}