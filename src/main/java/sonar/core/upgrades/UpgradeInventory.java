package sonar.core.upgrades;

import java.util.ArrayList;
import java.util.Map.Entry;

import gnu.trove.map.hash.THashMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.SonarCore;
import sonar.core.api.upgrades.IUpgradeInventory;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncPart;

public class UpgradeInventory extends SyncPart implements IUpgradeInventory {

	public ArrayList<String> allowed = new ArrayList();
	public THashMap<String, Integer> upgrades = new THashMap<String, Integer>();
	public THashMap<String, Integer> maxUpgrades = new THashMap<String, Integer>();
	//public boolean markDirty = true;

	public UpgradeInventory(int syncID, int max, Object... allowed) {
		super(syncID);		
		this.setAllowed(max, allowed);
		if (allowed.length == 0) {
			SonarCore.logger.warn("Upgradable Inventory: has no allowed Upgrade Types");
		}
	}
	
	public UpgradeInventory setAllowed(int max, Object... allowed){
		this.allowed.clear();
		for (Object object : allowed) {
			if (object != null && object instanceof String) {
				this.allowed.add((String) object);
			}
		}
		for (String type : this.allowed) {
			this.upgrades.put(type, 0);
			this.maxUpgrades.put(type, max);
		}
		return this;
	}

	public UpgradeInventory addMaxiumum(String type, int max) {
		maxUpgrades.put(type, max);
		return this;
	}

	public boolean addUpgrade(ItemStack stack) {
		if (!stack.isEmpty()) {
			String upgrade = SonarCore.machineUpgrades.getSecondaryObject(stack.getItem());
			if (upgrade != null) {
				if (allowed.contains(upgrade) && maxUpgrades.get(upgrade).intValue() != upgrades.get(upgrade).intValue()) {
					upgrades.put(upgrade, Integer.valueOf(upgrades.get(upgrade) + 1));
					this.markChanged();
					return true;
				}
			}
		}
		return false;
	}

	public ItemStack removeUpgrade(String type, int amount) {
		if (upgrades.containsKey(type)) {
			int stored = upgrades.get(type);
			if (stored != 0) {
				int remove = Math.min(amount, stored);
				Item item = SonarCore.machineUpgrades.getPrimaryObject(type);
				if (item != null) {
					upgrades.put(type, stored - remove);
					return new ItemStack(item, remove);
				}
			}
		}
		return null;
	}

	public int getUpgradesInstalled(String upgrade) {
		if (upgrades.get(upgrade) == null) {
			return 0;
		} else {
			return upgrades.get(upgrade);
		}
	}

	public ArrayList<ItemStack> getDrops() {
		ArrayList<ItemStack> drops = new ArrayList();
		for (Entry<String, Integer> entry : upgrades.entrySet()) {
			Item item = SonarCore.machineUpgrades.getPrimaryObject(entry.getKey());
			if (item != null && entry.getValue() != 0) {
				drops.add(new ItemStack(item, entry.getValue()));
				upgrades.put(entry.getKey(), 0);
			}
		}
		this.markChanged();
		return drops;
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			NBTTagCompound upgradeTag = new NBTTagCompound();
			for (Entry<String, Integer> entry : upgrades.entrySet()) {
				upgradeTag.setInteger(entry.getKey(), entry.getValue());
			}
			if (!upgradeTag.hasNoTags()) {
				nbt.setTag("Upgrades", upgradeTag);
			}
		}
		return nbt;
	}

	@Override
	public void readData(NBTTagCompound tag, SyncType type) {
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			NBTTagCompound upgradeTag = tag.getCompoundTag("Upgrades");
			if (upgradeTag != null && !upgradeTag.hasNoTags()) {
				for (String key : upgradeTag.getKeySet()) {
					upgrades.put(key, upgradeTag.getInteger(key));
				}
			}
		}
	}

	@Override
	public ArrayList<String> getAllowedUpgrades() {
		return allowed;
	}

	@Override
	public THashMap<String, Integer> getInstalledUpgrades() {
		return upgrades;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

}
