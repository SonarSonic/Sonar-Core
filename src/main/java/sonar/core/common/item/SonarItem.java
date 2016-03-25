package sonar.core.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.integration.SonarAPI;
import sonar.core.utils.helpers.FontHelper;

public class SonarItem extends Item {

	public NBTTagCompound getTagCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(getDefaultTag());
		}
		return stack.getTagCompound();
	}

	public NBTTagCompound getDefaultTag() {
		return new NBTTagCompound();
	}
}
