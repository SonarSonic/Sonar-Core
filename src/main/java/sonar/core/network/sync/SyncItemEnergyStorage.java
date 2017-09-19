package sonar.core.network.sync;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import sonar.core.integration.SonarLoader;

public class SyncItemEnergyStorage extends SyncEnergyStorage implements ICapabilityProvider {

	protected ItemStack stack;

	public SyncItemEnergyStorage(ItemStack stack, int capacity) {
		super(capacity);
		this.stack = stack;
	}

	public SyncItemEnergyStorage(ItemStack stack, int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
		this.stack = stack;
	}

	public SyncItemEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
		this.stack = stack;
	}

	public SyncItemEnergyStorage setItemStack(ItemStack stack) {
		if (!stack.isEmpty()) {
			this.stack = stack;
			if (stack.hasTagCompound()){
				readFromNBT(stack.getTagCompound());
			}
		}
		return this;
	}
	
	@Override
	public void markChanged() {
		super.markChanged();
		if (!stack.isEmpty()){
			if(!stack.hasTagCompound()){
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.setTagCompound(writeToNBT(stack.getTagCompound()));
		}
	}

    @Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (SonarLoader.teslaLoaded) {
            if (capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return true;
		}
		return false;
	}

    @Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (SonarLoader.teslaLoaded) {
            if (capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == TeslaCapabilities.CAPABILITY_HOLDER)
				return (T) this;
		}
		return null;
	}
}
