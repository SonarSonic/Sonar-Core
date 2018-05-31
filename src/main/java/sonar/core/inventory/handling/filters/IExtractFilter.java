package sonar.core.inventory.handling.filters;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public interface IExtractFilter{

    IExtractFilter BLOCK_EXTRACT = (SLOT,COUNT,SIDE) -> false;

    /**@return true = extraction is allowed! - false = extraction is blocked - null = the filter has no preference**/
    @Nullable
    Boolean canExtract(int slot, int amount, EnumFacing face);

}