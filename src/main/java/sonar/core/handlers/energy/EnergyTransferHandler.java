package sonar.core.handlers.energy;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.utils.ActionType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EnergyTransferHandler {

    //// THE SONAR CORE TRANSFER HANDLER - ALLOWS ALL TRANSFERS/CONVERSIONS \\\\
    public static final SonarTransferProxy PROXY_SC = new SonarTransferProxy();
    public static final EnergyTransferHandler INSTANCE_SC = new EnergyTransferHandler(PROXY_SC);

    public static class SonarTransferProxy implements IEnergyTransferProxy{

        @Override
        public double getRFConversion(EnergyType type) {
            switch(type){
                case FE:
                    return 1D;
                case TESLA:
                    return 1D;
                case RF:
                    return 1D;
                case EU:
                    return 0.25D;
                case MJ:
                    return 2.5D;
                case AE:
                    return 0.5D;
            }
            return 1D;
        }
    }

    public static long convert(long val, EnergyType from, EnergyType to, IEnergyTransferProxy proxy) {
        if(from == to){
            return val;
        }
        double inRF = val / proxy.getRFConversion(from);
        return (long) (inRF * proxy.getRFConversion(to));
    }

    //// ENERGY TRANSFER HANDLER \\\\

    public IEnergyTransferProxy transferProxy;

    public EnergyTransferHandler(IEnergyTransferProxy transferProxy){
        this.transferProxy = transferProxy;
    }

    public IEnergyTransferProxy getProxy(){
        return transferProxy;
    }

    public long convert(long val, EnergyType from, EnergyType to) {
        return convert(val, from, to, transferProxy);
    }

    @Nullable
    public IItemEnergyHandler getItemHandler(ItemStack stack){
        if(!transferProxy.canConnectItem(stack)){
            return null;
        }
       for(IItemEnergyHandler handler : SonarCore.itemEnergyHandlers){
           if(transferProxy.isItemEnergyTypeEnabled(handler.getEnergyType())) {
               if (handler.canAddEnergy(stack) || handler.canRemoveEnergy(stack)) {
                   return handler;
               }
           }
       }
       return null;
    }

    @Nullable
    public ITileEnergyHandler getTileHandler(TileEntity tile, EnumFacing face){
        if(!transferProxy.canConnectTile(tile, face)){
            return null;
        }
        for(ITileEnergyHandler handler : SonarCore.tileEnergyHandlers){
            if(transferProxy.isTileEnergyTypeEnabled(handler.getEnergyType())) {
                if (handler.canAddEnergy(tile, face) || handler.canRemoveEnergy(tile, face)) {
                    return handler;
                }
            }
        }
        return null;
    }

    @Nullable
    public IEnergyHandler getWrappedItemHandler(ItemStack stack){
        if(stack.isEmpty()){
            return null;
        }
        IItemEnergyHandler handler = getItemHandler(stack);
        if(handler != null){
            return new ItemHandlingWrapper(stack, handler);
        }
        return null;
    }

    @Nullable
    public IEnergyHandler getWrappedTileHandler(TileEntity tile, EnumFacing face){
        if(tile == null){
            return null;
        }
        ITileEnergyHandler handler = getTileHandler(tile, face);
        if(handler != null){
            return new TileHandlingWrapper(tile, face, handler);
        }
        return null;
    }

    public IEnergyHandler getWrappedStorageHandler(IEnergyStorage storage, EnumEnergyWrapperType wrapperType, EnergyType type){
        return new EnergyStorageWrapper(storage, wrapperType, type);
    }

    public boolean canAdd(ItemStack stack){
        if(stack.isEmpty()){
            return false;
        }
        IItemEnergyHandler handler = getItemHandler(stack);
        return handler != null && handler.canAddEnergy(stack);
    }

    public boolean canRemove(ItemStack stack){
        if(stack.isEmpty()){
            return false;
        }
        IItemEnergyHandler handler = getItemHandler(stack);
        return handler != null && handler.canRemoveEnergy(stack);
    }

    public boolean canRead(ItemStack stack){
        if(stack.isEmpty()){
            return false;
        }
        IItemEnergyHandler handler = getItemHandler(stack);
        return handler != null && handler.canReadEnergy(stack);
    }

    public boolean canAdd(TileEntity tile, EnumFacing face){
        if(tile == null){
            return false;
        }
        ITileEnergyHandler handler = getTileHandler(tile, face);
        return handler != null && handler.canAddEnergy(tile, face);
    }

    public boolean canRemove(TileEntity tile, EnumFacing face){
        if(tile == null){
            return false;
        }
        ITileEnergyHandler handler = getTileHandler(tile, face);
        return handler != null && handler.canRemoveEnergy(tile, face);
    }

    public boolean canRead(TileEntity tile, EnumFacing face){
        if(tile == null){
            return false;
        }
        ITileEnergyHandler handler = getTileHandler(tile, face);
        return handler != null && handler.canReadEnergy(tile, face);
    }

    public boolean canRenderConnection(TileEntity tile, EnumFacing face){
        if(tile == null || !transferProxy.canConnectTile(tile, face)){
            return false;
        }
        for(ITileEnergyHandler handler : SonarCore.tileEnergyHandlers){
            if(transferProxy.isTileEnergyTypeEnabled(handler.getEnergyType()) && handler.canRenderConnection(tile, face)) {
                return true;
            }
        }
        return false;
    }

    public long convertedAction(long toConvert, EnergyType from, EnergyType to, Function<Long, Long> action){
        long maxAdd = convert(toConvert, from, to, transferProxy);
        long added = action.apply(maxAdd);
        return convert(added, to, from, transferProxy);
    }

    public long doSimpleTransfer(Iterable<IEnergyHandler> sources, Iterable<IEnergyHandler> destinations, long maximum){
        long transferred = 0;
        for(IEnergyHandler source : sources){
            if(source.canRemoveEnergy()){
                long maxRemove = source.removeEnergy(maximum - transferred, ActionType.SIMULATE);
                long removed = 0;
                for(IEnergyHandler destination : destinations){
                    if(transferProxy.canConvert(source, destination) && transferProxy.canConvert(source.getEnergyType(), destination.getEnergyType())) {
                        removed += convertedAction(maxRemove - removed, source.getEnergyType(), destination.getEnergyType(), e -> destination.addEnergy(e, ActionType.PERFORM));
                    }
                }
                transferred += source.removeEnergy(removed, ActionType.PERFORM);
            }
        }
        return transferred;
    }

    public long addEnergy(IEnergyHandler handler, long charge, EnergyType energyType, ActionType actionType){
        if(transferProxy.canConvert(energyType, handler.getEnergyType())){
            return convertedAction(charge, energyType, handler.getEnergyType(), e -> handler.addEnergy(e, actionType));
        }
        return 0;
    }

    public long removeEnergy(IEnergyHandler handler, long charge, EnergyType energyType, ActionType actionType){
        if(transferProxy.canConvert(energyType, handler.getEnergyType())) {
            return convertedAction(charge, energyType, handler.getEnergyType(), e -> handler.removeEnergy(e, actionType));
        }
        return 0;
    }

    public long chargeItem(Iterable<IEnergyHandler> sources, ItemStack stack, long maximum){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canAddEnergy() ? doSimpleTransfer(sources, Lists.newArrayList(itemHandler), maximum) : 0;
    }

    public long dischargeItem(Iterable<IEnergyHandler> destinations, ItemStack stack, long maximum){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canRemoveEnergy() ? doSimpleTransfer(Lists.newArrayList(itemHandler), destinations, maximum) : 0;
    }

    public long chargeItem(ItemStack stack, long charge, EnergyType energyType, ActionType actionType){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canAddEnergy() ? addEnergy(itemHandler, charge, energyType, actionType) : 0;
    }

    public long dischargeItem(ItemStack stack, long discharge, EnergyType energyType, ActionType actionType){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canRemoveEnergy() ? removeEnergy(itemHandler, discharge, energyType, actionType) : 0;
    }

    public long getEnergyStored(ItemStack stack, EnergyType energyType){
        IItemEnergyHandler itemHandler = getItemHandler(stack);
        return itemHandler != null && itemHandler.canReadEnergy(stack) ? convert(itemHandler.getStored(stack), itemHandler.getEnergyType(), energyType, transferProxy) : 0;
    }

    public long getEnergyCapacity(ItemStack stack, EnergyType energyType){
        IItemEnergyHandler itemHandler = getItemHandler(stack);
        return itemHandler != null && itemHandler.canReadEnergy(stack) ? convert(itemHandler.getCapacity(stack), itemHandler.getEnergyType(), energyType, transferProxy) : 0;
    }

    public void transferToAdjacent(TileEntity tile, Iterable<EnumFacing> faces, long maximum){
        long transferred = 0;
        for(EnumFacing face : faces){
            IEnergyHandler handler = getWrappedTileHandler(tile, face);
            IEnergyHandler adjacent = getAdjacentHandler(tile.getWorld(), tile.getPos(), face);
            if(handler != null && adjacent != null) {
                transferred += doSimpleTransfer(Lists.newArrayList(handler), Lists.newArrayList(adjacent), maximum - transferred);
            }
        }
        getAdjacentHandlers(tile.getWorld(), tile.getPos(), faces);
    }

    public List<IEnergyHandler> getAdjacentHandlers(World world, BlockPos pos, Iterable<EnumFacing> faces){
        List<IEnergyHandler> handlers = new ArrayList<>();
        for(EnumFacing face : faces){
            IEnergyHandler handler = getAdjacentHandler(world, pos, face);
            if(handler != null){
                handlers.add(handler);
            }
        }
        return handlers;
    }

    @Nullable
    public IEnergyHandler getAdjacentHandler(World world, BlockPos pos, EnumFacing face){
        BlockPos adj = pos.offset(face);
        TileEntity tile = world.getTileEntity(adj);
        if(tile != null){
            IEnergyHandler handler = getWrappedTileHandler(tile, face.getOpposite());
            return handler;
        }
        return null;
    }

}
