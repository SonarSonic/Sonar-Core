package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.network.utils.IByteBufTile;

public class PacketByteBufMultipart extends PacketMultipart {

	public int packetID;
	public IByteBufTile tile;
	public ByteBuf buf;

	public PacketByteBufMultipart() {}

	public PacketByteBufMultipart(int slotID, IByteBufTile tile, BlockPos pos, int packetID) {
		super(slotID, pos);
		this.tile = tile;
		this.packetID = packetID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.buf = buf.retain();
		this.packetID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(packetID);
		tile.writePacket(buf, packetID);
	}

	public static class Handler extends PacketMultipartHandler<PacketByteBufMultipart> {

		@Override
		public IMessage processMessage(PacketByteBufMultipart message, EntityPlayer player, World world, IMultipartTile part, MessageContext ctx) {
			SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
				if (part != null && part instanceof IByteBufTile) {
					((IByteBufTile) part).readPacket(message.buf, message.packetID);
				}
				message.buf.release();
			});

			return null;
		}
	}
}