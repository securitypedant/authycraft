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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;


public class CommonProxy implements IGuiHandler{
	/** Used to store IExtendedEntityProperties data temporarily between player death and respawn */
	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();
	
	// Global mod constants that will be populated from the config file in the preInit.
	public static String AUTHYAPIKEY, AUTHYAPIURL, AUTHYAPPLYTOGROUP;
	public static int AUTHYSESSIONTIMEOUT, AUTHY2FATIMEOUT;
	public static boolean AUTHYFAILSECURE;
	// Variables to be used across the mod.
	public static int Authy2FAReminderTicksCount = 300;				// Already start the timer for 2FA reminders at 300 ticks, i.e 15 seconds after login.
	
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
        // TODO: Need to test for Authy API Java helper library. Or do we build it into our code?
		
		// Register on the events bus so that we can capture events for us to interact with.
		FMLCommonHandler.instance().bus().register(events);
		MinecraftForge.EVENT_BUS.register(events);
		
        // Setup the config file that will be in .minecraft/config/ and it will be named secondFactor.cfg
        Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		
        // Load the configuration from its file
        config.load();
        
        // TODO: MUST check for this at the right point in code and disable/unload mod if the key is not set. But where?
        // AuthyAPI key.
        Property configAuthyAPIKey = config.get("Service", "APIKey", "");
        configAuthyAPIKey.comment = "REQUIRED: API key for the Authy app that is protecting your Minecraft server. Without this, nothing works.";
        AUTHYAPIKEY = configAuthyAPIKey.getString();
        
        // AuthyAPI URL
        Property configAuthyAPIURL = config.get("Service", "APIURL", "https://api.authy.com/");
        configAuthyAPIURL.comment = "URL to the Authy API. Very rare to change this unless you are using the sandbox.";
        AUTHYAPIURL = configAuthyAPIURL.getString();

        // Should Authy only apply to OPS? or all users?
        Property configAuthyApplyToGroup = config.get("Login", "2FAApplyToGroup", "OPS");
        configAuthyApplyToGroup.comment = "What group of players should Authy enforce 2FA for? OPS by default. Valid values are: OPS, ALL";
        AUTHYAPPLYTOGROUP = configAuthyApplyToGroup.getString();
        
        // Authy 2FA timeout, how long before we boot the user when no Authy token is given after initial login.
        Property configAuthyTimeout = config.get("Login", "2FATimeOut", 60);
        configAuthyTimeout.comment = "Timeout in seconds for player to respond to 2FA request, after which player is disconnected. Default is a whopping 60 seconds.";
        AUTHY2FATIMEOUT = configAuthyTimeout.getInt();
        
        // Authy 2FA timeout for the session. I.e. how long before we prompt the user for 2FA again.
        Property configAuthySessionTimeout = config.get("Login", "2FASessionTimeOut", 120);
        configAuthySessionTimeout.comment = "Timeout in seconds for 2FA session. If player relogins into server during this session, 2FA is not enforced. Default is 21600 (6 hours)";
        AUTHYSESSIONTIMEOUT = configAuthySessionTimeout.getInt();
        
        // If there is a total failure with the 2FA process, do we fail safe? i.e. let people login, or fail secure, prevent logins. 
        // Defaults to fail safe and let people login if Authy not available.
        Property configAuthyFailSecure = config.get("Login", "FailSecure", false);
        configAuthyFailSecure.comment = "Define behaviour if Minecraft cannot communicate with Authy. true = Player cannot login, false = Player logs in without 2FA step. Default = false";
        AUTHYFAILSECURE = configAuthyFailSecure.getBoolean();
        
        // Save the configuration to its file
        config.save();        
        
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
