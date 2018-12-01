package sonar.core.handlers.planting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.planting.ISonarFertiliser;
import sonar.core.api.planting.ISonarHarvester;
import sonar.core.api.planting.ISonarPlanter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlantingHandler {

    public List<ISonarHarvester> harvesters = new ArrayList<>();
    public List<ISonarPlanter> planters = new ArrayList<>();
    public List<ISonarFertiliser> fertilisers = new ArrayList<>();

    public static PlantingHandler instance(){
        return SonarCore.instance.planting_handler;
    }

    @Nullable
    public ISonarPlanter getPlanter(ItemStack seeds){
        for(ISonarPlanter planter : planters){
            if(planter.isPlantable(seeds)){
                return planter;
            }
        }
        return null;
    }

    @Nullable
    public ISonarPlanter getPlanter(ItemStack seeds, int tier) {
        for (ISonarPlanter planter : planters) {
            if (planter.isPlantable(seeds) && planter.canTierPlant(seeds, tier)) {
                return planter;
            }
        }
        return null;
    }

    @Nullable
    public ISonarHarvester getHarvester(World world, BlockPos pos, IBlockState state){
        for(ISonarHarvester harvester : harvesters){
            if(harvester.canHarvest(world, pos, state)){
                return harvester;
            }
        }
        return null;
    }

    @Nullable
    public ISonarFertiliser getFertiliser(World world, BlockPos pos, IBlockState state){
        for(ISonarFertiliser fertiliser : fertilisers){
            if(fertiliser.canFertilise(world, pos, state)){
                return fertiliser;
            }
        }
        return null;
    }

    public boolean doFertilise(World world, BlockPos pos, IBlockState state){
        ISonarFertiliser fertiliser = getFertiliser(world, pos, state);
        if(fertiliser != null && fertiliser.canGrow(world, pos, state)) {
            return fertiliser.grow(world, pos, state);
        }
        return false;
    }

}
