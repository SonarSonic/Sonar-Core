package sonar.core.utils;

import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;

/** Manages energy storage on items for Industrial Craft 2 */
@Optional.Interface(iface = "ic2.api.item.IElectricItemManager", modid = "IC2", striprefs = true)
public class SonarElectricManager implements IElectricItemManager {

	@Method(modid = "IC2")
	@Override
	public double charge(ItemStack stack, double amount, int tier,
			boolean ignoreTransferLimit, boolean simulate) {

		if (stack.getItem() instanceof IEnergyContainerItem) {
			IEnergyContainerItem container = (IEnergyContainerItem) stack.getItem();

			if (stack.getTagCompound() == null) {
				stack.stackTagCompound = new NBTTagCompound();
			}
			int energyEU = stack.getTagCompound().getInteger("Energy") / 4;
			int energyRF = stack.getTagCompound().getInteger("Energy");
			IElectricItem item = (IElectricItem) stack.getItem();
			int newamount = (int) Math.min(item.getTransferLimit(stack), amount);
			int energyReceived = (int) Math.min((container.getMaxEnergyStored(stack) / 4) - energyEU, Math.min(container.receiveEnergy(stack,(int) newamount * 4, true) / 4, newamount));

			if (!simulate) {
				energyRF += energyReceived * 4;
				stack.getTagCompound().setInteger("Energy", energyRF);
			}
			return energyReceived;
		}
		return 0;
	}

	@Method(modid = "IC2")
	@Override
	public double discharge(ItemStack stack, double amount, int tier,
			boolean ignoreTransferLimit, boolean externally, boolean simulate) {
		if (stack.getItem() instanceof IEnergyContainerItem) {
			IEnergyContainerItem container = (IEnergyContainerItem) stack.getItem();
			if ((stack.getTagCompound() == null)|| (!stack.getTagCompound().hasKey("Energy"))) {
				return 0;
			}
			int energyEU = stack.getTagCompound().getInteger("Energy") / 4;
			int energyRF = stack.getTagCompound().getInteger("Energy");
			IElectricItem item = (IElectricItem) stack.getItem();
			int newamount = (int) Math.min(item.getTransferLimit(stack), amount);

			int energyExtracted = (int) Math.min(energyEU, Math.min(container.extractEnergy(stack, newamount * 4, true) / 4,newamount));

			if (!simulate) {
				energyRF -= energyExtracted * 4;
				stack.getTagCompound().setInteger("Energy", energyRF);
			}

			return energyExtracted;
		}
		return 0;
	}

	@Method(modid = "IC2")
	@Override
	public double getCharge(ItemStack stack) {

		if (stack.getItem() instanceof IEnergyContainerItem) {
			IEnergyContainerItem container = (IEnergyContainerItem) stack.getItem();
			int stored = container.getEnergyStored(stack) / 4;
			return stored;

		}

		return 0;
	}

	@Method(modid = "IC2")
	@Override
	public boolean canUse(ItemStack stack, double amount) {
		return true;
	}

	@Method(modid = "IC2")
	@Override
	public boolean use(ItemStack stack, double amount, EntityLivingBase entity) {
		return true;
	}

	@Method(modid = "IC2")
	@Override
	public void chargeFromArmor(ItemStack stack, EntityLivingBase entity) {

	}

	@Method(modid = "IC2")
	@Override
	public String getToolTip(ItemStack stack) {
		return null;
	}

}
