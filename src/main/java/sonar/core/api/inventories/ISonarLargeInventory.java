package sonar.core.api.inventories;

public interface ISonarLargeInventory extends ISonarInventory {

    long getStackSize(int slot);

    StoredItemStack getStoredStack(int slot);

}