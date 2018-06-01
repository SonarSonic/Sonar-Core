package sonar.core.api.energy;

import net.minecraft.nbt.NBTTagCompound;

/** used for the various energy types created by different mods You can create one yourself for custom energy systems and register it with {@link RegistryWrapper} NOTE: this may not accommodate for all energy systems as some have far more to them*/
public enum EnergyType {

	FE("Forge Energy", "FE", "FE/T"),
	TESLA("Tesla", "T", "T/t"),
	RF("Redstone Flux", "RF", "RF/T"),
	EU("Energy Units", "EU", "EU/T"),
	MJ("Minecraft Joules", "J", "J/T"),
	AE("Applied Energistics", "AE", "AE/t");

	private String name = "";
	private String storage = "";
	private String usage = "";

	EnergyType(String name, String storage, String usage) {
		this.name = name;
		this.storage = storage;
		this.usage = usage;
	}

	public String getName() {
		return name;
	}

	public String getStorageSuffix() {
		return storage;
	}

	public String getUsageSuffix() {
		return usage;
	}

	/**COMPATIBLITY ONLY - for energy types which were saved under their storage suffix*/
	public static EnergyType readFromNBT(NBTTagCompound tag, String tagName){
		String storage_name = tag.getString(tagName);
		if(!storage_name.isEmpty()){
			for(EnergyType type : EnergyType.values()){
				if(type.name.equals(storage_name)){
					return type;
				}
			}
			return FE;
		}else{
			int storage_ordinal = tag.getInteger(tagName);
			return EnergyType.values()[storage_ordinal];
		}
	}

	/**COMPATIBLITY ONLY - for energy types which were saved under their storage suffix*/
	public static NBTTagCompound writeToNBT(EnergyType type, NBTTagCompound tag, String tagName){
		tag.setInteger(tagName, type.ordinal());
		return tag;
	}

}
