package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.blocks.IWrenchable;
import sonar.core.api.inventories.ISonarInventoryTile;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.common.block.properties.SonarProperties;
import sonar.core.helpers.NBTHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class SonarBlock extends Block implements IWrenchable {

    public AxisAlignedBB bounding_box = FULL_BLOCK_AABB;
    public boolean orientation;
    public boolean wrenchable = true;
    public boolean hasSpecialRenderer = false;

    public SonarBlock(Material material, boolean orientation) {
        this(material, material.getMaterialMapColor(), orientation);
    }

    public SonarBlock(Material material, MapColor mapColor, boolean orientation) {
        super(material, mapColor);
        this.orientation = orientation;
    }

    public void setBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        bounding_box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return bounding_box;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemstack) {
        super.onBlockPlacedBy(world, pos, state, player, itemstack);
        readDropStack(itemstack, state, pos, world);
        if(orientation){
            world.setBlockState(pos, state.withProperty(SonarProperties.FACING, player.getHorizontalFacing().getOpposite()), 2);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if(orientation) {
            setDefaultFacing(world, pos, state);
        }
    }

    @Override
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        dropInventoryContents(world, pos, state);
        super.breakBlock(world, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int count = quantityDropped(state, fortune, SonarCore.rand);
        for (int i = 0; i < count; i++) {
            Item item = getItemDropped(state, SonarCore.rand, fortune);
            if (item != Items.AIR) {
                ItemStack stack = new ItemStack(item, 1, damageDropped(state));
                writeDropStack(stack, state, pos, world);
                drops.add(stack);
            }
        }
    }

    @Override
    public boolean canWrench(EntityPlayer player, World world, BlockPos pos){
        return wrenchable;
    }

    //// ORIENTATION \\\\

    @Override
    public int getMetaFromState(IBlockState state) {
        return orientation ? state.getValue(SonarProperties.FACING).getIndex() : 0;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = orientation ? EnumFacing.getFront(meta) : EnumFacing.NORTH;
        if(facing.getAxis().isVertical()){
            facing = EnumFacing.NORTH;
        }
        return getDefaultState().withProperty(SonarProperties.FACING, facing);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SonarProperties.FACING);
    }

    public EnumFacing getRotation(IBlockState state) {
        return state.getValue(SonarProperties.FACING);
    }

    public boolean hasSpecialRenderer() {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return !hasSpecialRenderer();
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return !hasSpecialRenderer();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return !hasSpecialRenderer();
    }

    //// DROPPING METHODS \\\\

    public static final String DROP_TAG_NAME = "sonar_drop";
    public static final String DROP_TAG_LEGACY = "dropped";

    public static void dropInventoryContents(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof ISonarInventoryTile){
            List<ItemStack> stacks = ((ISonarInventoryTile) tile).inv().getDrops();
            for (ItemStack itemstack : stacks){
                if (!itemstack.isEmpty()){
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                }
            }
        }
    }

    public static void writeDropStack(ItemStack stack, IBlockState state, BlockPos pos, IBlockAccess world){
        TileEntity entity = world.getTileEntity(pos);
        if(entity instanceof INBTSyncable){
            stack.removeSubCompound(DROP_TAG_NAME);
            NBTTagCompound tag = stack.getOrCreateSubCompound(DROP_TAG_NAME);
            ((INBTSyncable) entity).writeData(tag, NBTHelper.SyncType.DROP);
        }
    }

    public static void readDropStack(ItemStack stack, IBlockState state, BlockPos pos, IBlockAccess world){
        TileEntity entity = world.getTileEntity(pos);
        if(entity instanceof INBTSyncable && stack.hasTagCompound()){
            if(stack.getTagCompound().getBoolean(DROP_TAG_LEGACY)){
                ////TODO REMOVE LEGACY
                ((INBTSyncable) entity).readData(stack.getTagCompound(), NBTHelper.SyncType.DROP);
            }else{
                NBTTagCompound tag = stack.getSubCompound(DROP_TAG_NAME);
                if(tag != null) ((INBTSyncable) entity).readData(tag, NBTHelper.SyncType.DROP);
            }
        }
    }

    public static void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            IBlockState block = worldIn.getBlockState(pos.north());
            IBlockState block1 = worldIn.getBlockState(pos.south());
            IBlockState block2 = worldIn.getBlockState(pos.west());
            IBlockState block3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(SonarProperties.FACING);

            if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock()) {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(SonarProperties.FACING, enumfacing), 2);
        }
    }
}
