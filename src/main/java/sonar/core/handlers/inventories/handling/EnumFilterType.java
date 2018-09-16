package sonar.core.handlers.inventories.handling;

public enum EnumFilterType {
    EXTERNAL,
    INTERNAL,
    EXTERNAL_INTERNAL;

    public boolean matches(EnumFilterType type) {
        if (this == EnumFilterType.EXTERNAL_INTERNAL) {
            return true;
        }
        return this == type;
    }

    public boolean isInternal(){
        return this == INTERNAL || this == EXTERNAL_INTERNAL;
    }

    public boolean isExternal(){
        return this == EXTERNAL || this == EXTERNAL_INTERNAL;
    }

}
