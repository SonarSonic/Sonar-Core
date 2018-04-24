package sonar.core.common.block;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.utils.ISpecialTooltip;

import javax.annotation.Nonnull;

public class SonarBlockTip extends ItemBlock {

	DecimalFormat dec = new DecimalFormat("##.##");
	public Block type;

	public SonarBlockTip(Block block) {
		super(block);
		type = block;
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> list, @Nonnull ITooltipFlag par4) {
        super.addInformation(stack, world, list, par4);
		if (Block.getBlockFromItem(stack.getItem()) instanceof ISpecialTooltip) {
			ISpecialTooltip tooltip = (ISpecialTooltip) Block.getBlockFromItem(stack.getItem());
            tooltip.addSpecialToolTip(stack, world, list, stack.getTagCompound());
		}
	}
}
