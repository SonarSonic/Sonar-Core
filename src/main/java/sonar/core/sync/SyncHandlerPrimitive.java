package sonar.core.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class SyncHandlerPrimitive {

    public static class SyncHandlerBoolean implements ISyncHandler<Boolean> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, Boolean value) {
            tag.setBoolean(key, value);
            return tag;
        }

        @Override
        public Boolean load(NBTTagCompound tag, String key) {
            return tag.getBoolean(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, Boolean value) {
            return buf.writeBoolean(value);
        }

        @Override
        public Boolean load(ByteBuf buf) {
            return buf.readBoolean();
        }
    }

    public static class SyncHandlerByte implements ISyncHandler<Byte> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, Byte value) {
            tag.setByte(key, value);
            return tag;
        }

        @Override
        public Byte load(NBTTagCompound tag, String key) {
            return tag.getByte(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, Byte value) {
            return buf.writeByte(value);
        }

        @Override
        public Byte load(ByteBuf buf) {
            return buf.readByte();
        }
    }

    public static class SyncHandlerShort implements ISyncHandler<Short> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, Short value) {
            tag.setShort(key, value);
            return tag;
        }

        @Override
        public Short load(NBTTagCompound tag, String key) {
            return tag.getShort(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, Short value) {
            return buf.writeShort(value);
        }

        @Override
        public Short load(ByteBuf buf) {
            return buf.readShort();
        }
    }

    public static class SyncHandlerInteger implements ISyncHandler<Integer> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, Integer value) {
            tag.setInteger(key, value);
            return tag;
        }

        @Override
        public Integer load(NBTTagCompound tag, String key) {
            return tag.getInteger(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, Integer value) {
            return buf.writeInt(value);
        }

        @Override
        public Integer load(ByteBuf buf) {
            return buf.readInt();
        }
    }

    public static class SyncHandlerLong implements ISyncHandler<Long> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, Long value) {
            tag.setLong(key, value);
            return tag;
        }

        @Override
        public Long load(NBTTagCompound tag, String key) {
            return tag.getLong(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, Long value) {
            return buf.writeLong(value);
        }

        @Override
        public Long load(ByteBuf buf) {
            return buf.readLong();
        }
    }

    public static class SyncHandlerFloat implements ISyncHandler<Float> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, Float value) {
            tag.setFloat(key, value);
            return tag;
        }

        @Override
        public Float load(NBTTagCompound tag, String key) {
            return tag.getFloat(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, Float value) {
            return buf.writeFloat(value);
        }

        @Override
        public Float load(ByteBuf buf) {
            return buf.readFloat();
        }
    }

    public static class SyncHandlerDouble implements ISyncHandler<Double> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, Double value) {
            tag.setDouble(key, value);
            return tag;
        }

        @Override
        public Double load(NBTTagCompound tag, String key) {
            return tag.getDouble(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, Double value) {
            return buf.writeDouble(value);
        }

        @Override
        public Double load(ByteBuf buf) {
            return buf.readDouble();
        }
    }

    public static class SyncHandlerByteArray implements ISyncHandler<byte[]> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, byte[] value) {
            tag.setByteArray(key, value);
            return tag;
        }

        @Override
        public byte[] load(NBTTagCompound tag, String key) {
            return tag.getByteArray(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, byte[] value) {
            buf.writeInt(value.length);
            for(int i = 0; i < value.length; i++){
                buf.writeByte(value[i]);
            }
            return buf;
        }

        @Override
        public byte[] load(ByteBuf buf) {
            byte[] value = new byte[buf.readInt()];
            for(int i = 0; i < value.length; i++){
                value[i] = buf.readByte();
            }
            return value;
        }
    }

    public static class SyncHandlerIntArray implements ISyncHandler<int[]> {

        @Override
        public NBTTagCompound save(NBTTagCompound tag, String key, int[] value) {
            tag.setIntArray(key, value);
            return tag;
        }

        @Override
        public int[] load(NBTTagCompound tag, String key) {
            return tag.getIntArray(key);
        }

        @Override
        public ByteBuf save(ByteBuf buf, int[] value) {
            buf.writeInt(value.length);
            for(int i = 0; i < value.length; i++){
                buf.writeInt(value[i]);
            }
            return buf;
        }

        @Override
        public int[] load(ByteBuf buf) {
            int[] value = new int[buf.readInt()];
            for(int i = 0; i < value.length; i++){
                value[i] = buf.readInt();
            }
            return value;
        }
    }
}
