package sonar.core.handlers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.utils.ActionType;

public class TileHandlingWrapper implements IEnergyHandler {

    public TileEntity tile;
    public EnumFacing face;
    public ITileEnergyHandler handler;
    public boolean canAdd;
    public boolean canRemove;
    public boolean canRead;
    public EnergyType type;

    public TileHandlingWrapper(TileEntity tile, EnumFacing face, ITileEnergyHandler handler){
        this.tile = tile;
        this.face = face;
        this.handler = handler;
        this.canAdd = handler.canAddEnergy(tile, face);
        this.canRemove = handler.canRemoveEnergy(tile, face);
        this.canRead = handler.canReadEnergy(tile, face);
        this.type = handler.getEnergyType();
    }

    @Override
    public EnumEnergyWrapperType getWrapperType() {
        return EnumEnergyWrapperType.EXTERNAL_TILE;
    }

    @Override
    public EnergyType getEnergyType(){
        return type;
    }

    @Override
    public boolean canAddEnergy(){
        return canAdd;
    }

    @Override
    public boolean canRemoveEnergy(){
        return canRemove;
    }

    @Override
    public boolean canReadEnergy() {
        return canRead;
    }

    @Override
    public long addEnergy(long add, ActionType actionType){
        return handler.addEnergy(add, tile, face, actionType);
    }

    @Override
    public long removeEnergy(long remove, ActionType actionType){
        return handler.removeEnergy(remove, tile, face, actionType);
    }

    @Override
    public long getStored() {
        return handler.getStored(tile, face);
    }

    @Override
    public long getCapacity() {
        return handler.getCapacity(tile, face);
    }
}
