package sonar.core.common.block;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.utils.ISpecialTooltip;

public class SonarBlockTip extends ItemBlock {

	DecimalFormat dec = new DecimalFormat("##.##");
	public Block type;

	public SonarBlockTip(Block block) {
		super(block);
		type = block;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);
		if (stack.hasTagCompound() && Block.getBlockFromItem(stack.getItem()) instanceof ISpecialTooltip) {
			ISpecialTooltip tooltip = (ISpecialTooltip) Block.getBlockFromItem(stack.getItem());
			tooltip.addSpecialToolTip(stack, player, list);
		}
		if (Block.getBlockFromItem(stack.getItem()) instanceof ISpecialTooltip) {
			ISpecialTooltip tooltip = (ISpecialTooltip) Block.getBlockFromItem(stack.getItem());
			tooltip.standardInfo(stack, player, list);

		}
	}
}
