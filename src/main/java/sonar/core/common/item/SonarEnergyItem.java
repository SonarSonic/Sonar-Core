package sonar.core.common.item;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.client.util.ITooltipFlag;
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

@Optional.InterfaceList({@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = "redstoneflux")})
public class SonarEnergyItem extends SonarItem implements ISonarEnergyItem, IEnergyContainerItem {

	public SyncItemEnergyStorage storage;
	public int capacity, maxReceive, maxExtract;

	public SonarEnergyItem(int capacity, int maxReceive, int maxExtract) {
		super();
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		storage = new SyncItemEnergyStorage(ItemStack.EMPTY, capacity, maxReceive, maxExtract);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
        super.addInformation(stack, world, list, par4);
		if (stack != null)
			list.add(FontHelper.translate("energy.stored") + ": " + getEnergyLevel(stack) + " RF");
	}

	/////* SONAR *//////	
	@Override
	public long addEnergy(ItemStack stack, long maxReceive, ActionType action) {
		return storage.setItemStack(stack).getInternalWrapper().addEnergy(maxReceive, action);
	}

	@Override
	public long removeEnergy(ItemStack stack, long maxExtract, ActionType action) {
		return storage.setItemStack(stack).getInternalWrapper().removeEnergy(maxExtract,  action);
	}

	@Override
	public long getEnergyLevel(ItemStack stack) {
		return storage.setItemStack(stack).getEnergyLevel();
	}

	@Override
	public long getFullCapacity(ItemStack stack) {
		return storage.setItemStack(stack).getFullCapacity();
	}

	/////* CoFH *//////
	@Override
    @Optional.Method(modid = "redstoneflux")
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
		return storage.setItemStack(container).getInternalWrapper().receiveEnergy(maxReceive, simulate);
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
		return storage.setItemStack(container).getInternalWrapper().extractEnergy(maxExtract, simulate);
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int getEnergyStored(ItemStack container) {
		return storage.setItemStack(container).getInternalWrapper().getEnergyStored();
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int getMaxEnergyStored(ItemStack container) {
		return storage.setItemStack(container).getInternalWrapper().getMaxEnergyStored();
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		storage = new SyncItemEnergyStorage(stack, capacity, maxReceive, maxExtract);
		return storage;
	}
}
