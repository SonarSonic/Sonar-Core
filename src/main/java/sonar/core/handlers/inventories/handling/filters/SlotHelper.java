package sonar.core.handlers.inventories.handling.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.api.inventories.ISonarInventory;
import sonar.core.handlers.energy.DischargeValues;
import sonar.core.handlers.energy.EnergyTransferHandler;
import sonar.core.handlers.inventories.handling.EnumFilterType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;

public class SlotHelper {

    public static IInsertFilter filterSlot(int slot, Predicate<ItemStack> filter){
        return (SLOT,STACK,FACE) -> SLOT == slot ? filter.test(STACK) : null;
    }

    public static IInsertFilter blockSlot(int slot){
        return (SLOT,STACK,FACE) -> SLOT == slot ? false : null;
    }

    public static IInsertFilter chargeSlot(int slot){
        return (SLOT,STACK,FACE) -> SLOT == slot ? SlotHelper.chargeSlot(STACK) : null;
    }

    public static boolean chargeSlot(ItemStack stack){
        return EnergyTransferHandler.INSTANCE_SC.canAdd(stack);
    }

    public static IInsertFilter dischargeSlot(int slot){
        return (SLOT,STACK,FACE) -> SLOT == slot ? SlotHelper.dischargeSlot(STACK) : null;
    }

    public static boolean dischargeSlot(ItemStack stack){
        return DischargeValues.instance().getValue(stack) > 0 || EnergyTransferHandler.INSTANCE_SC.canRemove(stack);
    }

    public static boolean checkInsert(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing face, EnumFilterType internal, ISonarInventory inv, boolean def){
        boolean validFilters = false;
        boolean insert = def;
        for(Map.Entry<IInsertFilter, EnumFilterType> filter : inv.getInsertFilters().entrySet()){
            if(!filter.getValue().matches(internal)){
                continue;
            }
            validFilters = true;
            Boolean result = filter.getKey().canInsert(slot, stack, face);
            if(result != null){
                if(!result){
                    return false;
                }
                insert = true;
            }
        }
        if(!validFilters){
            return true;
        }
        return insert;
    }

    public static boolean checkExtract(int slot, int count, @Nullable EnumFacing face, EnumFilterType internal, ISonarInventory inv, boolean def){
        boolean validFilters = false;
        boolean extract = def;
        for(Map.Entry<IExtractFilter, EnumFilterType> filter : inv.getExtractFilters().entrySet()){
            if(!filter.getValue().matches(internal)){
                continue;
            }
            validFilters = true;
            Boolean result = filter.getKey().canExtract(slot, count, face);
            if(result != null){
                if(!result){
                    return false;
                }
                extract = true;
            }
        }
        if(!validFilters){
            return true;
        }
        return extract;
    }
}
