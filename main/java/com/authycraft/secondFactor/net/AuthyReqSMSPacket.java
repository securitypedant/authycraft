package com.authycraft.secondFactor.net;

import com.authycraft.secondFactor.AuthyAPI;
import com.authycraft.secondFactor.ExtendedPlayer;
import com.authycraft.secondFactor.SecondFactor;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class AuthyReqSMSPacket implements IMessage
{
	// Needs a default constructor.
	public AuthyReqSMSPacket() { }

    @Override
    public void fromBytes(ByteBuf buf) {
    	// Read the Authy token from the buffer 
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	// Write the Authy token to the buffer
    }

	public static class AuthyReqSMSPacketHandler implements IMessageHandler<AuthyReqSMSPacket, IMessage> {
		@Override
		public IMessage onMessage(AuthyReqSMSPacket message, MessageContext ctx) {
			// Get the player we want to auth the code for. 
			EntityPlayer player = SecondFactor.proxy.getPlayerFromMessageContext(ctx);
			// We need the player's extended properties so we can get their Authy ID
			ExtendedPlayer props = (ExtendedPlayer) player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);
			// Check to make sure they have registered, just in case this packet comes from an unregistered user.
			if (!props.getAuthyCell().equals("")) {
				 String strAuthyID = props.getAuthyID();		
				 // If they are registered, we should have a valid AuthyID stored.
		          
	            // TODO: Ideally put a try around this to catch any problems with communicating to Authy.
				AuthyAPI authyAPI = new AuthyAPI();
				authyAPI.requestAuthyVoiceToken(strAuthyID, true);			
			}
				
			return null; // no response in this case
		}   
	}
}
