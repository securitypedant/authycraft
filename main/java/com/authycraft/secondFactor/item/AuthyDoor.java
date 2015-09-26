package com.authycraft.secondFactor.item;

import com.authycraft.secondFactor.SecondFactor;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

public class AuthyDoor extends ItemDoor {
	
	public AuthyDoor() {
		super(Material.wood);
	}

	public static ItemDoor authyDoor;
	
	public static final void init() {

	//	authyDoor = new ItemDoor().setUnlocalizedName("Authy Secured Door").setCreativeTab(CreativeTabs.tabRedstone);
		GameRegistry.registerItem(authyDoor, "Authy Secured Door");
		GameRegistry.addRecipe(new ItemStack(AuthyDoor.authyDoor), new Object[] {"RC","CR","WW",'R', Items.redstone, 'C', Items.clay_ball, 'W', Blocks.planks} );
		
		// .setTextureName("/assets/minecraft/textures/items/door_wood.png")
		//GameRegistry.addRecipe(new ItemStack(Items.iron_pickaxe), new Object[] {"###", " I ", " I ", '#', Items.iron_ingot, 'I', Items.stick});
		//GameRegistry.addRecipe(new ItemStack(ModBlocks.tutorialBlock), new Object[] {"##", "##", '#', ModItems.tutorialItem});
    }
}
