package sonar.core.handlers.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;

public interface IEnergyTransferProxy {

    double getRFConversion(EnergyType type);

    default boolean isItemEnergyTypeEnabled(EnergyType type){
        return true;
    }

    default boolean isTileEnergyTypeEnabled(EnergyType type){
        return true;
    }

    default boolean canConnectTile(TileEntity tile, EnumFacing face){
        return true;
    }

    default boolean canConnectItem(ItemStack stack){
        return true;
    }

    default boolean canConvert(IEnergyHandler to, IEnergyHandler from){
        return true;
    }

    default boolean canConvert(EnergyType to, EnergyType from){
        return true;
    }

}
