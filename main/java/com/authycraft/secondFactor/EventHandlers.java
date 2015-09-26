package com.authycraft.secondFactor;

import java.util.Calendar;
import java.util.Date;

import com.authy.AuthyApiClient;
import com.authy.api.Tokens;
import com.authy.api.Users;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class EventHandlers {
	
	public static CommonProxy proxy = new CommonProxy();
	
	@SubscribeEvent
	public void playerRespawnEvent (PlayerRespawnEvent event) {
		System.out.println("Player respawned");
	}
	
	// Called whenever the player is updated or ticked
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		// TODO: How often do we check the tick? every player tick? Probably only need to execute this code every second at the most. Would this be a good config setting? 
		if(event.phase == Phase.START) { 
			// Get extended properties for all players. It's is from here we know if a user has registered yet with Authy.
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.player);
			
			// If the player has no Authy data, continue to keep them in place.
			// Do we need to do this? Is this use case covered in initial login? Or does the player tick fire even before the player login?
			if (props.getAuthyCell() == "") {
				props.setPlayerAwaitingAuthy(true);
			}
			
			// Wrap all the logic for if we are waiting on an Authy code into a big if.
			if (props.getPlayerAwaitingAuthy()) {
				// If player has taken too long to provide the 2FA response, kick them off the server!
				if (event.player.ticksExisted > (CommonProxy.AUTHY2FATIMEOUT * 20)) {
					// Disconnect player.
					// TODO: How do we correctly disconnect the player?
					System.out.println("Player '" + event.player.getDisplayName() + "' didn't respond to Authy 2FA in time,(" + (event.player.ticksExisted / 20) + " seconds) being disconnected.");
				}
				
				// If we are still waiting for player to run /atcode, let's remind them every 10 seconds.
				if (event.player.ticksExisted > CommonProxy.Authy2FAReminderTicksCount) {
					// Increment the count of ticks by another 100 ticks/10 seconds
					CommonProxy.Authy2FAReminderTicksCount = CommonProxy.Authy2FAReminderTicksCount + 300;
					// If the player needs to perform the second factor.
					if (props.getAuthyCell() != "") {
						// Communicate to the user they need to run the command to tell us the Authy code.
						event.player.addChatComponentMessage(new ChatComponentText("You need to pass Authy second factor, please run /atcode"));
					}
				}
				
				// If the player should be standing still, we get this from the extended props. 
				// TODO: Can we invoke the sleeping GUI or something similar?
				// Make sure they are still awaiting Authy response and also check if we've actually stored a position yet. The tick can fire before the login event.
				if (props.getLoginPosX() != null) {
					// Where are they standing?
					Double xLoc = props.getLoginPosX();
					Double yLoc = props.getLoginPosY();
					Double zLoc = props.getLoginPosZ();
					
					// Keep the player in the same place when they logged in, until they pass the Authy factor.
					event.player.setPositionAndUpdate(xLoc, yLoc, zLoc);
				}
			} else {
				// We are not waiting for the player to provide an Authy response.
			}
		}
	}
	
	@SubscribeEvent
	/**
	 * Capture player login event from Forge and insert the Authy second factor phase. This keeps a player in the same location in the world until
	 * they successfully pass an Authy second factor. If they fail, they get disconnected.
	 * 
	 * @param event
	 */
	public void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		boolean bEnforce2FA = CommonProxy.AUTHYFAILSECURE; // Default if 2FA enforced based on fail safe config.
		// Get extended properties for all players. It's is from here we know if a user has registered yet with Authy.
		ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.player);

		// Determine how server is supposed to enforce 2FA, and deterine if player fits into that enforcement model.
		// TODO: Add in ForgeEssentials permission model. Allow more than just OPS, allow any FE group to be used.
		if (CommonProxy.AUTHYAPPLYTOGROUP == "OPS") {
			// Only users who are OPS need to go through the 2FA step.
			Boolean bPlayerOp = MinecraftServer.getServer().getConfigurationManager().func_152596_g(event.player.getGameProfile());
			if (bPlayerOp) {
				bEnforce2FA = true;		// This player is an OP, 2FA will be enforced.
			} else {
				bEnforce2FA = false;	// This player is not an OP, 2FA will be ignored.
			}
		} else if (CommonProxy.AUTHYAPPLYTOGROUP == "ALL") {
			// EVERYONE must pass the 2FA step!
			bEnforce2FA = true; 
		} else {
			// Uh oh, unidentified group of players the server admin wants 2FA for. Fail to OPS if we fail secure, fail to ALL if we fail safe.
			bEnforce2FA = CommonProxy.AUTHYFAILSECURE; // Fall back to enforcing 2FA based on fail safe config.
		}
		// Set extended attribute which is used elsewhere to reflect outcome from above.
		props.setPlayerAwaitingAuthy(bEnforce2FA);
		
		// ENFORCE 2FA AT LOGIN?
		if (bEnforce2FA) {	
			// Grab the current logged in position and store it, so in later player tick events, we can use it to keep player in place.
			props.setLoginPosX(event.player.posX);
			props.setLoginPosY(event.player.posY);
			props.setLoginPosZ(event.player.posZ);
			// TODO: This is really debug information. How do I send info to console only in debug?
			System.out.println("[AUTHY CRAFT] captured player position, X=" + props.getLoginPosX() + " Y=" + props.getLoginPosY() + " Z=" + props.getLoginPosZ());
			
			// Has the user ever registered with Authy?
			if (props.getAuthyCell() == "") {
				// USER NOT REGISTERED.
				// No evidence the user has ever registered. We need them to run the /atreg command first.
				// TODO: Replace command with client GUI to capture information.
				event.player.addChatComponentMessage(new ChatComponentText("You must register with /atreg before joining server."));
				// Now sit and wait for them to run this command. If they don't after X seconds, we boot them with a message saying they need to register.
			} else if (props.getPlayerAwaitingAuthy() && props.getAuthySuccessDate() == null) {
				// PLAYER HAS REGISTERED BUT NEEDS TO DO 2FA
				// TODO: Replace command with client GUI to capture the 2FA token.
				// Player has registered with Authy AND they are in the time window of a previous Authy 2FA session or they've never done 2FA at all.
				event.player.addChatComponentMessage(new ChatComponentText("To continue login, please authenticate with Authy using /atcode"));
			} else if (props.getAuthySuccessDate() != null) {
				// USER REGISTERED AND HAS ACTIVE 2FA SESSION. CHECK IF SESSION STILL VALID.
				
				// Get time in seconds for both current login time and also the last successful 2FA.
				long long2FASuccessSeconds = (props.getAuthySuccessDate().getTime()*1000); 
				long longCurrentTimeSeconds= (new Date().getTime()*1000);
				
				// Compare the difference in time between the last successful 2FA and the current time. If current is higher than the session limit, wipe session and force 2FA again.
				if ((longCurrentTimeSeconds - long2FASuccessSeconds) >= CommonProxy.AUTHYSESSIONTIMEOUT) {
					// Force user to go through 2FA.
					props.setPlayerAwaitingAuthy(true);
					// Wipe whatever was in the success date because the session just expired.
					props.setAuthySuccessDate(null);
				} else {
					// Let's make sure the user can pass unimpaired.
					props.setPlayerAwaitingAuthy(false);
				}
			}
		}					
		// Send to the console information on the user logging in. 
		System.out.println("[AUTHY CRAFT] " + event.player.getDisplayName() + " email = " + props.getPlayerEmail() + " AuthyCell = " + props.getAuthyCell());		
	}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		// System.out.println("Entity being constructed.");
		/*
		Be sure to check if the entity being constructed is the correct type for the extended properties you're about to add! 
		The null check may not be necessary - I only use it to make sure properties are only registered once per entity
		*/
		if (event.entity instanceof EntityPlayer)
			event.entity.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer((EntityPlayer) event.entity));
	}
	
	
	// we need to add this new event - it is called for every living entity upon death
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		// System.out.println("Something alive died.");
		// we only want to save data for players (most likely, anyway)
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			// NOTE: See step 6 for a way to do this all in one line!!!
			// create a new NBT Tag Compound to store the IExtendedEntityProperties data
			NBTTagCompound playerData = new NBTTagCompound();
			// write the data to the new compound
			((ExtendedPlayer)(event.entity.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME))).saveNBTData(playerData);
			// and store it in our proxy
			proxy.storeEntityData(((EntityPlayer)event.entity).getUniqueID().toString(), playerData);  			
		}
	}

	
	// We already have this event, but we need to modify it some
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{/*
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
		// Before syncing the properties, we must first check if the player has some saved in the proxy
		// recall that 'getEntityData' also removes it from the map, so be sure to store it locally
		// NBTTagCompound playerData = CommonProxy.getEntityData(((EntityPlayer) event.entity).getDisplayName()); // NOTE: WARNING: Another use of username.
		
		NBTTagCompound playerData = proxy.getEntityData(((EntityPlayer)event.entity).getUniqueID().toString());
		
		// make sure the compound isn't null
		if (playerData != null) {
			// Then load the data back into the player's IExtendedEntityProperties
			((ExtendedPlayer)(event.entity.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME))).loadNBTData(playerData);
			ExtendedPlayer props = (ExtendedPlayer) event.entity.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);
			System.out.println("[AUTHY CRAFT] Loaded Authy cell " + props.getAuthyCell());
		}
		// There was some code to sync the data from the server to the client. I don't think I need to do this because once the data is on the server, we don't need it on the client.
		// finally, we sync the data between server and client (we did this earlier in 3.3)
		// ((ExtendedPlayer)(event.entity.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME))).syncExtendedProperties();
		}*/
	}
}
