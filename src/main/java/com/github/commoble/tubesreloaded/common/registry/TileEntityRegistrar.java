package com.github.commoble.tubesreloaded.common.registry;

import java.util.stream.IntStream;

import com.github.commoble.tubesreloaded.common.TubesReloadedMod;
import com.github.commoble.tubesreloaded.common.blocks.filter.FilterTileEntity;
import com.github.commoble.tubesreloaded.common.blocks.shunt.ShuntTileEntity;
import com.github.commoble.tubesreloaded.common.blocks.tube.TubeTileEntity;
import com.github.commoble.tubesreloaded.common.blocks.tube.redstone_tube.RedstoneTubeTileEntity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(TubesReloadedMod.MODID)
public class TileEntityRegistrar
{
	@ObjectHolder(BlockNames.TUBE_NAME)
	public static final TileEntityType<TubeTileEntity> TE_TYPE_TUBE = null;
	@ObjectHolder(BlockNames.SHUNT_NAME)
	public static final TileEntityType<ShuntTileEntity> TE_TYPE_SHUNT = null;
	@ObjectHolder(BlockNames.REDSTONE_TUBE_NAME)
	public static final TileEntityType<RedstoneTubeTileEntity> TE_TYPE_REDSTONE_TUBE = null;
	@ObjectHolder(BlockNames.FILTER_NAME)
	public static final TileEntityType<FilterTileEntity> TE_TYPE_FILTER = null;
	
	
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
		// register standard tube and colored tubes with the same TE
		Block[] tubes = new Block[17];
		IntStream.range(0, 16).forEach(i -> tubes[i] = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(TubesReloadedMod.MODID, BlockNames.COLORED_TUBE_NAMES[i])));
		tubes[16] = BlockRegistrar.TUBE;	// need an array with all the color tubes + the original tube since they use the same TE
		event.getRegistry().register(TileEntityType.Builder.create(TubeTileEntity::new, tubes)
				.build(null)
				.setRegistryName(BlockNames.TUBE_NAME)
				);
		event.getRegistry().register(TileEntityType.Builder.create(ShuntTileEntity::new,BlockRegistrar.SHUNT)
				.build(null)
				.setRegistryName(BlockNames.SHUNT_NAME)
				);
		event.getRegistry().register(TileEntityType.Builder.create(RedstoneTubeTileEntity::new,BlockRegistrar.REDSTONE_TUBE)
				.build(null)
				.setRegistryName(BlockNames.REDSTONE_TUBE_NAME)
				);
		event.getRegistry().register(TileEntityType.Builder.create(FilterTileEntity::new,BlockRegistrar.FILTER)
				.build(null)
				.setRegistryName(BlockNames.FILTER_NAME)
				);
		
		
	}
}
