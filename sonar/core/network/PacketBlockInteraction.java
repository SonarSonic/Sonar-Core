package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.IInteractBlock;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketBlockInteraction extends PacketCoords {

	public int side;
	public float hitX, hitY, hitZ;
	public BlockInteraction interact;

	public PacketBlockInteraction() {
	}

	public PacketBlockInteraction(int x, int y, int z, int side, float hitX, float hitY, float hitZ, BlockInteraction interact) {
		super(x, y, z);
		this.side = side;
		this.hitX = hitX;
		this.hitY = hitY;
		this.hitZ = hitZ;
		this.interact = interact;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.side = buf.readInt();
		this.hitX = buf.readFloat();
		this.hitY = buf.readFloat();
		this.hitZ = buf.readFloat();
		this.interact = BlockInteraction.valueOf(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(side);
		buf.writeFloat(hitX);
		buf.writeFloat(hitY);
		buf.writeFloat(hitZ);
		ByteBufUtils.writeUTF8String(buf, interact.name());
	}

	public static class Handler extends PacketCoordsHandler<PacketBlockInteraction> {
		@Override
		public IMessage processMessage(PacketBlockInteraction message, World world, EntityPlayer player) {
			if (!world.isRemote) {
				Block target = world.getBlock(message.xCoord, message.yCoord, message.zCoord);
				if (target != null && target instanceof IInteractBlock) {
					IInteractBlock interact = (IInteractBlock) target;
					interact.operateBlock(world, message.xCoord, message.yCoord, message.zCoord, player, message.side, message.hitX, message.hitY, message.hitZ, message.interact);
				}
			}
			return null;
		}

	}
}
