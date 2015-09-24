package com.authycraft.secondFactor.commands;

import java.util.ArrayList;
import java.util.List;

import com.authycraft.secondFactor.Authy;
import com.authycraft.secondFactor.ExtendedPlayer;

import net.minecraft.command.ICommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class AuthyRegisterUser extends CommandBase {
	private List aliases;
	private IChatComponent chatcomp;
	
	public AuthyRegisterUser() {
	    this.aliases = new ArrayList();
	    this.aliases.add("atreg");
	    this.aliases.add("authyregisteruser");		
	}
	
	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "Register an Authy user";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "atreg <email> <cellnumber> <countrycode>";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	/**
	 * Process the Authy register use command. This should be in the form, 
	 *   atreg <email> <cellnumber> <countrycode>
	 *   At the end of the process, user is reminded they need to send the Authy code to login.
	 */
	@Override
	public void processCommand(ICommandSender icommandsender, String[] argStrings) {	
	    // Get the player entity so we can communicate back.
		EntityPlayer player = getCommandSenderAsPlayer(icommandsender);
		
		// Check if the right number of arguments was sent.
		if (argStrings.length > 0 && argStrings.length < 4) {
			// Check to see if user is already registered by looking at their extended properties.
            ExtendedPlayer props = (ExtendedPlayer) player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);

            props.setPlayerEmail(argStrings[0]);
            props.setAuthyCell(argStrings[1]);
            props.setAuthyCountryCode(argStrings[2]);

            // Register the user with Authy.
			Authy authyAPI = new Authy();
			String authyID = authyAPI.registerAuthyUser(argStrings[1], argStrings[2], argStrings[0]);
			
			// Store the AuthyID on the user.
			props.setAuthyID(authyID);
			
			// Communicate to the user they now need to pass in the Authy code to continue login.
			player.addChatComponentMessage(new ChatComponentText("You are now registered! To continue login you must /atcode"));
			
		} else {
			// To few or too many arguments passed.
			System.out.println("[AUTHY CRAFT] Incorrect arguments sent for command /atreg");
			player.addChatComponentMessage(new ChatComponentText("Incorrect use of command, /atreg <email> <cellnumber> <countrycode>"));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return false;
	}

}
