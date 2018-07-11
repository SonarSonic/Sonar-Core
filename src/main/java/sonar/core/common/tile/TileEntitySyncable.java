package sonar.core.common.tile;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenableList;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.core.network.PacketTileSync;

import java.util.List;

public class TileEntitySyncable extends TileEntitySaveable {

    public ListenableList<PlayerListener> listeners = new ListenableList<>(new Listener(this), 1);

    public boolean hasSyncListeners(){
        return listeners.hasListeners(0);
    }

    public List<PlayerListener> getSyncListeners(){
        return listeners.getListeners(0);
    }

    @Override
    public void onValuesChanged(){
        super.onValuesChanged();
        if(!world.isRemote && hasSyncListeners()) {
            performSync();
        }
    }

    public void performSync(){
        if(!world.isRemote) {
            NBTTagCompound tag = writeData(new NBTTagCompound(), NBTHelper.SyncType.DEFAULT_SYNC);
            if(!tag.hasNoTags()){
                getSyncListeners().forEach(l -> SonarCore.network.sendTo(new PacketTileSync(this.getPos(), tag, NBTHelper.SyncType.DEFAULT_SYNC), l.player));
            }
        }
    }

    public void sendFirstPacket(EntityPlayerMP playerMP){
        if(!world.isRemote) {
            NBTTagCompound tag = writeData(new NBTTagCompound(), NBTHelper.SyncType.SAVE);
            SonarCore.network.sendTo(new PacketTileSync(this.getPos(), tag, NBTHelper.SyncType.SAVE), playerMP);
        }
    }

    public void sendLastPacket(EntityPlayerMP playerMP){}

    @Override
    public void invalidate(){
        super.invalidate();
        listeners.invalidateList();
    }

    public static class Listener implements ISonarListenable<PlayerListener> {

        public final TileEntitySyncable tile;

        public Listener(TileEntitySyncable tile){
            this.tile = tile;
        }

        public void onListenerAdded(ListenerTally<PlayerListener> tally){
            tile.sendFirstPacket(tally.listener.player);
        }

        public void onListenerRemoved(ListenerTally<PlayerListener> tally){
            tile.sendLastPacket(tally.listener.player);
        }

        @Override
        public ListenableList<PlayerListener> getListenerList() {
            return tile.listeners;
        }

        @Override
        public boolean isValid() {
            return !tile.isInvalid();
        }
    }

}