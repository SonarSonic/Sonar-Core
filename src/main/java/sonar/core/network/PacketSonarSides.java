package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.utils.ISonarSides;
import sonar.core.utils.MachineSide;

public class PacketSonarSides extends PacketCoords<PacketSonarSides> {

	public EnumFacing side;
	public MachineSide config;
	
	public PacketSonarSides() {}

	public PacketSonarSides(BlockPos pos, EnumFacing side, MachineSide config) {
		super(pos);
		this.side = side;
		this.config = config;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		side = side.VALUES[buf.readInt()];
		config = config.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(side.getIndex());
		buf.writeInt(config.ordinal());
	}
	public static class Handler extends PacketTileEntityHandler<PacketSonarSides> {

		@Override
		public IMessage processMessage(PacketSonarSides message, TileEntity tile) {
			if (tile.getWorld().isRemote && tile instanceof ISonarSides) {
				ISonarSides sides = (ISonarSides) tile;
				sides.setSide(message.side, message.config);
				tile.receiveClientEvent(1, 1);
			}
			return null;
		}
	}

}
