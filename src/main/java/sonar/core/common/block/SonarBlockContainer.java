package sonar.core.common.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class SonarBlockContainer extends SonarBlock implements ITileEntityProvider {

    protected SonarBlockContainer(Material material, boolean orientation) {
        this(material, material.getMaterialMapColor(), orientation);
    }

    protected SonarBlockContainer(Material material, MapColor color, boolean orientation) {
        super(material, color, orientation);
        this.hasTileEntity = true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (te instanceof IWorldNameable && ((IWorldNameable) te).hasCustomName()) {
            player.addStat(StatList.getBlockStats(this));
            player.addExhaustion(0.005F);

            if (world.isRemote) {
                return;
            }

            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            Item item = this.getItemDropped(state, world.rand, i);

            if (item == Items.AIR) {
                return;
            }

            ItemStack itemstack = new ItemStack(item, this.quantityDropped(world.rand));
            itemstack.setStackDisplayName(((IWorldNameable) te).getName());
            spawnAsEntity(world, pos, itemstack);
        } else {
            super.harvestBlock(world, player, pos, state, (TileEntity) null, stack);
        }
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        super.eventReceived(state, world, pos, id, param);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
}