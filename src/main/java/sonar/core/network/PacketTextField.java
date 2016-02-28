package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.ITextField;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketTextField extends PacketCoords<PacketTextField> {

	public int id;
	public String string;

	public PacketTextField() {
	}

	public PacketTextField(String string, int x, int y, int z, int id) {
		super(x, y, z);
		this.string = string;
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.string = ByteBufUtils.readUTF8String(buf);
		this.id = buf.readInt();

	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		ByteBufUtils.writeUTF8String(buf, string);
		buf.writeInt(id);
	}

	public static class Handler extends PacketTileEntityHandler<PacketTextField> {
		@Override
		public IMessage processMessage(PacketTextField message, TileEntity tile) {
			if (!tile.getWorldObj().isRemote) {
				Object te = FMPHelper.checkObject(tile);
				if (te == null) {
					return null;
				}
				if (te instanceof ITextField) {
					ITextField field = (ITextField) te;
					field.textTyped(message.string, message.id);
				}
			}
			return null;
		}
	}
}
