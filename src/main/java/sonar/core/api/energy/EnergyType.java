package sonar.core.api.energy;

import sonar.core.api.IRegistryObject;
import sonar.core.api.wrappers.RegistryWrapper;

/**
 * used for the various energy types created by different mods You can create one yourself for custom energy systems and register it with {@link RegistryWrapper} NOTE: this may not accommodate for all energy systems as some have far more to them
 */
public class EnergyType implements IRegistryObject {

	public static final EnergyType FE = new EnergyType("Forge Energy", "FE", "FE/T", (double) 1);
	public static final EnergyType TESLA = new EnergyType("Tesla", "T", "T/t", (double) 1);
	public static final EnergyType RF = new EnergyType("Redstone Flux", "RF", "RF/T", (double) 1);
	public static final EnergyType EU = new EnergyType("Energy Units", "EU", "EU/T", (double) 1 / 4);
	public static final EnergyType MJ = new EnergyType("Minecraft Joules", "J", "J/T", (double) 2.5);
	public static final EnergyType AE = new EnergyType("Applied Energistics", "AE", "AE/t", (double) 1 / 2);
	//public static final EnergyType SONAR = new EnergyType("Sonar", "S", "S/t", (double) 1);
	public static final EnergyType[] types = new EnergyType[]{FE, TESLA, RF, EU, MJ, AE};
	private String name = "";
	private String storage = "";
	private String usage = "";
	private double rfConversion = 1;
	private int id;

	public EnergyType(String name, String storage, String usage, double rfConversion) {
		this.name = name;
		this.storage = storage;
		this.usage = usage;
		this.rfConversion = rfConversion;
	}

	public int getID(){
		return id;
	}
	
	@Override
	public boolean isLoadable() {
		return true;
	}

    @Override
	public String getName() {
		return name;
	}

	public String getStorageSuffix() {
		return storage;
	}

	public String getUsageSuffix() {
		return usage;
	}

	public double toRFConversion() {
		return rfConversion;
	}
	
	public boolean equals(Object obj){
        return obj instanceof EnergyType && getName().equals(((EnergyType) obj).getName());
	}
	
	public static EnergyType fromID(int id){
		return types[id];
	}

	public static long convert(long val, EnergyType current, EnergyType type) {
		if(current == type){
			return val;
		}
        double inRF = val / current.toRFConversion();
		return (long) (inRF * type.toRFConversion());
	}
}
