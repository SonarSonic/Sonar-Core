package sonar.core.handlers.energy;

public enum EnumEnergyWrapperType {
    EXTERNAL_TILE,
    EXTERNAL_ITEM,
    INTERNAL_TILE_STORAGE,
    INTERNAL_ITEM_STORAGE;

    public boolean isTile(){
        return this == EXTERNAL_TILE || this == INTERNAL_TILE_STORAGE;
    }

    public boolean isItem(){
        return this == EXTERNAL_ITEM || this == INTERNAL_ITEM_STORAGE;
    }


}
