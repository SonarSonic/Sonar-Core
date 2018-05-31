package sonar.core.inventory.handling;

import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;

public class SlotIterator implements Iterator<Integer> {

    public final IItemHandler handler;
    public int count = 0;

    public SlotIterator(IItemHandler handler){
        this.handler = handler;
    }

    @Override
    public boolean hasNext() {
        return count + 1 < handler.getSlots();
    }

    @Override
    public Integer next() {
        return count++;
    }
}
