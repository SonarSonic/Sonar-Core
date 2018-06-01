package sonar.core.handlers.energy;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.ISonarEnergyItem;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.SonarLoader;

import javax.annotation.Nonnull;

@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla")
})
public class SonarEnergyItemWrapper implements IEnergyStorage, ICapabilityProvider, ITeslaConsumer, ITeslaProducer, ITeslaHolder {

    public ISonarEnergyItem energyItem;
    public ItemStack stack;

    public SonarEnergyItemWrapper(ISonarEnergyItem energyItem, ItemStack stack){
        this.energyItem = energyItem;
        this.stack = stack;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) energyItem.addEnergy(stack, maxReceive, ActionType.getTypeForAction(simulate));
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) energyItem.removeEnergy(stack, maxExtract, ActionType.getTypeForAction(simulate));
    }

    @Override
    public int getEnergyStored() {
        return (int) energyItem.getEnergyLevel(stack);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) energyItem.getFullCapacity(stack);
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    ///// * TESLA *//////
    @Override
    @Optional.Method(modid = "tesla")
    public long getStoredPower() {
        return getEnergyStored();
    }

    @Override
    @Optional.Method(modid = "tesla")
    public long getCapacity() {
        return getMaxEnergyStored();
    }

    @Override
    @Optional.Method(modid = "tesla")
    public long takePower(long power, boolean simulated) {
        return energyItem.removeEnergy(stack, Math.min(Integer.MAX_VALUE, power), ActionType.getTypeForAction(simulated));
    }

    @Override
    @Optional.Method(modid = "tesla")
    public long givePower(long power, boolean simulated) {
        return energyItem.addEnergy(stack, power, ActionType.getTypeForAction(simulated));
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        if(capability == CapabilityEnergy.ENERGY){
            return true;
        }
        if (SonarLoader.teslaLoaded) {
            return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == TeslaCapabilities.CAPABILITY_HOLDER;
        }
        return false;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityEnergy.ENERGY){
            return (T)this;
        }
        if (SonarLoader.teslaLoaded) {
            if (capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == TeslaCapabilities.CAPABILITY_HOLDER)
                return (T) this;
        }
        return null;
    }

}
