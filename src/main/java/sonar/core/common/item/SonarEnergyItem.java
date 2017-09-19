package sonar.core.common.item;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.energy.ISonarEnergyItem;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.FontHelper;
import sonar.core.network.sync.SyncItemEnergyStorage;

import java.util.List;

@Optional.InterfaceList({@Optional.Interface(iface = "cofh.api.energy.IEnergyContainerItem", modid = "redstoneflux")})
public class SonarEnergyItem extends SonarItem implements ISonarEnergyItem, IEnergyContainerItem {

	public SyncItemEnergyStorage storage;
	public int capacity, maxReceive, maxExtract;

	public SonarEnergyItem(int capacity, int maxReceive, int maxExtract) {
		super();
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		storage = new SyncItemEnergyStorage(null, capacity, maxReceive, maxExtract);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
		if (stack != null)
			list.add(FontHelper.translate("energy.stored") + ": " + getEnergyLevel(stack) + " RF");
	}

	/////* SONAR *//////	
	@Override
	public long addEnergy(ItemStack stack, long maxReceive, ActionType action) {
		storage.setItemStack(stack);
		return storage.addEnergy(maxReceive, action);
	}

	@Override
	public long removeEnergy(ItemStack stack, long maxExtract, ActionType action) {
		storage.setItemStack(stack);
		return storage.removeEnergy(maxExtract, action);
	}

	@Override
	public long getEnergyLevel(ItemStack stack) {
		storage.setItemStack(stack);
		return storage.getEnergyLevel();
	}

	@Override
	public long getFullCapacity(ItemStack stack) {
		storage.setItemStack(stack);
		return storage.getFullCapacity();
	}

	/////* CoFH *//////
	@Override
    @Optional.Method(modid = "redstoneflux")
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
		storage.setItemStack(container);
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
		storage.setItemStack(container);
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int getEnergyStored(ItemStack container) {
		storage.setItemStack(container);
		return storage.getEnergyStored();
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int getMaxEnergyStored(ItemStack container) {
		storage.setItemStack(container);
		return storage.getMaxEnergyStored();
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		storage = new SyncItemEnergyStorage(null, capacity, maxReceive, maxExtract);
		return storage;
	}
}
