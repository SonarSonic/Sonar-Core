package sonar.core.handlers.inventories.handling;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import sonar.core.helpers.ListHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ItemTransferHandler {

    public final List<IItemHandler> sources = new ArrayList<>();
    public final List<IItemHandler> destinations = new ArrayList<>();
    public ITransferMethod method;
    public Predicate<ItemStack> filter;

    public ItemTransferHandler(){}

    public ItemTransferHandler setMethod(ITransferMethod method){
        this.method = method;
        return this;
    }

    public ItemTransferHandler setFilter(Predicate<ItemStack> filter){
        this.filter = filter;
        return this;
    }

    public void transfer(){
        method.transfer();
    }

    public ItemTransferHandler addSource(IItemHandler source){
        ListHelper.addWithCheck(sources, source);
        method.onSourceAdded(source);
        return this;
    }

    public ItemTransferHandler removeSource(IItemHandler source){
        sources.remove(source);
        method.onSourceRemoved(source);
        return this;
    }

    public ItemTransferHandler addDestination(IItemHandler destination){
        ListHelper.addWithCheck(destinations, destination);
        method.onDestinationAdded(destination);
        return this;
    }

    public ItemTransferHandler removeDestination(IItemHandler destination){
        destinations.remove(destination);
        method.onDestinationRemoved(destination);
        return this;
    }

    public void clear(){
        sources.clear();
        destinations.clear();
    }

    public static Iterator<Integer> getSlotIterator(IItemHandler handler){
        return new SlotIterator(handler);
    }

    public static Iterator<IItemHandler> getItemHandlerIterator(List<IItemHandler> handlers){
        if(handlers.size() == 1){
            return new Iterator<IItemHandler>() {
                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public IItemHandler next() {
                    return handlers.get(0);
                }
            };
        }
        return handlers.iterator();
    }

}
