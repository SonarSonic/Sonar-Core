package sonar.core.handlers.planting.agricraft;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.asm.ASMFertiliser;
import sonar.core.api.planting.ISonarFertiliser;
import sonar.core.helpers.SonarHelper;

import java.util.Optional;

@ASMFertiliser(modid = "agricraft", priority = 0)
public class AgricraftFertiliser implements ISonarFertiliser {

    @Override
    public boolean canFertilise(World world, BlockPos pos, IBlockState state) {
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        if(agriCrop != null){
            Optional<IAgriFertilizer> agriFertilizer = AgriApi.getFertilizerRegistry().valueOf(new ItemStack(Items.DYE, 1, 15));
            return agriFertilizer.isPresent() && agriCrop.acceptsFertilizer(agriFertilizer.get());
        }
        return false;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state) {
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        return agriCrop != null && agriCrop.isFertile();
    }

    @Override
    public boolean grow(World world, BlockPos pos, IBlockState state) {
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        Optional<IAgriFertilizer> agriFertilizer = AgriApi.getFertilizerRegistry().valueOf(new ItemStack(Items.DYE, 1, 15));
        if(agriCrop != null && agriFertilizer.isPresent()){
            MethodResult result = agriCrop.onApplyFertilizer(agriFertilizer.get(), SonarCore.rand);
            return result == MethodResult.SUCCESS;
        }
        return false;
    }
}