package sonar.core.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;

public class CustomColour implements INBTSyncable {

	public int red, green, blue;
	private Integer rgb = null;

	public CustomColour(){}
	
	public CustomColour(int r, int g, int b) {
		red = r;
		green = g;
		blue = b;
	}

	public int getRGB() {
		if(rgb==null){
			rgb = FontHelper.getIntFromColor(red, green, blue);
		}
		return rgb;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		red = nbt.getInteger("red");
		green = nbt.getInteger("green");
		blue = nbt.getInteger("blue");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setInteger("red", red);
		nbt.setInteger("green", green);
		nbt.setInteger("blue", blue);
		return nbt;
	}

	public static void writeToBuf(CustomColour colour, ByteBuf buf) {
		buf.writeInt(colour.red);
		buf.writeInt(colour.green);
		buf.writeInt(colour.blue);
	}

	public static CustomColour readFromBuf(ByteBuf buf) {
		return new CustomColour(buf.readInt(), buf.readInt(), buf.readInt());
	}
}
