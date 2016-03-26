package sonar.core.api;

public class RegistryWrapper {

	/** used for registering Energy Types, this should have a unique storage suffix!
	 * @param info {@link EnergyType} to register */
	public void registerEnergyType(EnergyType type) {
	}

	/** used for registering Inventory handlers, this should have a unique id!
	 * @param handler {@link InventoryHandler} to register */
	public void registerInventoryHandler(InventoryHandler handler) {
	}

	/** used for registering Fluid handlers, this should have a unique id!
	 * @param handler {@link FluidHandler} to register */
	public void registerFluidHandler(FluidHandler handler) {
	}

	/** used for registering Fluid handlers, this should have a unique id!
	 * @param handler {@link EnergyHandler} to register */
	public void registerEnergyHandler(EnergyHandler handler) {
	}

	/** gets the {@link EnergyType} for the given storage suffix
	 * @param id the id to check
	 * @return the {@link EnergyType}, can be null */
	public EnergyType getEnergyType(String storage) {
		return null;
	}

	/** gets the {@link InventoryHandler} for the given id
	 * @param id the id to check
	 * @return the {@link InventoryHandler}, can be null */
	public InventoryHandler getInventoryHandler(int id) {
		return null;
	}

	/** gets the {@link FluidHandler} for the given id
	 * @param id the id to check
	 * @return the {@link FluidHandler}, can be null */
	public FluidHandler getFluidHandler(int id) {
		return null;
	}

	/** gets the {@link EnergyHandler} for the given id
	 * @param id the id to check
	 * @return the {@link EnergyHandler}, can be null */
	public EnergyHandler getEnergyHandler(int id) {
		return null;
	}

	/** gets the {@link InventoryHandler} id from its name
	 * @param name the name to check
	 * @return the id of the {@link InventoryHandler} */
	public int getInventorHandlerID(String name) {
		return -1;
	}

	/** gets the {@link FluidHandler} id from its name
	 * @param name the name to check
	 * @return the id of the {@link FluidHandler} */
	public int getFluidHandlerID(String name) {
		return -1;
	}

	/** gets the {@link EnergyProvider} id from its name
	 * @param name the name to check
	 * @return the id of the {@link EnergyProvider} */
	public int getEnergyHandlerID(String name) {
		return -1;
	}
}
