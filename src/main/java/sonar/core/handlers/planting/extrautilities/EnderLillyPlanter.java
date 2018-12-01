package sonar.core.handlers.planting.extrautilities;

import com.rwtema.extrautils2.backend.entries.XU2Entries;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import sonar.core.api.asm.ASMPlanter;
import sonar.core.api.planting.ISonarPlanter;

@ASMPlanter(modid = "extrautils2", priority = 0)
public class EnderLillyPlanter implements ISonarPlanter {

    @Override
    public boolean isPlantable(ItemStack seed) {
        return XU2Entries.blockEnderLilly.get().getItem() == seed.getItem();
    }

    @Override
    public boolean canPlant(ItemStack seed, World world, BlockPos pos) {
        if(world.isAirBlock(pos)) {
            return world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == Blocks.END_STONE;
        }
        return false;
    }

    @Override
    public boolean doPlant(ItemStack seed, World world, BlockPos pos) {
        IPlantable plant = (IPlantable)seed.getItem();
        world.setBlockState(pos, plant.getPlant(world, pos));
        return true;
    }
}
