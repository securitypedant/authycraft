package com.authycraft.secondFactor;

import java.util.HashMap;
import java.util.Map;

import com.authycraft.secondFactor.item.AuthyDoor;

import cpw.mods.fml.common.FMLCommonHandler;
//import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy implements IGuiHandler{
	/** Used to store IExtendedEntityProperties data temporarily between player death and respawn */
	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();
	
	EventHandlers events = new EventHandlers();
	
	@Override
    public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
    {
		return null;
    }

    @Override
    public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
    {
    	return null;
    }

	
	public void preInit(FMLPreInitializationEvent e) {
		// Register on the events bus so that we can interact with player logins.
		FMLCommonHandler.instance().bus().register(events);
		MinecraftForge.EVENT_BUS.register(events);
		
		// AuthyDoor.init();
    }

    public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }
    
    /**
    * Adds an entity's custom data to the map for temporary storage
    * @param compound An NBT Tag Compound that stores the IExtendedEntityProperties data only
    */
    public void storeEntityData(String name, NBTTagCompound compound)
    {
    	extendedEntityData.put(name, compound);
    }

    /**
    * Removes the compound from the map and returns the NBT tag stored for name or null if none exists
    */
    public NBTTagCompound getEntityData(String name)
    {
        NBTTagCompound entityData = extendedEntityData.get(name);
        extendedEntityData.remove(name);
        return entityData;
    }
}
