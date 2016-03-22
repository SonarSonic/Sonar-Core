package sonar.core.energy;

import net.minecraft.item.ItemStack;


/**for use with charging/discharging methods, an ItemStack to charge/discharge and if the stack was used up*/
public class EnergyCharge extends Object {
	private int charge;
	private ItemStack item;
	private boolean stackUsed;


	/**@param charge amount of energy added/removed
	 * @param itemEnergy ItemStack to charge/discharge
	 * @param stackUsed if the stack size was reduced*/
	public EnergyCharge(int charge, ItemStack itemEnergy, boolean stackUsed) {
		this.charge = charge;
		this.item = itemEnergy;
		this.stackUsed=stackUsed;
	}

	/**@return amount of energy added/removed*/
	public int getEnergyUsage(){
		return this.charge;
	}

	/**@return charged/discharged ItemStack*/
	public ItemStack getEnergyStack(){
		return this.item;
	}

	/**@return if the stack had a discharge value*/
	public boolean stackUsed(){
		return this.stackUsed;
	}

}
