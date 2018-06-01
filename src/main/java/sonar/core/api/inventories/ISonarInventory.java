package sonar.core.api.inventories;

import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.handlers.inventories.handling.filters.IExtractFilter;
import sonar.core.handlers.inventories.handling.filters.IInsertFilter;
import sonar.core.network.sync.ISyncPart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISonarInventory extends INBTSyncable, IItemHandlerModifiable, ISyncPart {

	/**for ISidedInventory*/
	int[] getDefaultSlots();

	IItemHandlerModifiable getItemHandler(EnumFacing side);

	IInventory getWrapperInventory();

	//List<ItemStack> slots();

	Map<IInsertFilter, EnumFilterType> getInsertFilters();

	Map<IExtractFilter, EnumFilterType> getExtractFilters();

	boolean checkInsert(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing face, EnumFilterType internal);

	boolean checkExtract(int slot, int count, @Nullable EnumFacing face, EnumFilterType internal);

	List<ItemStack> getDrops();
}
