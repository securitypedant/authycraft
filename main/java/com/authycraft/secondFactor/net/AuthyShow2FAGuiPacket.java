package com.authycraft.secondFactor.net;

import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.SecondFactor;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class AuthyShow2FAGuiPacket implements IMessage {	
	// Needs a default constructor.
	public AuthyShow2FAGuiPacket() { }
	
	private int showAuthyGui;
	
	public AuthyShow2FAGuiPacket(int authyGui) {
		this.showAuthyGui = authyGui;
	}

    @Override
    public void fromBytes(ByteBuf buf) {
    	// Read the Authy token from the buffer 
    	showAuthyGui = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
    	// Write the Authy token to the buffer
    	buf.writeInt(showAuthyGui);
    }

	public static class AuthyShow2FAGuiPacketHandler implements IMessageHandler<AuthyShow2FAGuiPacket, IMessage> {
		@Override
		public IMessage onMessage(AuthyShow2FAGuiPacket message, MessageContext ctx) {
			
			// Get the player object and tell them to open the Authy 2FA Gui...
			EntityPlayer player = SecondFactor.proxy.getPlayerFromMessageContext(ctx);
			
			int openGui = message.showAuthyGui;
			if (openGui == 1) {
				player.openGui(SecondFactor.instance, 0, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
			} else {
				// We need to close the gui for the player.
				player.closeScreen();
			}
			return null; // no response in this case
		}   
	}
}
