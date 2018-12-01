package sonar.core.handlers.planting.agricraft;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.asm.ASMHarvester;
import sonar.core.api.planting.ISonarHarvester;
import sonar.core.helpers.SonarHelper;

@ASMHarvester(modid = "agricraft", priority = 0)
public class AgricraftHarvester implements ISonarHarvester {

    @Override
    public boolean canHarvest(World world, BlockPos pos, IBlockState state) {
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        return agriCrop != null;
    }

    @Override
    public boolean isReady(World world, BlockPos pos, IBlockState state) {
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        return agriCrop != null && agriCrop.isMature() && agriCrop.canBeHarvested();
    }

    @Override
    public boolean doHarvest(NonNullList<ItemStack> drops, World world, BlockPos pos, IBlockState state, int fortune, boolean keepBlock) {
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        if(agriCrop != null) {
            MethodResult result = agriCrop.onHarvest(drops::add, null);
            return result == MethodResult.SUCCESS;
        }
        return false;
    }
}
