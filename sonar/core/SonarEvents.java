package sonar.core;

import sonar.core.utils.IInteractBlock;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SonarEvents {
	@SubscribeEvent
	public void playerInteracts(PlayerInteractEvent event) {
		if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
			Block block = event.world.getBlock(event.x, event.y, event.z);
			if (block instanceof IInteractBlock) {
				IInteractBlock interact = (IInteractBlock) block;
				if (interact.allowLeftClick()) {
					if (interact.isClickableSide(event.world, event.x, event.y, event.z, event.face)) {
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}
}