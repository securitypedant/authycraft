package com.authycraft.secondFactor.gui;

import java.io.IOException;

import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.SecondFactor;
import com.authycraft.secondFactor.net.AuthyReqPushPacket;
import com.authycraft.secondFactor.net.AuthyReqSMSPacket;
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
	
	public static CommonProxy proxy = new CommonProxy();
	
	private GuiButton sendSMS;
	private GuiButton sendPush;
	private GuiButton authyVerify;
	private GuiTextField authyTokenTxtField; 
	private int posX, posY, textStart;

	
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
	    this.drawCenteredString(this.fontRendererObj, "Please complete second factor...", this.width / 2, this.textStart, 0xFFFFFFFF);
	   
	}
	
	protected void keyTyped(char par1, int par2)
    {
        super.keyTyped(par1, par2);
        this.authyTokenTxtField.textboxKeyTyped(par1, par2);
    }
	
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        this.authyTokenTxtField.mouseClicked(x, y, btn);
    }
	
	@Override
	protected void actionPerformed (GuiButton button) {
		if (button == this.sendSMS) {
			proxy.network.sendToServer(new AuthyReqSMSPacket());
		}
		if (button == this.sendPush){
			proxy.network.sendToServer(new AuthyReqPushPacket());
		}
		if (button == this.authyVerify){
			proxy.network.sendToServer(new AuthyVerifyPacket(this.authyTokenTxtField.getText()));
			/*
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

		 // Setup the buttons for the GUI.
		this.buttonList.add(this.sendSMS = new GuiButton(0, posX + 20, posY + 100, 65, 20, "Send SMS"));
		this.buttonList.add(this.sendPush = new GuiButton(1, posX + 90, posY + 100, 65, 20, "Send Push"));
		this.buttonList.add(this.authyVerify = new GuiButton(2, posX + 80, posY + 70, 75, 20, "Verify"));
		// Setup the text field where we get back the code.
		this.authyTokenTxtField = new GuiTextField(this.fontRendererObj, posX + 20, posY + 70, 50, 20);
		this.authyTokenTxtField.setMaxStringLength(7);
		this.authyTokenTxtField.setText("");
		this.authyTokenTxtField.setFocused(true); 
	}
	
}
