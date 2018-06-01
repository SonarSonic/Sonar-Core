package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;

public class SyncEnergyType extends SyncPart {

    public EnergyType type = EnergyType.FE;

    public SyncEnergyType(int id) {
        super(id);
    }

    public SyncEnergyType(int id, EnergyType def) {
        super(id);
        type = def;
    }

    public EnergyType getEnergyType() {
        return type;
    }

    public void setEnergyType(EnergyType type){
    	this.type = type;
    	this.markChanged();
    }
    
    public void incrementType() {
        SonarHelper.incrementEnum(this.type, EnergyType.values());
        this.markChanged();
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        buf.writeInt(type.ordinal());
    }

    @Override
    public void readFromBuf(ByteBuf buf) {
        type = EnergyType.values()[buf.readInt()];
    }

    @Override
    public void readData(NBTTagCompound nbt, SyncType type) {
        this.type = EnergyType.readFromNBT(nbt, "energyType");
    }

    @Override
    public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
        EnergyType.writeToNBT(this.type, nbt, "energyType");
        return nbt;
    }

}