package sonar.core.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

public class ContainerSyncable extends Container {

    public TileEntitySyncable tile;

    public ContainerSyncable(TileEntitySyncable tile){
        this.tile = tile;
    }

    public void addListener(IContainerListener listener){
        super.addListener(listener);
        if(!tile.getWorld().isRemote && listener instanceof EntityPlayer){
            tile.listeners.addListener((EntityPlayer)listener, 0);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player){
        super.onContainerClosed(player);
        if(!tile.getWorld().isRemote){
            tile.listeners.removeListener(player, true, 0);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.getDistanceSq(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D) <= 64.0D;
    }

}
