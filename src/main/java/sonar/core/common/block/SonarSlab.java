package sonar.core.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SonarSlab extends Block {
	public static final PropertyEnum<BlockSlab.EnumBlockHalf> HALF = PropertyEnum.<BlockSlab.EnumBlockHalf> create("half", BlockSlab.EnumBlockHalf.class);

	public SonarSlab(Block block) {
		super(block.getMaterial());

		if (this.isDouble()) {
			this.fullBlock = true;
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

		if (!this.isDouble()) {
			this.setDefaultState(blockState.getBaseState().withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM));
		}

		this.setLightOpacity(255);
	}

	protected boolean canSilkHarvest() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		if (this.isDouble()) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			IBlockState iblockstate = worldIn.getBlockState(pos);

			if (iblockstate.getBlock() == this) {
				if (iblockstate.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
					this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
				} else {
					this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
				}
			}
		}
	}

	public void setBlockBoundsForItemRender() {
		if (this.isDouble()) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}

	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		this.setBlockBoundsBasedOnState(worldIn, pos);
		super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
	}

	public boolean isOpaqueCube() {
		return this.isDouble();
	}

	@Override
	public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (isOpaqueCube())
			return true;

		EnumBlockHalf side = world.getBlockState(pos).getValue(HALF);
		return (side == EnumBlockHalf.TOP && face == EnumFacing.DOWN) || (side == EnumBlockHalf.BOTTOM && face == EnumFacing.UP);
	}

	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
		return this.isDouble() ? iblockstate : (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? iblockstate : iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP));
	}

	public int quantityDropped(Random random) {
		return this.isDouble() ? 2 : 1;
	}

	public boolean isFullCube() {
		return this.isDouble();
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		if (this.isDouble()) {
			return super.shouldSideBeRendered(worldIn, pos, side);
		} else if (side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(worldIn, pos, side)) {
			return false;
		}
		return super.shouldSideBeRendered(worldIn, pos, side);
	}

	@SideOnly(Side.CLIENT)
	protected static boolean isSlab(Block blockIn) {
		return blockIn == Blocks.stone_slab || blockIn == Blocks.wooden_slab || blockIn == Blocks.stone_slab2 || blockIn instanceof SonarSlab;
	}

	public int getDamageValue(World worldIn, BlockPos pos) {
		return super.getDamageValue(worldIn, pos) & 7;
	}

	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();
		if (!this.isDouble()) {
			iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
		}

		return iblockstate;
	}

	/** Convert the BlockState into the correct metadata value */
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		if (!this.isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
			i |= 8;
		}

		return i;
	}

	protected BlockState createBlockState() {
		return this.isDouble() ? new BlockState(this) : new BlockState(this, new IProperty[] { HALF });
	}

	public abstract boolean isDouble();

	public static class Half extends SonarSlab {

		public Half(Block block) {
			super(block);
		}

		@Override
		public boolean isDouble() {
			return false;
		}

	}

	public static class Double extends SonarSlab {

		public Double(Block block) {
			super(block);
		}

		@Override
		public boolean isDouble() {
			return true;
		}

	}
}