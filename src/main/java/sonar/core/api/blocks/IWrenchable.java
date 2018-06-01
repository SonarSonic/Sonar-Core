package sonar.core.api.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWrenchable {

    default void onWrenched(EntityPlayer player, World world, BlockPos pos){}

    default boolean canWrench(EntityPlayer player, World world, BlockPos pos){
        return true;
    }
}
