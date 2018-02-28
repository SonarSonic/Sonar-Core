package sonar.core.network.sync2;

import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import sonar.core.network.sync.ISyncableListener;

public class EnumHandler implements ISyncHandler<Enum<?>> {

    @Override
    public boolean canHandle(Class<?> clazz) {
        return Enum.class.isAssignableFrom(clazz);
    }

    @Override
    public void writeToNBT(ISyncableListener syncable, Field field, Enum<?> obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException {
        nbt.setInteger(tagName, obj.ordinal());
    }

    @Override
    public void readFromNBT(ISyncableListener syncable, Field field, Enum<?> obj, String tagName, NBTTagCompound nbt) throws IllegalArgumentException, IllegalAccessException {
        Enum<?> toSet = obj;
        if (nbt.hasKey(tagName) && (obj != null || field != null)) {
            Enum<?>[] values = (Enum<?>[]) (obj != null ? obj.getClass().getEnumConstants() : field.getType().getEnumConstants());
            Enum<?> res = values[MathHelper.clamp(nbt.getInteger(tagName), 0, values.length - 1)];
            toSet = res;
        }
        field.set(obj, toSet);
    }

}