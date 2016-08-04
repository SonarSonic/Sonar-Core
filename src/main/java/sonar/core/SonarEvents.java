package sonar.core;

import net.minecraft.block.Block;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.blocks.IInteractBlock;
import sonar.core.client.renderers.SonarCustomStateMapper;

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
						return;
					}
				}
			}
		}
	}

}