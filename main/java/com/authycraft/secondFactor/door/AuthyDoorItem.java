package com.authycraft.secondFactor.door;

import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.SecondFactor;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AuthyDoorItem extends ItemDoor {
	/*  Notes for things to do 
	 *  
	 *  A door is both a block and an item. We need to create two classes. One to address the block and one for item.
	 *  There is a BlockDoor and an ItemDoor.
	 *  
	 *  Let's create an Iron door and over-ride the lower image. Also do we need to worry about the whole Redstone interaction?
	 *  Should we use Redstone to be able to trigger the Authy OneTouch?
	 *  
	 *  We need to achieve the following.
	 *  
	 *  1. Use the Authy texture.
	 *  2. When someone attempts to open the door, present user with a GUI.
	 *  	a) Get code that was created via SMS or Authy app
	 *  	b) A button to trigger push notification.
	 *  	c) UI should work irregardless of player name.
	 *  3. Need to check when door is activated if the current player is a registered Authy user.
	 *  	a) Note not all users will have done Authy registration if 2FA is only used for OPS
	 *  4. When door is placed, we need to associate with a user. Does this mean creating extended attributes for the door?
	 *  5. Register with the game and add recipe. 
	 *  
	 */
	
	public static CommonProxy proxy = new CommonProxy();
	public static ItemDoor authyDoor;
	public BlockDoor AuthyDoorBlock;
	
	public AuthyDoorItem() {
		// Setup defaults
		super(Material.iron);
		this.maxStackSize = 1;
		setUnlocalizedName("Authy Secured Door");
		setTextureName("authycraft" + ":door_authy");
        setCreativeTab(CreativeTabs.tabRedstone);
	}
	
   @Override
   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister)
   {
           this.itemIcon = iconRegister.registerIcon("authycraft" + ":door_authy");
   }
	
   /**
    * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
    * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
    */
   @Override
	public boolean onItemUse(ItemStack stack, EntityPlayer par2EntityPlayer, World worldIn, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (side != 1) {
			return false;
		} else {
			++y;
			Block block = AuthyDoorBlock;
			if (par2EntityPlayer.canPlayerEdit(x, y, z, side, stack)
					&& par2EntityPlayer.canPlayerEdit(x, y + 1, z, side, stack)) {
				if (!block.canPlaceBlockAt(worldIn, x, y, z)) {
					return false;
				} else {
					int i1 = MathHelper.floor_double((double) ((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
					AuthyDoorItem.placeDoorBlock(worldIn, x, y, z, i1, block);
					--stack.stackSize;
					return true;
				}
			} else {
				return false;
			}
			
			
			
			
		}
	}
	
}
