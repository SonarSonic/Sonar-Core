package sonar.core.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeHooks;
import sonar.core.utils.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class RayTraceHelper {

	@Nonnull
    public static Pair<RayTraceResult, AxisAlignedBB> rayTraceBoxes(BlockPos pos, Vec3d start, Vec3d end, List<AxisAlignedBB> boxes) {
		Vec3d vec3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d vec3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		RayTraceResult raytraceresult = null;
		AxisAlignedBB currentBB = null;
		for (AxisAlignedBB bb : boxes) {
			raytraceresult = bb.calculateIntercept(vec3d, vec3d1);
			if (raytraceresult != null) {
				currentBB = bb;
				break;
			}

		}
		return raytraceresult == null ? new Pair(null, null) : new Pair(new RayTraceResult(raytraceresult.hitVec.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos), currentBB);
	}

	public static double getBlockReach(EntityPlayer player) {
		double blockReachDistance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
		return blockReachDistance + 1;
	}

	public static Pair<Vec3d, Vec3d> getPlayerLookVec(EntityPlayer entity) {
		double length = getBlockReach(entity);
		Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().x * length, entity.getLookVec().y * length, entity.getLookVec().z * length));
		return new Pair(startPos, endPos);
	}

	public static Pair<Vec3d, Vec3d> getStandardLookVec(Entity entity, double reach) {
		Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().x * reach, entity.getLookVec().y * reach, entity.getLookVec().z * reach));
		return new Pair(startPos, endPos);
	}

	public static RayTraceResult getRayTraceEyes(EntityPlayer player) {
		return ForgeHooks.rayTraceEyes(player, getBlockReach(player));
	}
}
