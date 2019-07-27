package com.github.commoble.tubesreloaded.common.blocks.filter;

import com.github.commoble.tubesreloaded.common.routing.Endpoint;
import com.github.commoble.tubesreloaded.common.util.WorldHelper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public class FilterItemHandler implements IItemHandler
{
	public FilterTileEntity filter;
	
	public FilterItemHandler (FilterTileEntity filter)
	{
		this.filter = filter;
	}

	@Override
	public int getSlots()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		// TODO Auto-generated method stub
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (!this.isItemValid(slot, stack))
		{
			return stack.copy();
		}
		
		if (!simulate) // actually inserting an item
		{
			// attempt to insert item
			BlockPos pos = this.filter.getPos();
			Direction output_dir = this.filter.getBlockState().get(FilterBlock.FACING);
			BlockPos output_pos = pos.offset(output_dir);
			// if the block we are attempting to insert the item into is a shuntlike block, do not insert
			Tag<Block> shuntTag = BlockTags.getCollection().get(new ResourceLocation("tubesreloaded", "shunts"));
			ItemStack remaining = WorldHelper.getTEItemHandlerAtIf(filter.getWorld(), output_pos, output_dir.getOpposite(), te -> !shuntTag.contains(te.getBlockState().getBlock()))
			.map(handler -> Endpoint.disperseItemToHandler(stack, handler))
			.orElse(stack.copy());
			
			if (remaining.getCount() > 0) // we have remaining items
			{
				WorldHelper.ejectItemstack(this.filter.getWorld(), pos, output_dir, remaining);
			}
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack)
	{
		return stack.getCount() > 0 && this.filter.canItemPassThroughFilter(stack);
	}

}
