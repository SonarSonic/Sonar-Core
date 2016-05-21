package sonar.core.handlers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.ActionType;
import sonar.core.api.FluidHandler;
import sonar.core.api.InventoryHandler.StorageSize;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredFluidStack;
import sonar.core.integration.AE2Helper;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.IGridProxyable;
import cpw.mods.fml.common.Loader;

public class AE2FluidProvider extends FluidHandler {

	public static String name = "AE2-Fluids";

	@Override
	public String getName() {
		return name;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	@Override
	public boolean canHandleFluids(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IGridProxyable;
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> storedStacks, TileEntity tile, ForgeDirection dir) {
		long maxStorage = 0;
		IGridProxyable proxy = (IGridProxyable) tile;
		final IGrid grid = proxy.getProxy().getNode().getGrid();
		if (grid != null) {
			final IStorageGrid storage = grid.getCache(IStorageGrid.class);
			IItemList<IAEFluidStack> fluids = storage.getFluidInventory().getStorageList();
			if (fluids == null) {
				return StorageSize.EMPTY;
			}
			for (IAEFluidStack fluid : fluids) {
				if (fluid.isFluid() && fluid.getStackSize()!=0) {
					SonarAPI.getFluidHelper().addFluidToList(storedStacks, AE2Helper.convertAEFluidStack(fluid));
					maxStorage += fluid.getStackSize();
				}
			}
		}
		return new StorageSize(maxStorage, maxStorage);
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, ForgeDirection dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		final IGrid grid = proxy.getProxy().getNode().getGrid();
		if (grid != null) {
			final IStorageGrid storage = grid.getCache(IStorageGrid.class);
			IAEFluidStack fluid = storage.getFluidInventory().injectItems(AE2Helper.convertStoredFluidStack(add), AE2Helper.getActionable(action), new MachineSource(((IActionHost) tile)));
			if (fluid == null || fluid.getStackSize() == 0) {
				return null;
			}
			return AE2Helper.convertAEFluidStack(fluid);
		}
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, ForgeDirection dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		final IGrid grid = proxy.getProxy().getNode().getGrid();
		if (grid != null) {
			final IStorageGrid storage = grid.getCache(IStorageGrid.class);
			StoredFluidStack fluid = SonarAPI.getFluidHelper().getStackToAdd(remove.stored, remove, AE2Helper.convertAEItemStack(storage.getFluidInventory().extractItems(AE2Helper.convertStoredFluidStack(remove), AE2Helper.getActionable(action), new MachineSource(((IActionHost) tile)))));
			if (fluid == null || fluid.stored == 0) {
				return null;
			}
			return fluid;
		}
		return remove;
	}

}
