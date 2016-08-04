package sonar.core.handlers.container;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import sonar.core.api.energy.EnergyContainerHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class RFItemHandler extends EnergyContainerHandler {

	@Override
	public String getName() {
		return "RF Item Handler";
	}

	@Override
	public boolean canHandleItem(ItemStack stack) {
		return stack.getItem() != null && stack.getItem() instanceof IEnergyContainerItem;
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
		if (item instanceof IEnergyContainerItem) {
			IEnergyContainerItem receiver = (IEnergyContainerItem) item;
			if (receiver.getMaxEnergyStored(stack) > 0) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= receiver.receiveEnergy(stack, transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, ItemStack stack, ActionType action) {
		IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
		if (item instanceof IEnergyContainerItem) {
			IEnergyContainerItem receiver = (IEnergyContainerItem) item;
			if (receiver.getMaxEnergyStored(stack) > 0) {
				int transferRF = transfer.stored < Integer.MAX_VALUE ? (int) transfer.stored : Integer.MAX_VALUE;
				transfer.stored -= receiver.extractEnergy(stack, transferRF, action.shouldSimulate());
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, ItemStack stack) {
		IEnergyContainerItem item = (IEnergyContainerItem) stack.getItem();
		if (item == null) {
			return;
		}
		if (item instanceof IEnergyContainerItem) {
			if (item.getMaxEnergyStored(stack) > 0) {
				IEnergyContainerItem receiver = (IEnergyContainerItem) item;
				energyStack.setStorageValues(receiver.getEnergyStored(stack), receiver.getMaxEnergyStored(stack));
				energyStack.setMaxInput(receiver.receiveEnergy(stack, Integer.MAX_VALUE, true));
				energyStack.setMaxOutput(receiver.extractEnergy(stack, Integer.MAX_VALUE, true));
			}
		}
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}

}
