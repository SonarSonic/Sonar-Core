package sonar.core.api.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.ISonarStack;
import sonar.core.api.SonarAPI;
import sonar.core.handlers.energy.IEnergyTransferProxy;
import sonar.core.helpers.NBTHelper.SyncType;

/**
 * should only be in RF, inaccuracy due to conversion is a price we must pay at the moment
 */
public class StoredEnergyStack implements ISonarStack<StoredEnergyStack>{

	public long stored, capacity, input, output, usage;
	public boolean hasStorage, hasInput, hasOutput, hasUsage;
	public EnergyType energyType;

    public StoredEnergyStack() {
    }

	public StoredEnergyStack(EnergyType type) {
		this.energyType = type;
	}

	public StoredEnergyStack(EnergyType type, long stored) {
		this.energyType = type;
		this.stored = stored;
	}

	public void setStorageValues(long stored, long capacity) {
		if (!hasStorage) {
			this.stored = stored;
			this.capacity = capacity;
			this.hasStorage = true;
		}
	}

	public void increaseStorageValues(long stored, long capacity) {
		this.stored += stored;
		this.capacity += capacity;
	}

	public void setMaxInput(long input) {
		if (!hasInput) {
			this.input = input;
			this.hasInput = true;
		}
	}

	public void increaseMaxInput(long input) {
		this.input += input;
	}

	public void setMaxOutput(long output) {
		if (!hasOutput) {
			this.output = output;
			this.hasOutput = true;
		}
	}

	public void increaseMaxOutput(long output) {
		this.output += output;
	}

	public void setUsage(long usage) {
		if (!hasUsage) {
			this.usage = usage;
			this.hasUsage = true;
		}
	}

	public void increaseUsage(long usage) {
		this.usage += usage;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		energyType = EnergyType.readFromNBT(nbt, "energytype");
		hasStorage = nbt.getBoolean("hS");
		hasInput = nbt.getBoolean("hI");
		hasOutput = nbt.getBoolean("hO");
		hasUsage = nbt.getBoolean("hU");

		if (hasStorage) {
			stored = nbt.getLong("s");
			capacity = nbt.getLong("c");
		}
		if (hasInput) {
			input = nbt.getLong("i");
		}
		if (hasOutput) {
			output = nbt.getLong("o");
		}
		if (hasUsage) {
			usage = nbt.getLong("u");
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		EnergyType.writeToNBT(energyType, nbt, "energytype");
		nbt.setBoolean("hS", hasStorage);
		nbt.setBoolean("hI", hasInput);
		nbt.setBoolean("hO", hasOutput);
		nbt.setBoolean("hU", hasUsage);

		if (hasStorage) {
			nbt.setLong("s", stored);
			nbt.setLong("c", capacity);
		}
		if (hasInput) {
			nbt.setLong("i", input);
		}
		if (hasOutput) {
			nbt.setLong("o", output);
		}
		if (hasUsage) {
			nbt.setLong("u", usage);
		}
		return nbt;
	}

	public static StoredEnergyStack readFromBuf(ByteBuf buf) {
    	StoredEnergyStack energyStack = new StoredEnergyStack();
		energyStack.readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
		return energyStack;
	}

	public static void writeToBuf(ByteBuf buf, StoredEnergyStack energyStack) {
		ByteBufUtils.writeTag(buf, energyStack.writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	public boolean equals(Object obj) {
		if (obj instanceof StoredEnergyStack) {
			StoredEnergyStack target = (StoredEnergyStack) obj;
            return this.stored == target.stored && this.capacity == target.capacity && this.input == target.input && this.output == target.output && this.usage == target.usage && this.energyType.getName().equals(target.energyType.getName());
		}
		return false;
	}

	@Override
	public StorageTypes getStorageType() {
		return StorageTypes.ENERGY;
	}

    @Override
	public StoredEnergyStack copy() {
		StoredEnergyStack stack = new StoredEnergyStack(energyType);
		stack.stored = stored;
		stack.capacity = capacity;
		stack.input = input;
		stack.output = output;
		stack.usage = usage;
		stack.hasStorage = hasStorage;
		stack.hasInput = hasInput;
		stack.hasOutput = hasOutput;
		stack.hasUsage = hasUsage;
		return stack;
	}

	@Override
	public void add(StoredEnergyStack stack) {
		if (energyType.getName().equals(stack.energyType.getName())) {
			this.stored += stack.stored;
			this.capacity += stack.capacity;
			if (stack.input > this.input)
				this.input += stack.input;
			if (stack.output > this.output)
				this.output += stack.output;
			if (stack.usage > this.usage)
				this.usage += stack.usage;

			if(stack.hasOutput){
				this.hasOutput=true;
			}
			if(stack.hasStorage){
				this.hasStorage=true;
			}
			if(stack.hasUsage){
				this.hasUsage=true;
			}
			if(stack.hasInput){
				this.hasInput=true;
			}
		}
	}

	@Override
	public void remove(StoredEnergyStack stack) {
		if (energyType.getName().equals(stack.energyType.getName())) {
			this.stored -= stack.stored;
			this.capacity -= stack.capacity;
		}
	}

	@Override
	public StoredEnergyStack setStackSize(long size) {
		stored = size;
		return this;
	}

	@Override
	public long getStackSize() {
		return stored;
	}

	public StoredEnergyStack convertEnergyType(EnergyType newFormat, IEnergyTransferProxy proxy) {
		if (energyType != newFormat) {
			double oldRFRate = proxy.getRFConversion(energyType);
			double newRFRate = proxy.getRFConversion(newFormat);

			input = (long) ((long) (input / oldRFRate) * newRFRate);
			output = (long) ((long) (output / oldRFRate) * newRFRate);
			stored = (long) ((long) (stored / oldRFRate) * newRFRate);
			capacity = (long) ((long) (capacity / oldRFRate) * newRFRate);
			usage = (long) ((long) (usage / oldRFRate) * newRFRate);
			energyType = newFormat;
		}
		return this;
	}
}
