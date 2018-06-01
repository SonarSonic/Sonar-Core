package sonar.core.handlers.inventories.handling.methods;

import sonar.core.handlers.inventories.handling.ITransferMethod;
import sonar.core.handlers.inventories.handling.ItemTransferHandler;
import sonar.core.handlers.inventories.handling.ItemTransferHelper;

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
