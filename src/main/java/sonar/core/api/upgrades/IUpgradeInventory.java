package sonar.core.api.upgrades;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public interface IUpgradeInventory {

	public boolean addUpgrade(ItemStack stack);

	public ItemStack removeUpgrade(String type, int amount);

	public int getUpgradesInstalled(String upgrade);
	
	public ArrayList<String> getAllowedUpgrades();	

	public THashMap<String, Integer> getInstalledUpgrades();
}
