package com.github.commoble.tubesreloaded.common.blocks.shunt;

import com.github.commoble.tubesreloaded.common.registry.TileEntityRegistrar;
import com.github.commoble.tubesreloaded.common.util.DirectionHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ShuntBlock extends Block
{
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	
	protected final VoxelShape[] shapes;

	public ShuntBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
		this.shapes=this.makeShapes();
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return TileEntityRegistrar.TE_TYPE_SHUNT.create();
	}

	//// facing and blockstate boilerplate

	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return this.getDefaultState().with(FACING, DirectionHelper.getBlockFacingForPlacement(context));
	}

	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn)
	{
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}
	
	/// model shapes

	protected VoxelShape[] makeShapes()
	{
		final double MIN_VOXEL = 0D;
		final double ONE_QUARTER = 4D;
		final double THREE_QUARTERS = 12D;
		final double SIX_SIXTEENTHS = 6D;
		final double TEN_SIXTEENTHS = 10D;
		final double MAX_VOXEL = 16D;
		
		// one set of shapes for each state * six directional states = 6 sets of shapes
		VoxelShape[] shapes = new VoxelShape[6];

		// define the shapes for the piping core and the dunswe pipe segments
		// reminder: north = negative
		
		// core voxels
		VoxelShape coreNorth = Block.makeCuboidShape(ONE_QUARTER, ONE_QUARTER, MIN_VOXEL,
				THREE_QUARTERS, THREE_QUARTERS, THREE_QUARTERS);
		VoxelShape coreSouth = Block.makeCuboidShape(ONE_QUARTER, ONE_QUARTER, ONE_QUARTER,
				THREE_QUARTERS, THREE_QUARTERS,	MAX_VOXEL);
		VoxelShape coreWest = Block.makeCuboidShape(MIN_VOXEL, ONE_QUARTER, ONE_QUARTER,
				THREE_QUARTERS, THREE_QUARTERS, THREE_QUARTERS);
		VoxelShape coreEast = Block.makeCuboidShape(ONE_QUARTER, ONE_QUARTER, ONE_QUARTER,
				MAX_VOXEL, THREE_QUARTERS, THREE_QUARTERS);
		VoxelShape coreDown = Block.makeCuboidShape(ONE_QUARTER, MIN_VOXEL, ONE_QUARTER,
				THREE_QUARTERS, THREE_QUARTERS, THREE_QUARTERS);
		VoxelShape coreUp = Block.makeCuboidShape(ONE_QUARTER, ONE_QUARTER, ONE_QUARTER,
				THREE_QUARTERS, MAX_VOXEL, THREE_QUARTERS);
		
		// tube voxels
		VoxelShape down = Block.makeCuboidShape(SIX_SIXTEENTHS, MIN_VOXEL, SIX_SIXTEENTHS, TEN_SIXTEENTHS,
				THREE_QUARTERS, TEN_SIXTEENTHS);
		VoxelShape up = Block.makeCuboidShape(SIX_SIXTEENTHS, THREE_QUARTERS, SIX_SIXTEENTHS, TEN_SIXTEENTHS, MAX_VOXEL,
				TEN_SIXTEENTHS);
		VoxelShape north = Block.makeCuboidShape(SIX_SIXTEENTHS, SIX_SIXTEENTHS, MIN_VOXEL, TEN_SIXTEENTHS,
				TEN_SIXTEENTHS, ONE_QUARTER);
		VoxelShape south = Block.makeCuboidShape(SIX_SIXTEENTHS, SIX_SIXTEENTHS, THREE_QUARTERS, TEN_SIXTEENTHS,
				TEN_SIXTEENTHS, MAX_VOXEL);
		VoxelShape west = Block.makeCuboidShape(MIN_VOXEL, SIX_SIXTEENTHS, SIX_SIXTEENTHS, THREE_QUARTERS,
				TEN_SIXTEENTHS, TEN_SIXTEENTHS);
		VoxelShape east = Block.makeCuboidShape(THREE_QUARTERS, SIX_SIXTEENTHS, SIX_SIXTEENTHS, MAX_VOXEL,
				TEN_SIXTEENTHS, TEN_SIXTEENTHS);
		
		VoxelShape[] tube_dunswe = { down, up, north, south, west, east };
		VoxelShape[] core_dunswe = { coreDown, coreUp, coreNorth, coreSouth, coreWest, coreEast};

		for (int state_dir = 0; state_dir < 6; state_dir++)
		{
			VoxelShape stateShape = core_dunswe[state_dir];
			for (int voxel_dir=0; voxel_dir<6; voxel_dir++)
			{
				if (voxel_dir != state_dir)
				{
					stateShape = VoxelShapes.or(stateShape, tube_dunswe[voxel_dir]);
				}
			}
			shapes[state_dir] = stateShape;
		}

		return shapes;
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return state.getShape(worldIn, pos);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return this.getShape(state, worldIn, pos, context);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return this.shapes[this.getShapeIndex(state)];
	}

	public int getShapeIndex(BlockState state)
	{
		return state.get(FACING).getIndex();
	}
}
