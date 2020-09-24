package commoble.tubesreloaded.blocks.tube;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class RaytraceHelper
{
	public static Vector3d[] getInterpolatedDifferences(Vector3d vector)
	{
		int points = 17; // 16 segments
		Vector3d[] list = new Vector3d[points];

		double dx = vector.getX();
		double dy = vector.getY();
		double dz = vector.getZ();

		for (int point = 0; point < points; point++)
		{
			double startLerp = getFractionalLerp(point, points - 1);
			list[point] = new Vector3d(startLerp * dx, startLerp * dy, startLerp * dz);
		}

		return list;
	}

	public static Vector3d[] getInterpolatedPoints(Vector3d lower, Vector3d upper)
	{
		Vector3d diff = upper.subtract(lower);
		Vector3d[] diffs = getInterpolatedDifferences(diff);
		Vector3d[] points = new Vector3d[diffs.length];
		for (int i = 0; i < points.length; i++)
		{
			points[i] = lower.add(diffs[i]);
		}
		return points;
	}

	public static double getFractionalLerp(int current, int max)
	{
		return (double) current / (double) max;
	}
	
	/** Returns the vector representing the center of the side of a tube block **/
	public static Vector3d getTubeSideCenter(BlockPos pos, Direction side)
	{
		Vector3d center = TubeTileEntity.getCenter(pos);
		double offsetFromCenter = 4D/16D;
		double xOff = side.getXOffset() * offsetFromCenter;
		double yOff = side.getYOffset() * offsetFromCenter;
		double zOff = side.getZOffset() * offsetFromCenter;
		return center.add(xOff, yOff, zOff);
	}

	@Nullable
	public static Vector3d getTubeRaytraceHit(Vector3d startVec, Vector3d endVec, World world)
	{
		Vector3d[] points = getInterpolatedPoints(startVec, endVec);
		TubeRayTraceSelectionContext selector = new TubeRayTraceSelectionContext();
		int pointCount = points.length;
		int rayTraceCount = pointCount-1;
		for (int i=0; i<rayTraceCount; i++)
		{
			TubeRayTraceContext context = new TubeRayTraceContext(selector, points[i], points[i+1], BlockMode.COLLIDER, FluidMode.NONE);
			BlockRayTraceResult result = rayTraceBlocks(world, context);
			if (result.getType() != RayTraceResult.Type.MISS)
			{
				return result.getHitVec();
			}
		}
		
		return null; // didn't hit
		
	}

	// vanilla raytracer requires a non-null entity when the context is constructed
	// we don't need an entity though
	public static BlockRayTraceResult rayTraceBlocks(World world, TubeRayTraceContext context)
	{
		return doRayTrace(context, (rayTraceContext, pos) ->
		{
			BlockState state = world.getBlockState(pos);
			Vector3d startVec = rayTraceContext.getStartVec();
			Vector3d endVec = rayTraceContext.getEndVec();
			VoxelShape shape = rayTraceContext.getBlockShape(state, world, pos);
			BlockRayTraceResult result = world.rayTraceBlocks(startVec, endVec, pos, shape, state);
			return result;
		}, (rayTraceContext) -> {
			Vector3d difference = rayTraceContext.getStartVec().subtract(rayTraceContext.getEndVec());
			return BlockRayTraceResult.createMiss(rayTraceContext.getEndVec(), Direction.getFacingFromVector(difference.x, difference.y, difference.z), new BlockPos(rayTraceContext.getEndVec()));
		});
	}

	static <T> T doRayTrace(TubeRayTraceContext context, BiFunction<TubeRayTraceContext, BlockPos, T> rayTracer, Function<TubeRayTraceContext, T> missFactory)
	{
		Vector3d start = context.getStartVec();
		Vector3d end = context.getEndVec();
		if (start.equals(end))
		{
			return missFactory.apply(context);
		}
		else
		{
			double endX = MathHelper.lerp(-1.0E-7D, end.x, start.x);
			double endY = MathHelper.lerp(-1.0E-7D, end.y, start.y);
			double endZ = MathHelper.lerp(-1.0E-7D, end.z, start.z);
			double startX = MathHelper.lerp(-1.0E-7D, start.x, end.x);
			double startY = MathHelper.lerp(-1.0E-7D, start.y, end.y);
			double startZ = MathHelper.lerp(-1.0E-7D, start.z, end.z);
			int startXInt = MathHelper.floor(startX);
			int startYInt = MathHelper.floor(startY);
			int startZInt = MathHelper.floor(startZ);
			BlockPos.Mutable mutaPos = new BlockPos.Mutable(startXInt, startYInt, startZInt);
			T result = rayTracer.apply(context, mutaPos);
			if (result != null)
			{
				return result;
			}
			else
			{
				double dx = endX - startX;
				double dy = endY - startY;
				double dz = endZ - startZ;
				int xSign = MathHelper.signum(dx);
				int ySign = MathHelper.signum(dy);
				int zSign = MathHelper.signum(dz);
				double reciprocalX = xSign == 0 ? Double.MAX_VALUE : xSign / dx;
				double reciprocalY = ySign == 0 ? Double.MAX_VALUE : ySign / dy;
				double reciprocalZ = zSign == 0 ? Double.MAX_VALUE : zSign / dz;
				double calcX = reciprocalX * (xSign > 0 ? 1.0D - MathHelper.frac(startX) : MathHelper.frac(startX));
				double calcY = reciprocalY * (ySign > 0 ? 1.0D - MathHelper.frac(startY) : MathHelper.frac(startY));
				double calcZ = reciprocalZ * (zSign > 0 ? 1.0D - MathHelper.frac(startZ) : MathHelper.frac(startZ));

				while (calcX <= 1.0D || calcY <= 1.0D || calcZ <= 1.0D)
				{
					if (calcX < calcY)
					{
						if (calcX < calcZ)
						{
							startXInt += xSign;
							calcX += reciprocalX;
						}
						else
						{
							startZInt += zSign;
							calcZ += reciprocalZ;
						}
					}
					else if (calcY < calcZ)
					{
						startYInt += ySign;
						calcY += reciprocalY;
					}
					else
					{
						startZInt += zSign;
						calcZ += reciprocalZ;
					}

					T fallbackResult = rayTracer.apply(context, mutaPos.setPos(startXInt, startYInt, startZInt));
					if (fallbackResult != null)
					{
						return fallbackResult;
					}
				}

				return missFactory.apply(context);
			}
		}
	}
}
