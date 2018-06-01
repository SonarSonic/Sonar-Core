package sonar.core.handlers.inventories.handling.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.SonarHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotFilter implements IInsertFilter, IExtractFilter {

    public int[] filtered;
    @Nullable
    public Boolean defaultReturn;
    public EnumFacing[] faces;

    public SlotFilter(Boolean defaultReturn, int[] filtered){
        this.defaultReturn = defaultReturn;
        this.filtered = filtered;
        this.faces = null;
    }

    public SlotFilter(Boolean defaultReturn, int[] filtered, int[] faces){
        this.defaultReturn = defaultReturn;
        this.filtered = filtered;
        EnumFacing[] newFaces = new EnumFacing[faces.length];
        for(int i = 0; i < faces.length; i++){
            newFaces[i] = EnumFacing.values()[faces[i]];
        }
        this.faces = newFaces;
    }

    public SlotFilter(Boolean defaultReturn, int[] filtered, EnumFacing... faces){
        this.defaultReturn = defaultReturn;
        this.filtered = filtered;
        this.faces = faces;
    }

    public boolean checkSlot(int slot){
        return SonarHelper.intContains(filtered, slot);
    }

    public boolean checkFace(EnumFacing face){
        return (this.faces == null || face == null || SonarHelper.arrayContains(faces, face));
    }

    public boolean checkFilter(int slot, EnumFacing face){
        return ((this.faces == null || face == null || SonarHelper.arrayContains(faces, face)) && SonarHelper.intContains(filtered, slot));
    }

    /**checks if this slot filter should be used for the given face before checking the filter,
     * returns true if the filter doesn't effect the given face*/
    public boolean checkFilterSafe(int slot, EnumFacing face){
        if(checkFace(face)){
            return checkSlot(slot);
        }
        return true;
    }

    @Override
    public Boolean canExtract(int slot, int amount, EnumFacing face) {
        if(checkFace(face)){
            return checkSlot(slot);
        }
        return defaultReturn;
    }

    @Override
    public Boolean canInsert(int slot, @Nonnull ItemStack stack, EnumFacing face) {
        if(checkFace(face)){
            return checkSlot(slot);
        }
        return defaultReturn;
    }
}
