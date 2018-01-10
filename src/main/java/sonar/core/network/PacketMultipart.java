package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketMultipart extends PacketCoords {

	public int slotID;

	public PacketMultipart() {
		super();
	}

	public PacketMultipart(int slotID, BlockPos pos) {
		super(pos);
		this.slotID = slotID;
	}

	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		slotID = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(slotID);
	}

}