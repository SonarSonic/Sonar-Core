package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;

public class PacketSonarSides extends PacketCoords<PacketSonarSides> {

	public EnumFacing side;
	public MachineSideConfig config;
	
	public PacketSonarSides() {}

	public PacketSonarSides(BlockPos pos, EnumFacing side, MachineSideConfig config) {
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
			if (tile.getWorld().isRemote && tile instanceof IMachineSides) {
				IMachineSides sides = (IMachineSides) tile;
				sides.getSideConfigs().setSide(message.side, message.config);
				if(tile instanceof TileEntitySonar){
					((TileEntitySonar) tile).markBlockForUpdate();
				}
			}
			return null;
		}
	}

}
