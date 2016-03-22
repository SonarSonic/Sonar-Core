package sonar.core.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.integration.SonarAPI;
import sonar.core.utils.helpers.FontHelper;

public class SonarItem extends Item {

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);
		if (!SonarAPI.isEnabled(stack)) {
			list.add(FontHelper.translate("calc.ban"));
		}
	}
}
