package com.authycraft.secondFactor.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class AuthyGuiHandler implements IGuiHandler {
	
	public static final int AUTHYGUI = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == AUTHYGUI)
			return new AuthyGui();
		return null;
	}

}
