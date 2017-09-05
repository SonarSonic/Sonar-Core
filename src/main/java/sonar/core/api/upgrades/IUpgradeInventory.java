package sonar.core.api.upgrades;

import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import sonar.core.api.nbt.INBTSyncable;

import java.util.ArrayList;

public interface IUpgradeInventory extends INBTSyncable {

    boolean addUpgrade(ItemStack stack);

    ItemStack removeUpgrade(String type, int amount);

    int getUpgradesInstalled(String upgrade);
	
    ArrayList<String> getAllowedUpgrades();

    THashMap<String, Integer> getInstalledUpgrades();
}
