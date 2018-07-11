package sonar.core.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;

import java.util.function.Supplier;

public class SyncHandlerNBTSyncable implements ISyncHandler<INBTSyncable> {

    public final Supplier<INBTSyncable> supplier;

    public SyncHandlerNBTSyncable(Supplier<INBTSyncable> supplier){
        this.supplier = supplier;
    }

    public SyncHandlerNBTSyncable(Class clazz){
        this.supplier = () -> {
            try {
                return (INBTSyncable) clazz.newInstance();
            } catch (Throwable e) {
                System.out.println("FAILED TO INIT NBT SYNCABLE: " + clazz);
                e.printStackTrace();
            }
            return null;
        };
    }

    @Override
    public NBTTagCompound save(NBTTagCompound tag, String key, INBTSyncable value) {
        NBTTagCompound subTag = new NBTTagCompound();
        value.writeData(subTag, NBTHelper.SyncType.SAVE);
        if(!subTag.hasNoTags()){
            tag.setTag(key, subTag);
        }
        return tag;
    }

    @Override
    public INBTSyncable load(NBTTagCompound tag, String key) {
        INBTSyncable value = supplier.get();
        if(tag.hasKey(key)) {
            NBTTagCompound subTag = tag.getCompoundTag(key);
            value.readData(subTag, NBTHelper.SyncType.SAVE);
        }
        return value;
    }

    @Override
    public ByteBuf save(ByteBuf buf, INBTSyncable value) {
        ByteBufUtils.writeTag(buf, save(new NBTTagCompound(), "buf", value));
        return buf;
    }

    @Override
    public INBTSyncable load(ByteBuf buf) {
        return load(ByteBufUtils.readTag(buf), "buf");
    }

}
