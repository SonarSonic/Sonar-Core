package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;

public class PacketFlexibleOpenGui extends PacketCoords {
	public boolean change;
	public NBTTagCompound tag;
	public int windowID;

	public PacketFlexibleOpenGui() {}

	public PacketFlexibleOpenGui(boolean change, BlockPos pos, int windowID, NBTTagCompound tag) {
		super(pos);
		this.change = change;
		this.tag = tag;
		this.windowID = windowID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		change = buf.readBoolean();
		tag = ByteBufUtils.readTag(buf);
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		windowID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeBoolean(change);
		ByteBufUtils.writeTag(buf, tag);
		buf.writeInt(windowID);
	}

	public static class Handler implements IMessageHandler<PacketFlexibleOpenGui, IMessage> {
		@Override
		public IMessage onMessage(PacketFlexibleOpenGui message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
				int id = message.tag.getInteger("id");
				SonarCore.instance.guiHandler.openGuiClient(player, message.pos, message.tag, id, message.windowID, message.change);
			});
			return null;
		}
	}
}
