package sonar.core.handlers.inventories.handling.methods;

import net.minecraftforge.items.IItemHandler;
import sonar.core.handlers.inventories.handling.ITransferMethod;
import sonar.core.handlers.inventories.handling.ItemTransferHandler;

import javax.annotation.Nullable;
import java.util.Iterator;

public class TransferMethodRoundRobin implements ITransferMethod {

    public final ItemTransferHandler handler;

    //// SOURCES \\\\
    private IItemHandler current_source;
    private int current_source_slot = 0;
    public Iterator<IItemHandler> source_iterator;
    public Iterator<Integer> source_slot_iterator;
    public boolean sources_changed = true;

    //// DESTINATIONS \\\\
    private IItemHandler current_destination;
    private int current_destination_slot = 0;
    public Iterator<IItemHandler> destination_iterator;
    public Iterator<Integer> destination_slot_iterator;
    public boolean destinations_changed = true;

    public TransferMethodRoundRobin(ItemTransferHandler handler){
        this.handler = handler;
    }

    @Override
    public void transfer() {
        if(handler.sources.isEmpty() || handler.destinations.isEmpty()){
           return;
        }
        if(sources_changed){
            source_iterator = getSourceIterator();

            if(source_iterator == null || current_source == null || !handler.sources.contains(current_source)){
                current_source_slot = 0;
            }else{
                source_iterator = null;

            }
        }
        if(destinations_changed){
            if(destination_iterator == null){
                destination_iterator = getSourceIterator();
            }else{

            }
        }

    }

    @Nullable
    private IItemHandler getCurrentSource(){
        if(current_source == null){
            current_source = getSourceIterator().next();
        }
        return current_source;
    }

    @Nullable
    private Iterator<IItemHandler> getSourceIterator() {
        if(source_iterator == null){
            source_iterator = ItemTransferHandler.getItemHandlerIterator(handler.sources);
        }
        return source_iterator;
    }

    @Nullable
    private Iterator<IItemHandler> getDestinationIterator() {
        if(destination_iterator == null){
            destination_iterator = ItemTransferHandler.getItemHandlerIterator(handler.destinations);
        }
        return destination_iterator;
    }

    private void incrementSourceSlot(){

    }

    @Override
    public void onSourcesChanged() {
        sources_changed = true;
    }

    @Override
    public void onDestinationsChanged() {
        destinations_changed = true;
    }


}
