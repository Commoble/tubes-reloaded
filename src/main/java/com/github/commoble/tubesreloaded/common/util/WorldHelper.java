package com.github.commoble.tubesreloaded.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.github.commoble.tubesreloaded.common.brasstube.TileEntityBrassTube;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHelper
{
	public static List<TileEntityBrassTube> getTubesAdjacentTo(World world, BlockPos pos)
	{
		List<TileEntityBrassTube> tes = new ArrayList<TileEntityBrassTube>(6);
		for (Direction face : Direction.values())
		{
			BlockPos checkPos = pos.offset(face);
			TileEntity te = world.getTileEntity(checkPos);
			if (te instanceof TileEntityBrassTube)
			{
				tes.add((TileEntityBrassTube)te);
			}
		}
		
		return tes;
	}
	
	public static Stream<TileEntityBrassTube> getBlockPositionsAsTubeTileEntities(World world, Collection<BlockPos> posCollection)
	{
		Stream<TileEntity> teStream = posCollection.stream().map(tubePos -> world.getTileEntity(tubePos));
		Stream<TileEntity> filteredStream = teStream.filter(te -> te instanceof TileEntityBrassTube);
		return filteredStream.map(te -> (TileEntityBrassTube) te);
	}
}
