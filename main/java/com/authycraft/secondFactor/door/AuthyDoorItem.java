package com.authycraft.secondFactor.door;

import com.authycraft.secondFactor.SecondFactor;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

public class AuthyDoorItem extends ItemDoor {
	
	/*
	 *  Notes for things to do 
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
	
	public AuthyDoorItem() {
		super(Material.iron);
	}
	
	public static ItemDoor authyDoor;
	

	
	public static final void init() {

	//	authyDoor = new ItemDoor().setUnlocalizedName("Authy Secured Door").setCreativeTab(CreativeTabs.tabRedstone);
		GameRegistry.registerItem(authyDoor, "Authy Secured Door");
		GameRegistry.addRecipe(new ItemStack(AuthyDoorItem.authyDoor), new Object[] {"RC","CR","II",'R', Items.redstone, 'C', Items.clay_ball, 'I', Blocks.iron_bars} );
		
		// .setTextureName("/assets/minecraft/textures/items/door_wood.png")
		//GameRegistry.addRecipe(new ItemStack(Items.iron_pickaxe), new Object[] {"###", " I ", " I ", '#', Items.iron_ingot, 'I', Items.stick});
		//GameRegistry.addRecipe(new ItemStack(ModBlocks.tutorialBlock), new Object[] {"##", "##", '#', ModItems.tutorialItem});
    }
}
