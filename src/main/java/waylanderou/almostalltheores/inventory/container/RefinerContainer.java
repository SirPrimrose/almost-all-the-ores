package waylanderou.almostalltheores.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import waylanderou.almostalltheores.RefinerRegistryEvents;
import waylanderou.almostalltheores.item.Items;
import waylanderou.almostalltheores.item.crafting.RefinerRecipe;

public class RefinerContainer extends Container {
	private IIntArray machineData;
	private BlockPos tePos;
	private final IInventory machineInventory;
	private final World world;

	public RefinerContainer(int windowId, PlayerInventory playerInventory) {
		this(windowId, playerInventory, new IntArray(5), new Inventory(12), null);
	}    

	public RefinerContainer(int windowId, PlayerInventory playerInventory, IIntArray machineData, IInventory machineInv, BlockPos tePos) {
		super(RefinerRegistryEvents.REFINER_CONTAINER, windowId);
		this.tePos = tePos;		    
		this.machineInventory = machineInv;
		this.machineData = machineData;
		this.world = playerInventory.player.world;

		addSlot(new Slot(machineInv, 0, 56, 53));
		addSlot(new Slot(machineInv, 1, 45, 17));
		addSlot(new Slot(machineInv, 2, 67, 17));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 3, 109, 17));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 4, 127, 17));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 5, 145, 17));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 6, 109, 35));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 7, 127, 35));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 8, 145, 35));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 9, 109, 53));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 10, 127, 53));
		addSlot(new RefinerResultSlot(playerInventory.player, machineInv, 11, 145, 53));
		layoutPlayerInventorySlots(playerInventory, 8, 84);         

		this.trackIntArray(machineData);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {		
		return this.machineInventory.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			itemstack = stack.copy();
			if (index == 0 || index == 1 || index == 2 || index == 3 || index == 4 || index == 5 || index ==  6 || index == 7 || 
					index == 8 || index == 9 || index == 10 || index == 11) { 
				if (!this.mergeItemStack(stack, 12, 48, true)) { 
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(stack, itemstack);
			} else { 
				if (net.minecraft.tileentity.AbstractFurnaceTileEntity.isFuel(stack)) {
					if (!this.mergeItemStack(stack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if(stack.getItem() == Items.SULPHURIC_ACID) {
					if (!this.mergeItemStack(stack, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if(this.hasRecipe(stack)) {
					if (!this.mergeItemStack(stack, 2, 3, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < 39) {
					if (!this.mergeItemStack(stack, 39, 48, false)) { 
						return ItemStack.EMPTY;
					}
				} else if (index < 48 && !this.mergeItemStack(stack, 12, 39, false)) {
					return ItemStack.EMPTY;
				}
			}
			if (stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (stack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, stack);
		}
		return itemstack;
	}	

	private boolean hasRecipe(ItemStack stack) {
		boolean flag = false;
		for(ItemStack s : this.getInventory()) {
			if(s.getItem() == Items.SULPHURIC_ACID) {
				flag = this.world.getRecipeManager().getRecipe((IRecipeType<RefinerRecipe>)RefinerRecipe.REFINING, new Inventory(ItemStack.EMPTY, new ItemStack(Items.SULPHURIC_ACID), stack), this.world).isPresent();
			}
		}		
		return flag || this.world.getRecipeManager().getRecipe((IRecipeType<RefinerRecipe>)RefinerRecipe.REFINING, new Inventory(ItemStack.EMPTY, ItemStack.EMPTY, stack), this.world).isPresent();
	}

	private int addSlotRange(PlayerInventory playerInventory, int index, int x, int y, int amount, int dx) {
		for (int i = 0 ; i < amount ; i++) {
			this.addSlot(new Slot(playerInventory, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}

	private int addSlotBox(PlayerInventory playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
		for (int j = 0 ; j < verAmount ; j++) {
			index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
			y += dy;
		}
		return index;
	}

	private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int leftCol, int topRow) {		  
		addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);		
		topRow += 58;
		addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
	}

	public int getArrowProgression() { 
		int i = hasBeenProcessingFor();
		int j = this.machineData.get(3);

		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	public int getFireProgression() {
		int i = this.machineData.get(4);
		if(i == 0) i = 200;
		return getFuelLeft() * 13 / i; 
	}

	public boolean isProcessingOre() {
		return this.hasBeenProcessingFor()>0;
	}    

	public int hasBeenProcessingFor() {    	
		return this.machineData.get(0);
	}

	public int getSecondCounter() { 
		return this.machineData.get(2);    	
	}

	public int getFuelLeft() {    	
		return this.machineData.get(1);  	
	}

}
