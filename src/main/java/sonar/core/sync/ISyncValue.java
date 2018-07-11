package sonar.core.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public interface ISyncValue<T> extends ISonarValue<T> {

    /**key to use when saving the value to nbt*/
    String getTagName();

    ISyncHandler<T> getSyncHandler();

    default boolean canLoadFrom(NBTTagCompound tag){
        return tag.hasKey(getTagName());
    }

    default NBTTagCompound save(NBTTagCompound tag){
        return getSyncHandler().save(tag, getTagName(), getValue());
    }

    default T load(NBTTagCompound tag){
        setValueInternal(getSyncHandler().load(tag, getTagName()));
        return getValue();
    }

    default ByteBuf save(ByteBuf buf){
        return getSyncHandler().save(buf, getValue());
    }

    default T load(ByteBuf buf){
        setValueInternal(getSyncHandler().load(buf));
        return getValue();
    }


}
