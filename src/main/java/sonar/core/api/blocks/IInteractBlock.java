package sonar.core.api.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockInteraction;

/**
 * for blocks which can be clicked with left click
 */
public interface IInteractBlock {
	
    boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact);

    boolean allowLeftClick();
	
    boolean isClickableSide(World world, BlockPos pos, int side);

    /*
    @Override
    public boolean isClickableSide(World world, BlockPos pos, int side) {
        return false;
    }

    @Override
    public abstract boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact);

    @Override
    public boolean allowLeftClick() {
        return false;
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        if (world.isRemote && allowLeftClick()) {
            RayTraceResult movingPos = Minecraft.getMinecraft().objectMouseOver;
            float hitX = (float) (movingPos.hitVec.x - movingPos.sideHit.getFrontOffsetX());
            float hitY = (float) (movingPos.hitVec.y - movingPos.sideHit.getFrontOffsetY());
            float hitZ = (float) (movingPos.hitVec.z - movingPos.sideHit.getFrontOffsetZ());
            SonarCore.network.sendToServer(new PacketBlockInteraction(pos, new BlockInteraction(movingPos.sideHit.getIndex(), hitX, hitY, hitZ, player.isSneaking() ? BlockInteractionType.SHIFT_LEFT : BlockInteractionType.LEFT)));
        }
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
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
    */
}
