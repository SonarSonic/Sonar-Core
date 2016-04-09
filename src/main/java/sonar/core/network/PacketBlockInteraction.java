package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.blocks.IInteractBlock;
import sonar.core.api.utils.BlockInteraction;

public class PacketBlockInteraction extends PacketCoords {

	public BlockInteraction interact;

	public PacketBlockInteraction() {
	}

	public PacketBlockInteraction(BlockPos pos, BlockInteraction interact) {
		super(pos);
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
				Block target = world.getBlockState(message.pos).getBlock();
				if (target != null && target instanceof IInteractBlock) {
					IInteractBlock interact = (IInteractBlock) target;
					interact.operateBlock(world, message.pos, player, message.interact);
				}
			}
			return null;
		}

	}
}
