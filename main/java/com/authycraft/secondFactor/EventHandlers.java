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
		// TODO: How often do we check the tick? every player tick?
		if(event.phase == Phase.START) { 
			// Only execute on the start of the phase.
			// Get extended properties for all players. It's is from here we know if a user has registered yet with Authy.
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.player);
			
			// If the player has no Authy data, continue to keep them in place.
			// TODO: If too much time passes, disconnect the user.
			if (props.getAuthyCell() == "") {
				props.setPlayerAwaitingAuthy(true);
			}
			
			if ((event.player.ticksExisted % 200) == 0 && props.getPlayerAwaitingAuthy()) {
				// If the player needs to perform the second factor.
				if (props.getAuthyCell() != "") {
					// Communicate to the user they need to run the command to tell us the Authy code.
					event.player.addChatComponentMessage(new ChatComponentText("You need to pass Authy second factor, please run /atcode"));
				}
			}
			
			// If the player should be standing still, we get this from the extended props. 
			// Make sure they are still awaiting authy response and also check if we've actually stored a position yet. The tick can fire before the login event.
			if (props.getPlayerAwaitingAuthy() && props.getLoginPosX() != null) {
				// Keep them in place and maybe display something on the screen. Sleeping GUI?
				Double xLoc = props.getLoginPosX();
				Double yLoc = props.getLoginPosY();
				Double zLoc = props.getLoginPosZ();
				
				// Keep the player in the same place when they logged in, until they pass the Authy factor.
				// event.player.setPosition(xLoc, yLoc, zLoc);
				event.player.setPositionAndUpdate(xLoc, yLoc, zLoc);
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
		// TODO: Would be nice to add a session property to the user so we can allow configuration of how often the 2FA needs to happen.
		// TODO: Add logic to determine what class of players need 2FA. Default to only OPS. Do we also support use of ForgeEssentials permissions? Need to abstract to config file.
		
		// Get extended properties for all players. It's is from here we know if a user has registered yet with Authy.
		ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) event.player);
		// Stop the player from going anywhere.
		props.setPlayerAwaitingAuthy(true);
		// Grab the current logged in position and store it, so later in the player tick event we can use it to keep player in place.
		props.setLoginPosX(event.player.posX);
		props.setLoginPosY(event.player.posY);
		props.setLoginPosZ(event.player.posZ);
		System.out.println("[AUTHY CRAFT] captured player position, X=" + props.getLoginPosX() + " Y=" + props.getLoginPosY() + " Z=" + props.getLoginPosZ());
		
		// Send to the console information on the user logging in. 
		System.out.println("[AUTHY CRAFT] " + event.player.getDisplayName() + " email = " + props.getPlayerEmail() + " AuthyCell = " + props.getAuthyCell());
		
		// Has the user ever registered with Authy for this app?
		if (props.getAuthyCell() == "") {
			// No evidence the user has ever registered. We need them to run the /atreg command first.
			event.player.addChatComponentMessage(new ChatComponentText("You must register with /atreg before joining server."));
			// Now sit and wait for them to run this command. If they don't after X seconds, we boot them with a message saying they need to register.
		} else {
			event.player.addChatComponentMessage(new ChatComponentText("To continue login, please authenticate with Authy using /atcode"));
		}
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
