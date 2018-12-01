package sonar.core.handlers.planting.agricraft;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.asm.ASMPlanter;
import sonar.core.api.planting.ISonarPlanter;
import sonar.core.helpers.SonarHelper;

import java.util.Optional;

@ASMPlanter(modid = "agricraft", priority = 0)
public class AgricraftPlanter implements ISonarPlanter {

    @Override
    public boolean isPlantable(ItemStack seed) {
        Optional<AgriSeed> agriSeed = AgriApi.getSeedRegistry().valueOf(seed);
        return agriSeed.isPresent();
    }

    @Override
    public boolean canPlant(ItemStack seed, World world, BlockPos pos) {
        Optional<AgriSeed> agriSeed = AgriApi.getSeedRegistry().valueOf(seed);
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        if (agriSeed.isPresent() && agriCrop != null && agriCrop.acceptsSeed(agriSeed.get())) {
            return agriSeed.get().getPlant().getGrowthRequirement().isMet(world, pos);
        }
        return false;
    }

    @Override
    public boolean doPlant(ItemStack seed, World world, BlockPos pos) {
        Optional<AgriSeed> agriSeed = AgriApi.getSeedRegistry().valueOf(seed);
        IAgriCrop agriCrop = SonarHelper.getTile(world, pos, IAgriCrop.class);
        if (agriSeed.isPresent() && agriCrop != null) {
            MethodResult result = agriCrop.onApplySeeds(agriSeed.get(), null);
            return result == MethodResult.SUCCESS;
        }
        return false;
    }
}
