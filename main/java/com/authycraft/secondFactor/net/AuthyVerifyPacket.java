package com.authycraft.secondFactor.net;

import com.authycraft.secondFactor.AuthyAPI;
import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.ExtendedPlayer;
import com.authycraft.secondFactor.SecondFactor;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class AuthyVerifyPacket implements IMessage {
	
	private static CommonProxy proxy = new CommonProxy();
	
	// Needs a default constructor.
	public AuthyVerifyPacket() { }
	private String authyToken;
	
	public AuthyVerifyPacket(String authyToken) {
		this.authyToken = authyToken;
	}

    @Override
    public void fromBytes(ByteBuf buf) {
    	// Read the Authy token from the buffer 
    	authyToken = ByteBufUtils.readUTF8String(buf); 
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	// Write the Authy token to the buffer
        ByteBufUtils.writeUTF8String(buf, authyToken);
    }

	public static class AuthyVerifyPacketHandler implements IMessageHandler<AuthyVerifyPacket, IMessage> {
		@Override
		public IMessage onMessage(AuthyVerifyPacket message, MessageContext ctx) {
			// Get the player we want to auth the code for. 
			EntityPlayer player = SecondFactor.proxy.getPlayerFromMessageContext(ctx);
			// We need the player's extended properties so we can get their Authy ID
			ExtendedPlayer props = (ExtendedPlayer) player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);
			// Check to make sure they have registered, just in case this packet comes from an unregistered user.
			if (!props.getAuthyCell().equals("")) {
				// Player has registered, so they must have an AuthyID.
				String strAuthyID = props.getAuthyID();
				
				AuthyAPI authyAPI = new AuthyAPI();
				boolean tokenSuccess = authyAPI.validateAuthyToken(message.authyToken, strAuthyID);
				
				if (tokenSuccess) {
					// Code was correct, tell client to close Gui.
					System.out.println("Was able to verify Authy token from Gui for " + player.getDisplayName());
					// Close the GUI and let them in...
					props.setPlayerAwaitingAuthy(false);
					props.setPushRequestStatus("");
					props.setpushRequestUUID("");
					proxy.network.sendTo(new AuthyShow2FAGuiPacket(0), (EntityPlayerMP) player);
				}
				
			} else {
				System.out.println("Player " + player.getDisplayName() + " is not registered with Authy.");
			}
			       
			
			// We are getting the code from the Authy client.
			System.out.println("Got the Authy token " + message.authyToken);
			
			return null; // no response in this case
		}   
	}
}
