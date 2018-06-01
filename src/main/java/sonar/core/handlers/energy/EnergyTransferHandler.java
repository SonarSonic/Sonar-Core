package sonar.core.handlers.energy;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.utils.ActionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.function.Function;

public class EnergyTransferHandler {

    @Nullable
    public static IItemEnergyHandler getItemHandler(ItemStack stack){
        return null;
    }

    @Nullable
    public static ITileEnergyHandler getTileHandler(TileEntity tile, EnumFacing face){
        return null;
    }

    @Nullable
    public static IEnergyHandler getWrappedItemHandler(ItemStack stack){
        return null;
    }

    @Nullable
    public static IEnergyHandler getWrappedTileHandler(TileEntity tile, EnumFacing face){
        return null;
    }

    public static IEnergyHandler getWrappedStorageHandler(IEnergyStorage storage, EnergyType type){
        return new EnergyStorageWrapper(storage, type);
    }

    public static boolean canAdd(ItemStack stack){
        if(stack.isEmpty()){
            return false;
        }
        IItemEnergyHandler handler = getItemHandler(stack);
        return handler != null && handler.canAddEnergy(stack);
    }

    public static boolean canRemove(ItemStack stack){
        if(stack.isEmpty()){
            return false;
        }
        IItemEnergyHandler handler = getItemHandler(stack);
        return handler != null && handler.canRemoveEnergy(stack);
    }

    public static boolean canRead(ItemStack stack){
        if(stack.isEmpty()){
            return false;
        }
        IItemEnergyHandler handler = getItemHandler(stack);
        return handler != null && handler.canReadEnergy(stack);
    }

    public static boolean canAdd(TileEntity tile, EnumFacing face){
        if(tile == null){
            return false;
        }
        ITileEnergyHandler handler = getTileHandler(tile, face);
        return handler != null && handler.canAddEnergy(tile, face);
    }

    public static boolean canRemove(TileEntity tile, EnumFacing face){
        if(tile == null){
            return false;
        }
        ITileEnergyHandler handler = getTileHandler(tile, face);
        return handler != null && handler.canRemoveEnergy(tile, face);
    }

    public static boolean canRead(TileEntity tile, EnumFacing face){
        if(tile == null){
            return false;
        }
        ITileEnergyHandler handler = getTileHandler(tile, face);
        return handler != null && handler.canReadEnergy(tile, face);
    }

    public static long convertedAction(long toConvert, EnergyType from, EnergyType to, Function<Long, Long> action){
        long maxAdd = EnergyType.convert(toConvert, from, to);
        long added = action.apply(maxAdd);
        return EnergyType.convert(added, to, from);
    }

    public static long doSimpleTransfer(Iterable<IEnergyHandler> sources, Iterable<IEnergyHandler> destinations, long maximum){
        long transferred = 0;
        for(IEnergyHandler source : sources){
            if(source.canRemoveEnergy()){
                long maxRemove = source.removeEnergy(maximum - transferred, ActionType.SIMULATE);
                long removed = 0;
                for(IEnergyHandler destination : destinations){
                    removed += convertedAction(maxRemove - removed, source.getEnergyType(), destination.getEnergyType(), e -> destination.addEnergy(e, ActionType.PERFORM));
                }
                transferred += source.removeEnergy(removed, ActionType.PERFORM);
            }
        }
        return transferred;
    }

    public static long addEnergy(IEnergyHandler handler, long charge, EnergyType energyType, ActionType actionType){
        return convertedAction(charge, energyType, handler.getEnergyType(), e -> handler.addEnergy(e, actionType));
    }

    public static long removeEnergy(IEnergyHandler handler, long charge, EnergyType energyType, ActionType actionType){
        return convertedAction(charge, energyType, handler.getEnergyType(), e -> handler.removeEnergy(e, actionType));
    }

    public static long chargeItem(Iterable<IEnergyHandler> sources, ItemStack stack, long maximum){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canAddEnergy() ? doSimpleTransfer(sources, Lists.newArrayList(itemHandler), maximum) : 0;
    }

    public static long dischargeItem(Iterable<IEnergyHandler> destinations, ItemStack stack, long maximum){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canRemoveEnergy() ? doSimpleTransfer(Lists.newArrayList(itemHandler), destinations, maximum) : 0;
    }

    public static long chargeItem(ItemStack stack, long charge, EnergyType energyType, ActionType actionType){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canAddEnergy() ? addEnergy(itemHandler, charge, energyType, actionType) : 0;
    }

    public static long dischargeItem(ItemStack stack, long discharge, EnergyType energyType, ActionType actionType){
        IEnergyHandler itemHandler = getWrappedItemHandler(stack);
        return itemHandler != null && itemHandler.canRemoveEnergy() ? removeEnergy(itemHandler, discharge, energyType, actionType) : 0;
    }

    public static long getEnergyStored(ItemStack stack, EnergyType energyType){
        IItemEnergyHandler itemHandler = getItemHandler(stack);
        return itemHandler != null && itemHandler.canReadEnergy(stack) ? EnergyType.convert(itemHandler.getStored(stack), itemHandler.getEnergyType(), energyType) : 0;
    }

    public static long getEnergyCapacity(ItemStack stack, EnergyType energyType){
        IItemEnergyHandler itemHandler = getItemHandler(stack);
        return itemHandler != null && itemHandler.canReadEnergy(stack) ? EnergyType.convert(itemHandler.getCapacity(stack), itemHandler.getEnergyType(), energyType) : 0;
    }

    public static void transferToAdjacent(TileEntity tile, Iterable<EnumFacing> faces, long maximum){
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

    public static List<IEnergyHandler> getAdjacentHandlers(World world, BlockPos pos, Iterable<EnumFacing> faces){
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
    public static IEnergyHandler getAdjacentHandler(World world, BlockPos pos, EnumFacing face){
        BlockPos adj = pos.offset(face);
        TileEntity tile = world.getTileEntity(adj);
        if(tile != null){
            IEnergyHandler handler = getWrappedTileHandler(tile, face.getOpposite());
            return handler;
        }
        return null;
    }

}
