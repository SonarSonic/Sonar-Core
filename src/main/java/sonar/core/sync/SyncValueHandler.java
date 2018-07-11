package sonar.core.sync;

public class SyncValueHandler {

    public static void invertBoolean(ISonarValue<Boolean> b){
        b.setValue(!b.getValue());
    }

    public static <E extends Enum> void incrementEnum(ISonarValue<E> b){
        E[] values = null;
        if(b instanceof ISyncValue){
            ISyncValue<E> value = (ISyncValue<E>) b;
            if(value.getSyncHandler() instanceof SyncHandlerGeneral.SyncHandlerEnum){
                SyncHandlerGeneral.SyncHandlerEnum handlerEnum = (SyncHandlerGeneral.SyncHandlerEnum) value.getSyncHandler();
                values = (E[]) handlerEnum.constants;
            }
        }
        if(values == null){
           values = (E[]) b.getValue().getClass().getEnumConstants();
        }

        int ordinal = b.getValue().ordinal() + 1;
        b.setValue(values[ordinal < values.length ? ordinal : 0]);
    }

}
