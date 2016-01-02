package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.ITextField;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTextField implements IMessage {

	public int xCoord, yCoord, zCoord;
	public int id;
	public String string;

	public PacketTextField() {
	}

	public PacketTextField(String string, int xCoord, int yCoord, int zCoord, int id) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.string = string;
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.string = ByteBufUtils.readUTF8String(buf);
		this.id = buf.readInt();

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		ByteBufUtils.writeUTF8String(buf, string);
		buf.writeInt(id);
	}

	public static class Handler implements IMessageHandler<PacketTextField, IMessage> {

		@Override
		public IMessage onMessage(PacketTextField message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			Object te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			te = FMPHelper.checkObject(te);
			if (te == null) {
				return null;
			}

			if (te instanceof ITextField) {
				ITextField field = (ITextField) te;
				field.textTyped(message.string, message.id);

			}
			return null;
		}

	}
}
