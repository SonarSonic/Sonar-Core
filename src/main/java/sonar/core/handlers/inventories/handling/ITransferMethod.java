package sonar.core.handlers.inventories.handling;

import net.minecraftforge.items.IItemHandler;

public interface ITransferMethod {

    void transfer();

    default void onSourceAdded(IItemHandler source){onSourcesChanged();}

    default void onSourceRemoved(IItemHandler source){onSourcesChanged();}

    default void onDestinationAdded(IItemHandler destination){onDestinationsChanged();}

    default  void onDestinationRemoved(IItemHandler destination){onDestinationsChanged();}

    default void onSourcesChanged(){}

    default void onDestinationsChanged(){}

}
