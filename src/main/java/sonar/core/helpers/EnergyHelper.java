package sonar.core.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.ActionType;
import sonar.core.api.EnergyHandler;
import sonar.core.api.EnergyWrapper;
import sonar.core.api.InventoryHandler;
import sonar.core.api.StoredEnergyStack;
import sonar.core.api.StoredItemStack;

public class EnergyHelper extends EnergyWrapper {

	//VERY FAR FROM COMPLETION, HELP OUT IF YOU CAN!!!
	
	public StoredEnergyStack getStackToAdd(long inputSize, StoredEnergyStack stack, StoredEnergyStack returned) {
		StoredEnergyStack simulateStack = null;
		if (returned == null || returned.stored == 0) {
			simulateStack = stack.copy().setStackSize(inputSize);
		} else {
			simulateStack = stack.copy().setStackSize(inputSize - returned.stored);
		}
		return simulateStack;
	}

	public StoredEnergyStack addEnergy(TileEntity tile, StoredEnergyStack stack, EnumFacing dir, ActionType type) {
		if (stack == null) {
			return stack;
		}
		if (tile != null) {
			List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects();
			for (EnergyHandler handler : handlers) {
				if (handler.canProvideEnergy(tile, dir)) {
					StoredEnergyStack returned = handler.addEnergy(stack.copy(), tile, dir, type);
					StoredEnergyStack add = getStackToAdd(stack.getStackSize(), stack.copy(), returned);
					return add;
				}
			}
		}
		return stack;
	}

	public StoredEnergyStack removeEnergy(TileEntity tile, StoredEnergyStack stack, EnumFacing dir, ActionType type) {
		if (stack == null) {
			return stack;
		}
		if (tile != null) {
			List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects();
			for (EnergyHandler handler : handlers) {
				if (handler.canProvideEnergy(tile, dir)) {
					StoredEnergyStack returned = handler.removeEnergy(stack.convert(handler.getProvidedType()), tile, dir, type).convert(stack.energyType);
					StoredEnergyStack add = getStackToAdd(stack.getStackSize(), stack.copy(), returned);
					return add;
				}
			}
		}
		return null;
	}

	public void transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo) {
		if (from != null && to != null) {
			/*StoredEnergyStack energy = null; List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects(); for (EnergyHandler handler : handlers) { if (handler.canProvideEnergy(from, dirFrom)) { energy = new StoredEnergyStack(handler.getProvidedType()); handler.getEnergy(energy, from, dirFrom); continue; } } if (energy != null) { */
			/*
			StoredEnergyStack removed = removeEnergy(from, energy.copy(), dirFrom, ActionType.SIMULATE);
			if (removed != null) {
				StoredEnergyStack add = addEnergy(to, removed.copy(), dirTo, ActionType.SIMULATE);
				if (add != null) {
					removeEnergy(from, add.copy(), dirFrom, ActionType.PERFORM);
					addEnergy(to, add.copy(), dirTo, ActionType.PERFORM);
				}
			}
		*/
		}
	}
}
