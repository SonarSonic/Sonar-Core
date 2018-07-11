package sonar.core.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.UUID;

public class SyncHandlerGeneral {

    public static class SyncHandlerString implements ISyncHandler<String> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, String value) {
            tag.setString(key, value);
            return tag;
        }

        @Override
        public String load(NBTTagCompound tag, String key) {
            return tag.getString(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, String value) {
            ByteBufUtils.writeUTF8String(buf, value);
            return buf;
        }

        @Override
        public String load(ByteBuf buf) {
            return ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class SyncHandlerNBTTagCompound implements ISyncHandler<NBTTagCompound> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, NBTTagCompound value) {
            tag.setTag(key, value);
            return tag;
        }

        @Override
        public NBTTagCompound load(NBTTagCompound tag, String key) {
            return tag.getCompoundTag(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, NBTTagCompound value) {
            ByteBufUtils.writeTag(buf, value);
            return buf;
        }

        @Override
        public NBTTagCompound load(ByteBuf buf) {
            return ByteBufUtils.readTag(buf);
        }
    }

    public static class SyncHandlerItemStack implements ISyncHandler<ItemStack> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, ItemStack value) {
            return value.writeToNBT(tag);
        }

        @Override
        public ItemStack load(NBTTagCompound tag, String key) {
            return new ItemStack(tag);
        }

        @Override
        public ByteBuf save(ByteBuf buf, ItemStack value) {
            ByteBufUtils.writeItemStack(buf, value);
            return buf;
        }

        @Override
        public ItemStack load(ByteBuf buf) {
            return ByteBufUtils.readItemStack(buf);
        }
    }

    public static class SyncHandlerUUID implements ISyncHandler<UUID> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, UUID value) {
            tag.setUniqueId(key, value);
            return tag;
        }

        @Override
        public UUID load(NBTTagCompound tag, String key) {
            return tag.getUniqueId(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, UUID value) {
            buf.writeLong(value.getMostSignificantBits());
            buf.writeLong(value.getLeastSignificantBits());
            return buf;
        }

        @Override
        public UUID load(ByteBuf buf) {
            return new UUID(buf.readLong(), buf.readLong());
        }
    }

    public static class SyncHandlerEnum<E extends Enum> implements ISyncHandler<E> {

        public E[] constants;

        public SyncHandlerEnum(E[] constants){
            this.constants = constants;
        }

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, E value) {
            tag.setInteger(key, value.ordinal());
            return tag;
        }

        @Override
        public E load(NBTTagCompound tag, String key) {
            return constants[tag.getInteger(key)];
        }

        @Override
        public ByteBuf save(ByteBuf buf, E value) {
            return buf.writeInt(value.ordinal());
        }

        @Override
        public E load(ByteBuf buf) {
            return constants[buf.readInt()];
        }
    }
}
