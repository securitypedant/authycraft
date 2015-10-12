package com.authycraft.secondFactor.gui;

import java.io.IOException;

import com.authycraft.secondFactor.CommonProxy;
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

public class AuthyGui extends GuiScreen {
	
	private CommonProxy proxy = new CommonProxy();
	
	private GuiButton sendSMS, sendPush, sendVoice, authyVerify;
	private GuiTextField authyTokenTxtField; 
	private int posX, posY, textStart;
	public String strDialogMessage;
	
    @Override
    public void updateScreen() {
        super.updateScreen();
        this.authyTokenTxtField.updateCursorCounter();
    }
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
		
	    this.mc.getTextureManager().bindTexture(new ResourceLocation("authycraft", "textures/gui/authyGui.png"));
	    this.drawTexturedModalRect(posX, posY, 0, 0, 176, 150);
		
		// Draw the text box.
		this.authyTokenTxtField.drawTextBox();
		
	    super.drawScreen(mouseX, mouseY, partialTicks);

	    // Draw buttons and the text field
	    this.drawCenteredString(this.fontRendererObj, strDialogMessage, this.width / 2, this.textStart, 0xFFFFFFFF);
	   
	}
	
	protected void keyTyped(char typedChar, int keyCode)
    {
		// FIXME: Ideally need to prevent the ESCAPE key from bring up the GUI. OR we can maybe have the PlayerTick force open a Gui screen if you close it.
        //System.out.println("typedChar=" + typedChar + " keyCode=" + keyCode);
		// TODO: Consider removing the super. so that Escape doesn't work.
		// TODO: Catch the ENTER key and it call the verify.
		super.keyTyped(typedChar, keyCode);
        this.authyTokenTxtField.textboxKeyTyped(typedChar, keyCode);
    }
	
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        this.authyTokenTxtField.mouseClicked(x, y, btn);
    }
	
	@Override
	protected void actionPerformed (GuiButton button) {
		// TODO: Dialog message isn't actually tied to API response. It should be.
		if (button == this.sendSMS) {
			proxy.network.sendToServer(new AuthyReqTokenPacket("sms"));
			this.strDialogMessage = "SMS sent. Please enter code.";
		}
		if (button == this.sendPush){
			proxy.network.sendToServer(new AuthyReqPushPacket());
			this.strDialogMessage = "Push notification sent.";
		}
		if (button == this.sendVoice){
			proxy.network.sendToServer(new AuthyReqTokenPacket("voice"));
			this.strDialogMessage = "Answer phone and enter code.";
		}
		if (button == this.authyVerify){
			// TODO: Should check to ensure the verify text is all numerical.
			if (this.authyTokenTxtField.getText().length() == 7) {
				proxy.network.sendToServer(new AuthyVerifyPacket(this.authyTokenTxtField.getText()));	
			} else {
				// Verify code isn't long enough.
				this.strDialogMessage = "Invalid code, please try again.";

			}
			/*
			 * Saved for future reference. This is how to close the screen from the Gui.
			 this.mc.displayGuiScreen(null);
			if (this.mc.currentScreen == null)
                this.mc.setIngameFocus();
			 */
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
	    return true;
	}
	
	@Override
	public void initGui() {
		// Setup vars for the overall positioning of the main Authy bitmap.
		this.posX = (this.width - 176) / 2;
		this.posY = (this.height - 150) / 2;
		this.textStart = ((this.height - 150) / 2) + 50;
		this.strDialogMessage = "Complete second factor.";

		 // Setup the buttons for the GUI.
		this.buttonList.add(this.sendSMS = new GuiButton(0, posX + 9, posY + 99, 48, 20, "SMS"));
		this.buttonList.add(this.sendVoice = new GuiButton(3, posX + 65, posY + 99, 48, 20, "Voice"));
		this.buttonList.add(this.sendPush = new GuiButton(1, posX + 120, posY + 99, 48, 20, "Push"));
		this.buttonList.add(this.authyVerify = new GuiButton(2, posX + 80, posY + 72, 88, 20, "Verify"));
		// Setup the text field where we get back the code.
		this.authyTokenTxtField = new GuiTextField(this.fontRendererObj, posX + 9, posY + 72, 60, 20);
		this.authyTokenTxtField.setMaxStringLength(7);
		this.authyTokenTxtField.setText("");
		this.authyTokenTxtField.setFocused(true); 
	}
	
}
