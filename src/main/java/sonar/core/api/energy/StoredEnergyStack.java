package sonar.core.api.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.ISonarStack;
import sonar.core.api.SonarAPI;

/** should only be in RF, inaccuracy due to conversion is a price we must pay at the moment */
public class StoredEnergyStack implements ISonarStack<StoredEnergyStack> {

	public long stored, capacity, input, output, usage;
	public boolean hasStorage, hasInput, hasOutput, hasUsage;
	public EnergyType energyType;

	public StoredEnergyStack(EnergyType type) {
		this.energyType = type;
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

	public static StoredEnergyStack readFromNBT(NBTTagCompound tag) {
		StoredEnergyStack stored = new StoredEnergyStack(SonarAPI.getRegistry().getEnergyType(tag.getString("energytype")));
		stored.hasStorage = tag.getBoolean("hS");
		stored.hasInput = tag.getBoolean("hI");
		stored.hasOutput = tag.getBoolean("hO");
		stored.hasUsage = tag.getBoolean("hU");
		if (stored.hasStorage) {
			stored.stored = tag.getLong("s");
			stored.capacity = tag.getLong("c");
		}
		if (stored.hasInput) {
			stored.input = tag.getLong("i");
		}
		if (stored.hasOutput) {
			stored.output = tag.getLong("o");
		}
		if (stored.hasUsage) {
			stored.usage = tag.getLong("u");
		}
		return stored;
	}

	public static void writeToNBT(NBTTagCompound tag, StoredEnergyStack storedStack) {
		tag.setString("energytype", storedStack.energyType.getStorageSuffix());
		tag.setBoolean("hS", storedStack.hasStorage);
		tag.setBoolean("hI", storedStack.hasInput);
		tag.setBoolean("hO", storedStack.hasOutput);
		tag.setBoolean("hU", storedStack.hasUsage);

		if (storedStack.hasStorage) {
			tag.setLong("s", storedStack.stored);
			tag.setLong("c", storedStack.capacity);
		}
		if (storedStack.hasInput) {
			tag.setLong("i", storedStack.input);
		}
		if (storedStack.hasOutput) {
			tag.setLong("o", storedStack.output);
		}
		if (storedStack.hasUsage) {
			tag.setLong("u", storedStack.usage);
		}
	}

	public static StoredEnergyStack readFromBuf(ByteBuf buf) {
		StoredEnergyStack stored = new StoredEnergyStack(SonarAPI.getRegistry().getEnergyType(ByteBufUtils.readUTF8String(buf)));
		stored.hasStorage = buf.readBoolean();
		stored.hasInput = buf.readBoolean();
		stored.hasOutput = buf.readBoolean();
		stored.hasUsage = buf.readBoolean();
		if (stored.hasStorage) {
			stored.stored = buf.readLong();
			stored.capacity = buf.readLong();
		}
		if (stored.hasInput) {
			stored.input = buf.readLong();
		}
		if (stored.hasOutput) {
			stored.output = buf.readLong();
		}
		if (stored.hasUsage) {
			stored.usage = buf.readLong();
		}
		return stored;
	}

	public static void writeToBuf(ByteBuf buf, StoredEnergyStack storedStack) {
		ByteBufUtils.writeUTF8String(buf, storedStack.energyType.getStorageSuffix());
		buf.writeBoolean(storedStack.hasStorage);
		buf.writeBoolean(storedStack.hasInput);
		buf.writeBoolean(storedStack.hasOutput);
		buf.writeBoolean(storedStack.hasUsage);

		if (storedStack.hasStorage) {
			buf.writeLong(storedStack.stored);
			buf.writeLong(storedStack.capacity);
		}
		if (storedStack.hasInput) {
			buf.writeLong(storedStack.input);
		}
		if (storedStack.hasOutput) {
			buf.writeLong(storedStack.output);
		}
		if (storedStack.hasUsage) {
			buf.writeLong(storedStack.usage);
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof StoredEnergyStack) {
			StoredEnergyStack target = (StoredEnergyStack) obj;
			if (this.stored == target.stored && this.capacity == target.capacity && this.input == target.input && this.output == target.output && this.usage == target.usage && this.energyType.getStorageSuffix().equals(target.energyType.getStorageSuffix())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public StorageTypes getStorageType() {
		return StorageTypes.ENERGY;
	}

	public StoredEnergyStack copy() {
		StoredEnergyStack stack = new StoredEnergyStack(energyType);
		stack.stored = stored;
		stack.capacity = stored;
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

	public StoredEnergyStack convertEnergyType(EnergyType newFormat) {
		if (energyType != newFormat) {
			input = (long) ((long) (input / energyType.toRFConversion()) * newFormat.toRFConversion());
			output = (long) ((long) (output / energyType.toRFConversion()) * newFormat.toRFConversion());
			stored = (long) ((long) (stored / energyType.toRFConversion()) * newFormat.toRFConversion());
			capacity = (long) ((long) (capacity / energyType.toRFConversion()) * newFormat.toRFConversion());
			usage = (long) ((long) (usage / energyType.toRFConversion()) * newFormat.toRFConversion());
			energyType = newFormat;
		}
		return this;
	}

	public static long convert(long val, EnergyType current, EnergyType type) {
		double inRF = (val / current.toRFConversion());
		return (long) (inRF * type.toRFConversion());
	}

}
