package sonar.core.sync;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;

import java.util.*;

public class SyncRegistry {

    private static List<ISyncValueFactory> value_factories = new ArrayList<>();
    private static List<ISyncHandlerFactory> handler_factories = new ArrayList<>();

    private static Map<Class, ISyncHandler> handlers = new HashMap<>();

    static{
        handler_factories.add(clazz -> clazz.isEnum() ? new SyncHandlerGeneral.SyncHandlerEnum((Enum[])clazz.getEnumConstants()) : null);
        handler_factories.add(clazz -> INBTSyncable.class.isAssignableFrom(clazz) ? new SyncHandlerNBTSyncable(clazz) : null);

        handlers.put(Boolean.class, new SyncHandlerPrimitive.SyncHandlerBoolean());
        handlers.put(Byte.class, new SyncHandlerPrimitive.SyncHandlerByte());
        handlers.put(Short.class, new SyncHandlerPrimitive.SyncHandlerShort());
        handlers.put(Integer.class, new SyncHandlerPrimitive.SyncHandlerInteger());
        handlers.put(Long.class, new SyncHandlerPrimitive.SyncHandlerLong());
        handlers.put(Float.class, new SyncHandlerPrimitive.SyncHandlerFloat());
        handlers.put(Double.class, new SyncHandlerPrimitive.SyncHandlerDouble());
        handlers.put(byte[].class, new SyncHandlerPrimitive.SyncHandlerByteArray());
        handlers.put(int[].class, new SyncHandlerPrimitive.SyncHandlerIntArray());

        handlers.put(String.class, new SyncHandlerGeneral.SyncHandlerString());
        handlers.put(NBTTagCompound.class, new SyncHandlerGeneral.SyncHandlerNBTTagCompound());
        handlers.put(ItemStack.class, new SyncHandlerGeneral.SyncHandlerItemStack());
        handlers.put(UUID.class, new SyncHandlerGeneral.SyncHandlerUUID());

        value_factories.add((clazz, watcher, key) -> {
            if(UUID.class.isAssignableFrom(clazz)){
                return new SyncValue(clazz, watcher, key) {
                    public boolean canLoadFrom(NBTTagCompound tag) {
                        ////UUIDS are stored via getTagName + 'Most' / 'Least'
                        return tag.hasUniqueId(getTagName());
                    }
                };
            }
            return null;
        });
    }

    public static <T> ISonarValue<T> createSonarValue(Class<T> type, IValueWatcher watcher, T setting){
        return new SonarValue<>(type, watcher, setting);
    }

    public static <T> ISonarValue<List<T>> createSonarValueList(Class<T> type, IValueWatcher watcher, List<T> setting){
        return new SonarValueList<>(type, watcher, setting);
    }

    public static <T> ISonarValue<List<T>> createSonarControlledList(Class<T> type, IValueWatcher watcher, List<T> setting){
        return new SonarControlledList<>(type, watcher, setting);
    }

    public static <T> ISyncValue<List<T>> createListValue(Class<T> type, IValueWatcher watcher, String key, List<T> setting){
        return new SyncedValueList<>(type, watcher, key, setting);
    }

    public static <T> ISyncValue<T> createValue(Class<T> type, IValueWatcher watcher, String key, T setting){
        ISyncValue<T> value = createValue(type, watcher, key);
        value.setValueInternal(setting);
        return value;
    }

    public static <T> ISyncValue<T> createValue(Class<T> type, IValueWatcher watcher, String key){
        for(ISyncValueFactory factory : value_factories){
            ISyncValue<T> value = factory.createValue(type, watcher, key);
            if(value != null){
                return value;
            }
        }
        ISyncHandler handler = getHandler(type);
        if(handler != null){
            return new SyncValue<>(type, watcher, key);
        }
        throw new NullPointerException("INVALID VALUE TYPE: " + type);
    }

    public static ISyncHandler getHandler(Class clazz){
        for(ISyncHandlerFactory factory : handler_factories){
            ISyncHandler handler = factory.createHandler(clazz);
            if(handler != null){
                return handler;
            }
        }
        Optional<Map.Entry<Class, ISyncHandler>> handler = handlers.entrySet().stream().filter((C) -> C.getKey().isAssignableFrom(clazz)).findFirst();
        return handler.map(Map.Entry::getValue).orElse(null);
    }

}
