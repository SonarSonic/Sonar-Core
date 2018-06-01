package sonar.core.handlers.inventories.handling.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IInsertFilter{

    IInsertFilter BLOCK_INSERT = (SLOT,STACK,SIDE) -> false;

    /**@return true = insertion is allowed! - false = insertion is blocked - null = the filter has no preference**/
    @Nullable
    Boolean canInsert(int slot, @Nonnull ItemStack stack, EnumFacing face);

}

