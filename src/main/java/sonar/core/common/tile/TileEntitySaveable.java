package sonar.core.common.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.sync.ISonarValue;
import sonar.core.sync.IValueWatcher;
import sonar.core.sync.ValueWatcher;

import javax.annotation.Nonnull;

public class TileEntitySaveable extends TileEntity implements ITickable, INBTSyncable {

    public final ValueWatcher value_watcher = new ValueWatcher(new IValueWatcher() {
        @Override
        public void onSyncValueChanged(ISonarValue value) {
            onInternalValueChanged(value);
        }
    });

    public void onValuesChanged(){}

    /**world may be null*/
    public void onInternalValueChanged(ISonarValue value){}

    @Override
    public void update(){
        if(value_watcher.isDirty()){
            onValuesChanged();
            value_watcher.forEachSyncable(s -> s.setDirty(false));
            value_watcher.setDirty(false);
            if(!world.isRemote)
                markDirty();
        }
    }

    @Override
    public final void readFromNBT(NBTTagCompound compound){
        super.readFromNBT(compound);
        readData(compound, NBTHelper.SyncType.SAVE);
    }

    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound){
        super.writeToNBT(compound);
        writeData(compound, NBTHelper.SyncType.SAVE);
        return compound;
    }

    @Override
    public void readData(NBTTagCompound nbt, NBTHelper.SyncType type) {
        value_watcher.forEachSyncable(v -> { if(v.canLoadFrom(nbt)) v.load(nbt); });
    }

    @Override
    public NBTTagCompound writeData(NBTTagCompound nbt, NBTHelper.SyncType type) {
        value_watcher.forEachSyncable(v -> v.save(nbt));
        return nbt;
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = writeToNBT(new NBTTagCompound());
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    public NBTHelper.SyncType getUpdateTagType(){
        return NBTHelper.SyncType.SYNC_OVERRIDE;
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeData(super.getUpdateTag(), getUpdateTagType());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readData(tag, getUpdateTagType());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
