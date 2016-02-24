package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.IInteractBlock;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketBlockInteraction extends PacketCoords {

	public BlockInteraction interact;

	public PacketBlockInteraction() {
	}

	public PacketBlockInteraction(int x, int y, int z, BlockInteraction interact) {
		super(x, y, z);
		this.interact = interact;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.interact = BlockInteraction.readFromBuf(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		this.interact.writeToBuf(buf);
	}

	public static class Handler extends PacketCoordsHandler<PacketBlockInteraction> {
		@Override
		public IMessage processMessage(PacketBlockInteraction message, World world, EntityPlayer player) {
			if (!world.isRemote) {
				Block target = world.getBlock(message.xCoord, message.yCoord, message.zCoord);
				if (target != null && target instanceof IInteractBlock) {
					IInteractBlock interact = (IInteractBlock) target;
					interact.operateBlock(world, message.xCoord, message.yCoord, message.zCoord, player, message.interact);
				}
			}
			return null;
		}

	}
}
