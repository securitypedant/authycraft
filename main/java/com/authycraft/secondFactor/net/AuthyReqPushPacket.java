package com.authycraft.secondFactor.net;

import java.io.IOException;

import com.authycraft.secondFactor.AuthyAPI;
import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.ExtendedPlayer;
import com.authycraft.secondFactor.SecondFactor;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.FMLCommonHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class AuthyReqPushPacket implements IMessage {
	// Needs a default constructor.
	public AuthyReqPushPacket() { }
		
    @Override
    public void fromBytes(ByteBuf buf) {
    	// No need to read from the buffer
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	// No need to write to the buffer
    }

	public static class AuthyReqPushPacketHandler implements IMessageHandler<AuthyReqPushPacket, IMessage> {
		@Override
		public IMessage onMessage(AuthyReqPushPacket message, MessageContext ctx) {			
			// Get the player we want to request a push notification for. 
			EntityPlayer player = SecondFactor.proxy.getPlayerFromMessageContext(ctx);
			String strPlayerName = player.getDisplayName();
			String strMinecraftVersion = MinecraftServer.getServer().getMinecraftVersion();
			String strServerName = MinecraftServer.getServer().getMotd();
			String strPlayerUUID = player.getUniqueID().toString();
			String strMessage = "Player attempting to login to server:";
			
			// We need the player's extended properties so we can get their Authy ID
			ExtendedPlayer props = (ExtendedPlayer) player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);
			// Check to make sure they have registered, just in case this packet comes from an unregistered user.
			if (!props.getAuthyCell().equals("")) {
				// If they are registered, we should have a valid AuthyID stored.
		         String strAuthyID = props.getAuthyID();		
				  
	            // TODO: Ideally put a try around this to catch any problems with communicating to Authy.
				//AuthyAPI authyAPI = new AuthyAPI();
				//String authyPushUUID = authyAPI.requestAuthyPushNotification(strAuthyID, strMessage, strPlayerName, strServerName, strPlayerUUID);
		        AuthyREST authyRest = new AuthyREST();
		     	try {
		     		String pushRequestID = authyRest.postNotify(strAuthyID, CommonProxy.AUTHYAPIKEY, strMessage, strPlayerName, strPlayerUUID, strServerName, strMinecraftVersion);
		     		System.out.println("Push Request ID=" + pushRequestID);
		  			props.setpushRequestUUID(pushRequestID);
		  		} catch (IOException e) {
		  			
		  		}
		     	 
			}
			return null; // no response in this case
		}   
	}
}
