package sonar.core.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.client.gui.ContainerCraftInventory;

public class InventoryContainerItem extends Item {

	@Override
	public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
		if (itemstack != null && player instanceof EntityPlayerMP
				&& player.openContainer instanceof ContainerCraftInventory) {
			player.closeScreen();
		}

		return super.onDroppedByPlayer(itemstack, player);
	}
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
	return 1;
	}
}
