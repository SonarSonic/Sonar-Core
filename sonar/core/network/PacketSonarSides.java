package sonar.core.network;

import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.ITextField;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import cofh.api.tileentity.IReconfigurableSides;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSonarSides extends PacketCoords<PacketSonarSides> {

	public int side, value;

	public PacketSonarSides() {}

	public PacketSonarSides(int x, int y, int z, int side, int value) {
		super(x, y, z);
		this.side = side;
		this.value = value;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		side = buf.readInt();
		value = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(side);
		buf.writeInt(value);
	}
	public static class Handler extends PacketTileEntityHandler<PacketSonarSides> {

		@Override
		public IMessage processMessage(PacketSonarSides message, TileEntity tile) {

			if (tile.getWorldObj().isRemote && tile instanceof IReconfigurableSides) {
				IReconfigurableSides sides = (IReconfigurableSides) tile;
				sides.setSide(message.side, message.value);
			}
			return null;
		}
	}

}
