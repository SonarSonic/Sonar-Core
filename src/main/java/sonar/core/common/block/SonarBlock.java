package sonar.core.common.block;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.SonarCore;
import sonar.core.api.blocks.IInteractBlock;
import sonar.core.api.blocks.IWrenchable;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.common.block.properties.IBlockRotated;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.inventory.IAdditionalInventory;
import sonar.core.inventory.IDropInventory;
import sonar.core.network.PacketBlockInteraction;

public abstract class SonarBlock extends Block implements IWrenchable, IInteractBlock, IBlockRotated {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public boolean orientation = true, wrenchable = true;
	public AxisAlignedBB customBB = null;

	protected SonarBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material);
		this.orientation = orientation;
		this.wrenchable = wrenchable;
		this.useNeighborBrightness = true;
		if (orientation)
			this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public final boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player != null) {
			TileEntity target = world.getTileEntity(pos);
			if (target != null && target instanceof TileEntitySonar) {
				((TileEntitySonar) target).forceNextSync();
			}
			return operateBlock(world, pos, state, player, hand, new BlockInteraction(side.getIndex(), hitX, hitY, hitZ, player.isSneaking() ? BlockInteractionType.SHIFT_RIGHT : BlockInteractionType.RIGHT));
		}
		return false;

	}

	public boolean isClickableSide(World world, BlockPos pos, int side) {
		return false;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (willHarvest) {
			if (world.isRemote && allowLeftClick()) {
				RayTraceResult posn = Minecraft.getMinecraft().objectMouseOver;
				if (isClickableSide(world, pos, posn.sideHit.getIndex())) {
					onBlockClicked(world, pos, player);
					return false;
				}
			}
			return true;
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	/** @return does the block drop as normal */
	public abstract boolean dropStandard(IBlockAccess world, BlockPos pos);

	/** standard onBlockActivated for use in Calculators blocks */
	public abstract boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact);

	public boolean allowLeftClick() {
		return false;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (world.isRemote && allowLeftClick()) {
			RayTraceResult movingPos = Minecraft.getMinecraft().objectMouseOver;
			float hitX = (float) (movingPos.hitVec.xCoord - movingPos.sideHit.getFrontOffsetX());
			float hitY = (float) (movingPos.hitVec.yCoord - movingPos.sideHit.getFrontOffsetY());
			float hitZ = (float) (movingPos.hitVec.zCoord - movingPos.sideHit.getFrontOffsetZ());
			SonarCore.network.sendToServer(new PacketBlockInteraction(pos, new BlockInteraction(movingPos.sideHit.getIndex(), hitX, hitY, hitZ, player.isSneaking() ? BlockInteractionType.SHIFT_LEFT : BlockInteractionType.LEFT)));
		}
	}

	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		super.harvestBlock(world, player, pos, state, te, stack);
		world.setBlockToAir(pos);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		super.getDrops(world, pos, state, fortune);
		if (dropStandard(world, pos)) {
			return super.getDrops(world, pos, state, fortune);
		}

		return Lists.newArrayList(getSpecialDrop(world, pos));
	}

	public final ItemStack getSpecialDrop(IBlockAccess world, BlockPos pos) {
		TileEntity target = world.getTileEntity(pos);
		if (target != null && target instanceof INBTSyncable) {
			ItemStack itemStack = new ItemStack(this, 1);
			processDrop(world, pos, (INBTSyncable) target, itemStack);
			return itemStack;
		} else {
			ItemStack itemStack = new ItemStack(this, 1);
			processDrop(world, pos, null, itemStack);
			return itemStack;
		}
	}

	public void processDrop(IBlockAccess world, BlockPos pos, INBTSyncable te, ItemStack drop) {
		if (te != null) {
			INBTSyncable handler = (INBTSyncable) te;
			NBTTagCompound tag = new NBTTagCompound();
			handler.writeData(tag, SyncType.DROP);
			if (!tag.hasNoTags()) {
				tag.setBoolean("dropped", true);
				drop.setTagCompound(tag);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		if (orientation)
			setDefaultFacing(world, pos, state);
	}

	protected void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			IBlockState block = worldIn.getBlockState(pos.north());
			IBlockState block1 = worldIn.getBlockState(pos.south());
			IBlockState block2 = worldIn.getBlockState(pos.west());
			IBlockState block3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			} else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			} else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			} else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}

			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (orientation)
			return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
		else {
			return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemstack) {
		if (orientation)
			world.setBlockState(pos, state.withProperty(FACING, player.getHorizontalFacing().getOpposite()), 2);

		if (itemstack.hasTagCompound()) {
			TileEntity entity = world.getTileEntity(pos);
			if (entity != null && entity instanceof INBTSyncable) {
				INBTSyncable handler = (INBTSyncable) entity;
				handler.readData(itemstack.getTagCompound(), SyncType.DROP);
			}
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		List<ItemStack> drops = Lists.newArrayList();
		TileEntity entity = world.getTileEntity(pos);
		if (entity != null) {
			if (entity instanceof IDropInventory) {
				IDropInventory inv = (IDropInventory) entity;
				if (inv.canDrop() && inv.dropSlots() != null) {
					int[] slots = inv.dropSlots();
					for (int i = 0; i < slots.length; i++) {
						ItemStack itemstack = inv.getStackInSlot(slots[i]);
						if (itemstack != null) {
							drops.add(itemstack);
						}
					}
				}
			} else if (entity instanceof IInventory) {
				IInventory inv = (IInventory) entity;
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack itemstack = inv.getStackInSlot(i);
					if (itemstack != null) {
						drops.add(itemstack);
					}
				}
			}
			if (entity instanceof IAdditionalInventory) {
				IAdditionalInventory additionalInv = (IAdditionalInventory) entity;
				ItemStack[] additionalStacks = additionalInv.getAdditionalStacks();
				if (additionalStacks != null) {
					for (ItemStack stack : additionalStacks) {
						if (stack != null) {
							drops.add(stack);
						}
					}
				}
			}
		}

		for (ItemStack stack : drops) {
			if (stack != null) {
				float f = SonarCore.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = SonarCore.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = SonarCore.rand.nextFloat() * 0.8F + 0.1F;

				EntityItem dropStack = new EntityItem(world, pos.getX() + f, pos.getY() + f1, pos.getZ() + f2, stack);
				world.spawnEntityInWorld(dropStack);
			}
		}
		super.breakBlock(world, pos, state);

	}

	@Override
	public ArrayList<ItemStack> wrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops) {
		SonarHelper.dropTile(player, world.getBlockState(pos).getBlock(), world, pos);
		return null;
	}

	@Override
	public boolean canWrench(EntityPlayer player, World world, BlockPos pos) {
		return true;
	}

	public boolean hasSpecialRenderer() {
		return false;
	}

	/*
	 * @Override public EnumBlockRenderType getRenderType(IBlockState state) { // NEEDS SOME ATTENTION return hasSpecialRenderer() ? EnumBlockRenderType. : EnumBlockRenderType.MODEL; }
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return hasSpecialRenderer() ? false : true;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return hasSpecialRenderer() ? false : true;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return hasSpecialRenderer() ? false : true;
	}

	/*
	 * public List<AxisAlignedBB> getCollisionBoxes(World world, BlockPos pos, List<AxisAlignedBB> list) { list.add(AxisAlignedBB.fromBounds(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ)); return list; }
	 * 
	 * public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axis, List list, Entity entity) { if (hasSpecialCollisionBox()) { List<AxisAlignedBB> collisionList = this.getCollisionBoxes(world, pos, Lists.newArrayList()); for (AxisAlignedBB collision : collisionList) { collision.offset(pos.getX(), pos.getY(), pos.getZ()); if (collision != null && collision.intersectsWith(axis)) { list.add(collision); } } } else { super.addCollisionBoxesToList(world, pos, state, axis, list, entity); } } public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) { return AxisAlignedBB.fromBounds(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ); }
	 */

	public void setBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		customBB = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return blockState.getBoundingBox(worldIn, pos);
	}

	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (customBB != null) {
			return customBB;
		}
		return super.getBoundingBox(state, world, pos);
	}

	public boolean hasSpecialCollisionBox() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}
		return this.getDefaultState().withProperty(FACING, enumfacing);

	}

	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();

	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	public EnumFacing getRotation(IBlockState state) {
		if (orientation)
			return state.getValue(FACING);
		else
			return EnumFacing.NORTH;
	}
}