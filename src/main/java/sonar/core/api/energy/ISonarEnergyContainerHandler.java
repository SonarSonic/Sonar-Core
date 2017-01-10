package sonar.core.api.energy;

import net.minecraft.item.ItemStack;
import sonar.core.api.utils.ActionType;

public interface ISonarEnergyContainerHandler {

	//public int getID() {
	//	return SonarAPI.getRegistry().getEnergyHandlerID(getName());
	//}
	
	/**the {@link EnergyType} this provider can handle*/
	public EnergyType getProvidedType();

	/** @param stack the {@link ItemStack} to check
	 * @return can this provider handle energy in the ItemStack */
	public boolean canHandleItem(ItemStack stack);

	/** used for adding an a {@link StoredEnergyStack} to the TileEntity
	 * @param add the {@link StoredEnergyStack} to add, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link ItemStack} to check
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action);

	/** used for removing an a {@link StoredEnergyStack} from the Inventory
	 * @param remove the {@link StoredEnergyStack} to remove, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link ItemStack} to check
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action);

	/** only called if canProvideEnergy is true
	 * @param storedStacks current list of energy for the block, providers only add to this and don't remove.
	 * @param tile the {@link ItemStack} to check */
	public void getEnergy(StoredEnergyStack energyStack, ItemStack stack);

}