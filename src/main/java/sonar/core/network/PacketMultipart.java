/*package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketMultipart extends PacketCoords {

	public UUID partUUID;

	public PacketMultipart() {
		super();
	}

	public PacketMultipart(UUID partUUID, BlockPos pos) {
		super(pos);
		this.partUUID = partUUID;
	}

	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		long msb = buf.readLong();
		long lsb = buf.readLong();
		partUUID = new UUID(msb, lsb);
	}

	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeLong(partUUID.getMostSignificantBits());
		buf.writeLong(partUUID.getLeastSignificantBits());
	}

}*/