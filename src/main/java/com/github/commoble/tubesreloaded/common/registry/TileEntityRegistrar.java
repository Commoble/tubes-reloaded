package com.github.commoble.tubesreloaded.common.registry;

import com.github.commoble.tubesreloaded.common.TubesReloadedMod;
import com.github.commoble.tubesreloaded.common.tube.TubeTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(TubesReloadedMod.MODID)
public class TileEntityRegistrar
{
	@ObjectHolder(BlockNames.TUBE_NAME)
	public static final TileEntityType<TubeTileEntity> TE_TYPE_TUBE = null;
	
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
		event.getRegistry().register(TileEntityType.Builder.create(TubeTileEntity::new,BlockRegistrar.TUBE)
				.build(null)
				.setRegistryName(BlockNames.TUBE_NAME)
				);
	}
}
