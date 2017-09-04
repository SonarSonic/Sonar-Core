package sonar.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public interface IWrenchable {

    ArrayList<ItemStack> wrenchBlock(EntityPlayer player, World world, BlockPos pos, boolean returnDrops);

    boolean canWrench(EntityPlayer player, World world, BlockPos pos);
}
