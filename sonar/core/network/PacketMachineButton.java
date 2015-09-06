package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.utils.IMachineButtons;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketMachineButton implements IMessage {

	public int xCoord, yCoord, zCoord, id, value;

	public PacketMachineButton() {
	}

	public PacketMachineButton(int id, int value, int xCoord, int yCoord, int zCoord) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.id = id;
		this.value=value;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.id = buf.readInt();
		this.value = buf.readInt();


	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		buf.writeInt(id);
		buf.writeInt(value);
	}

	public static class Handler implements IMessageHandler<PacketMachineButton, IMessage> {

		@Override
		public IMessage onMessage(PacketMachineButton message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (te == null) {
				return null;
			}
			if (te instanceof IMachineButtons) {
				((IMachineButtons) te).buttonPress(message.id, message.value);
			}

			return null;
		}

	}
}
