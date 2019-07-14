package com.github.commoble.tubesreloaded.common.blocks.shunt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.commoble.tubesreloaded.common.blocks.tube.TubeTileEntity;
import com.github.commoble.tubesreloaded.common.registry.TileEntityRegistrar;
import com.github.commoble.tubesreloaded.common.util.WorldHelper;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public class ShuntTileEntity extends TileEntity
{
	public ShuntItemHandler output_handler = new ShuntItemHandler(this, false);
	public ShuntItemHandler input_handler = new ShuntItemHandler(this, true);

	public ShuntTileEntity(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	public ShuntTileEntity()
	{
		this(TileEntityRegistrar.TE_TYPE_SHUNT);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			Direction output_dir = this.getBlockState().get(ShuntBlock.FACING);
			BlockPos output_pos = this.pos.offset(output_dir);
			return (LazyOptional<T>) LazyOptional.of(() -> side.equals(output_dir) ? output_handler : input_handler);
		}
		return super.getCapability(cap, side);
	}
}
