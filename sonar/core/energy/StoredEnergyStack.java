package sonar.core.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * should only be in RF, inaccuracy due to conversion is a price we must pay at
 * the moment
 */
public class StoredEnergyStack {

	public long stored, capacity, input, output, usage;
	public boolean hasStorage, hasInput, hasOutput, hasUsage;

	public StoredEnergyStack() {}
	
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
		StoredEnergyStack stored = new StoredEnergyStack();
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
		StoredEnergyStack stored = new StoredEnergyStack();
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
			if (this.stored == target.stored && this.capacity == target.capacity && this.input == target.input && this.output == target.output && this.usage == target.usage) {
				return true;
			}
		}
		return false;
	}
}
