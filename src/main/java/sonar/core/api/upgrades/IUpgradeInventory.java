package sonar.core.api.upgrades;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;

import sonar.core.api.nbt.INBTSyncable;
import net.minecraft.item.ItemStack;

public interface IUpgradeInventory extends INBTSyncable {

	public boolean addUpgrade(ItemStack stack);

	public ItemStack removeUpgrade(String type, int amount);

	public int getUpgradesInstalled(String upgrade);
	
	public ArrayList<String> getAllowedUpgrades();	

	public THashMap<String, Integer> getInstalledUpgrades();
}
