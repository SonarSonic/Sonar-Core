package sonar.core.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.helpers.FontHelper;
import sonar.core.integration.SonarLoader;

public class SonarSeeds extends Item implements IPlantable {
	private Block cropBlock;
	private Block soilBlock;
	public int greenhouseTier;

	public SonarSeeds(Block cropBlock, Block soilBlock, int tier) {
		this.cropBlock = cropBlock;
		this.soilBlock = soilBlock;
		this.setCreativeTab(CreativeTabs.MATERIALS);
		this.greenhouseTier = tier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);
		if (SonarLoader.calculatorLoaded()) {
			switch (greenhouseTier) {
			case 0:
				break;
			case 1:
				list.add(FontHelper.translate("planting.basic"));
				break;
			case 2:
				list.add(FontHelper.translate("planting.advanced"));
				break;
			case 3:
				list.add(FontHelper.translate("planting.flawless"));
				break;
			}
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (this.greenhouseTier == 0 || !SonarLoader.calculatorLoaded()) {
			if (side != EnumFacing.UP) {
				return EnumActionResult.PASS;
			} else if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(pos.offset(EnumFacing.UP), side, stack)) {
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock().canSustainPlant(state, world, pos, EnumFacing.UP, this) && world.isAirBlock(pos.offset(EnumFacing.UP))) {
					world.setBlockState(pos.offset(side), cropBlock.getDefaultState());
					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				} else {
					return EnumActionResult.PASS;
				}
			} else {
				return EnumActionResult.PASS;
			}
		}
		return EnumActionResult.PASS;
	}

	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return cropBlock == Blocks.NETHER_WART ? EnumPlantType.Nether : EnumPlantType.Crop;
	}

	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return cropBlock.getDefaultState();
	}

	public boolean canTierUse(int tier) {
		if (tier >= this.greenhouseTier) {
			return true;
		}
		return false;
	}

}