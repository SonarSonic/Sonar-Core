package sonar.core.inventory.handling.methods;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sonar.core.inventory.handling.ITransferMethod;
import sonar.core.inventory.handling.ItemTransferHandler;
import sonar.core.inventory.handling.ItemTransferHelper;

public class TransferMethodSimple implements ITransferMethod {

    public final ItemTransferHandler transferHandler;
    public int perTick = 4;
    public int tick;
    public int tickTime = 0;

    public TransferMethodSimple(ItemTransferHandler handler){
        this.transferHandler = handler;
    }

    public TransferMethodSimple setTickTime(int tickTime){
        this.tickTime = tickTime;
        return this;
    }

    public TransferMethodSimple setTransferRate(int perTick){
        this.perTick = perTick;
        return this;
    }

    @Override
    public void transfer() {
        if(tickTime != 0){
            tick++;
            if(!(tick >= tickTime)){
                return;
            }
        }
        if(!transferHandler.sources.isEmpty() && !transferHandler.destinations.isEmpty()){
            ItemTransferHelper.doSimpleTransfer(transferHandler.sources, transferHandler.destinations, transferHandler.filter, perTick);
        }
    }
}
