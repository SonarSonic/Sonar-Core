package sonar.core.sync;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class SyncHandlerList<V> implements ISyncHandler<List<V>> {

    public ISyncHandler<V> subHandler;

    public SyncHandlerList(ISyncHandler<V> subHandler){
        Preconditions.checkState(subHandler != null);
        this.subHandler = subHandler;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound tag, String key, List<V> value) {
        NBTTagList tagList = new NBTTagList();
        for(V v : value){
            NBTTagCompound subTag = new NBTTagCompound();
            subHandler.save(subTag, "v", v);
            tagList.appendTag(subTag);
        }
        if(!tagList.hasNoTags()){
            tag.setTag(key, tagList);
        }
        return tag;
    }

    @Override
    public List<V> load(NBTTagCompound tag, String key) {
        List<V> list = new ArrayList<>();
        NBTTagList tagList = tag.getTagList(key, Constants.NBT.TAG_COMPOUND);
        for(int i = 0 ; i < tagList.tagCount(); i++){
            NBTTagCompound subTag = tagList.getCompoundTagAt(i);
            list.add(subHandler.load(subTag, "v"));
        }
        return list;
    }

    @Override
    public ByteBuf save(ByteBuf buf, List<V> value) {
        buf.writeInt(value.size());
        for(V v : value){
            if(v != null) {
                buf.writeBoolean(true);
                subHandler.save(buf, v);
            }else {
                buf.writeBoolean(false);
            }
        }
        return buf;
    }

    @Override
    public List<V> load(ByteBuf buf) {
        List<V> list = new ArrayList<>();
        int listSize = buf.readInt();
        for(int i = 0; i < listSize ; i++){
            if(buf.readBoolean()) {
                list.add(subHandler.load(buf));
            }else{
                list.add(null);
            }
        }
        return list;
    }
}
