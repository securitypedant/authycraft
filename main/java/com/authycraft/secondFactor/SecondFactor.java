package com.authycraft.secondFactor;

import com.authycraft.secondFactor.commands.AuthyCode;
import com.authycraft.secondFactor.commands.AuthyRegisterUser;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.init.Blocks;

@Mod(modid=SecondFactor.MODID, name=SecondFactor.MODNAME, version=SecondFactor.MODVER) //Tell forge "Oh hey, there's a new mod here to load."
// @NetworkMod(clientSideRequired = true, serverSideRequired = true);

public class SecondFactor
{
    //Set the ID of the mod (Should be lower case).
    public static final String MODID = "secondfactor";
    //Set the "Name" of the mod.
    public static final String MODNAME = "Authy Second Factor (2FA) Mod";
    //Set the version of the mod.
    public static final String MODVER = "0.0.2";

    @Instance(value = SecondFactor.MODID) // Tell Forge what instance to use.
    public static SecondFactor instance;
    
    @SidedProxy(clientSide="com.authycraft.secondFactor.client.ClientProxy", serverSide="com.authycraft.secondFactor.server.ServerProxy" )
    public static CommonProxy proxy;
  
    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
    	// Call proxy, no other code needs to go in here.
        proxy.preInit(e);
    }
        
    @EventHandler
    public void init(FMLInitializationEvent e)
    {
    	// Call proxy, no other code needs to go in here.
    	proxy.init(e);
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
    	// Call proxy, no other code needs to go in here.
    	proxy.postInit(e);
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
      event.registerServerCommand(new AuthyCode());
      event.registerServerCommand(new AuthyRegisterUser());
    }
}