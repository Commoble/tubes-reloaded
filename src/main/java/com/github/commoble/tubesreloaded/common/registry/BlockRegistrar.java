package com.github.commoble.tubesreloaded.common.registry;

import com.github.commoble.tubesreloaded.common.TubesReloadedMod;
import com.github.commoble.tubesreloaded.common.blocks.loader.LoaderBlock;
import com.github.commoble.tubesreloaded.common.blocks.shunt.ShuntBlock;
import com.github.commoble.tubesreloaded.common.blocks.tube.TubeBlock;
import com.github.commoble.tubesreloaded.common.blocks.tube.redstone_tube.RedstoneTubeBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(TubesReloadedMod.MODID)
public class BlockRegistrar
{
	@ObjectHolder(BlockNames.TUBE_NAME)
	public static final TubeBlock TUBE = null;
	@ObjectHolder(BlockNames.SHUNT_NAME)
	public static final ShuntBlock SHUNT = null;
	@ObjectHolder(BlockNames.LOADER_NAME)
	public static final LoaderBlock LOADER = null;
	@ObjectHolder(BlockNames.REDSTONE_TUBE_NAME)
	public static final RedstoneTubeBlock REDSTONE_TUBE = null;

	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> registry = event.getRegistry();
		
		registerBlock(registry, new TubeBlock(Block.Properties.create(Material.GLASS, MaterialColor.YELLOW_TERRACOTTA).hardnessAndResistance(0.4F).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)), BlockNames.TUBE_NAME);
		registerBlock(registry, new ShuntBlock(Block.Properties.create(Material.CLAY).hardnessAndResistance(2F, 6F).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)), BlockNames.SHUNT_NAME);
		registerBlock(registry, new LoaderBlock(Block.Properties.create(Material.CLAY).hardnessAndResistance(2F, 6F).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)), BlockNames.LOADER_NAME);
		registerBlock(registry, new RedstoneTubeBlock(Block.Properties.create(Material.GLASS, MaterialColor.GOLD).hardnessAndResistance(0.4F).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)), BlockNames.REDSTONE_TUBE_NAME);
	}
	
	private static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T newBlock, String name)
	{
		String prefixedName = TubesReloadedMod.MODID + ":" + name;
		newBlock.setRegistryName(prefixedName);
		registry.register(newBlock);
		return newBlock;
	}
}
