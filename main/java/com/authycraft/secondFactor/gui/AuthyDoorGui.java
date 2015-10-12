package com.authycraft.secondFactor.gui;

import java.io.IOException;

import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.ExtendedPlayer;
import com.authycraft.secondFactor.SecondFactor;
import com.authycraft.secondFactor.net.AuthyReqPushPacket;
import com.authycraft.secondFactor.net.AuthyReqTokenPacket;
import com.authycraft.secondFactor.net.AuthyShow2FAGuiPacket;
import com.authycraft.secondFactor.net.AuthyVerifyPacket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class AuthyDoorGui extends GuiScreen {
	
	private CommonProxy proxy = new CommonProxy();
	private GuiButton checkPush;
	private int posX, posY, textStart;
	public String strDialogMessage1, strDialogMessage2;
	
    @Override
    public void updateScreen() {
        super.updateScreen();
    }
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
		
	    this.mc.getTextureManager().bindTexture(new ResourceLocation("authycraft", "textures/gui/authyDoorGui.png"));
	    this.drawTexturedModalRect(posX, posY, 0, 0, 120, 88);
	
	    super.drawScreen(mouseX, mouseY, partialTicks);

	    // Write out the dialog text.
	    this.drawCenteredString(this.fontRendererObj, strDialogMessage1, this.width / 2, this.textStart, 0xFFFFFFFF);
	    this.drawCenteredString(this.fontRendererObj, strDialogMessage2, this.width / 2, this.textStart + 15, 0xFFFFFFFF);
	}
	
	@Override
	protected void actionPerformed (GuiButton button) {
		// TODO: Dialog message isn't actually tied to API response. It should be.
		if (button == this.checkPush) {
			ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer) this.mc.thePlayer);
			
			System.out.println("Status of player push request: " + props.getPushRequestStatus());
			// If player has just passed an Authy push notification.
			if (props.getPushRequestStatus().toLowerCase().equals("approved")) {
				// Close the GUI and let them in...
				// FIXME: We also need to add in here the storage of the date/time so the 2FA session is setup.
				props.setPlayerAwaitingAuthy(false);
				props.setPushRequestStatus("");
				props.setpushRequestUUID("");
				
				 this.mc.displayGuiScreen(null);
					if (this.mc.currentScreen == null)
		                this.mc.setIngameFocus();
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
	    return true;
	}
	
	@Override
	public void initGui() {
		// Setup vars for the overall positioning of the main Authy bitmap.
		this.posX = (this.width - 120) / 2;
		this.posY = (this.height - 88) / 2;
		this.textStart = ((this.height - 88) / 2) + 50;
		this.strDialogMessage1 = "Authy secured door";
		this.strDialogMessage2 = "Owner notified";
		
		
	}
	
}
