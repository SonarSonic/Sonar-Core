package sonar.core.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public interface ISyncHandler<T> {

    /**saves the value to the nbt tag*/
    NBTTagCompound save(NBTTagCompound tag, String key, T value);

    /**loads the value from the nbt tag*/
    T load(NBTTagCompound tag, String key);

    /**saves the value to the byte buffer*/
    ByteBuf save(ByteBuf buf, T value);

    /**loads the value from the byte buffer*/
    T load(ByteBuf buf);

}
