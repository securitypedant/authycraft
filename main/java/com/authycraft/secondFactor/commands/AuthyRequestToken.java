package com.authycraft.secondFactor.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.authycraft.secondFactor.AuthyAPI;
import com.authycraft.secondFactor.ExtendedPlayer;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class AuthyRequestToken extends CommandBase {
	
	private List aliases;
	private IChatComponent chatcomp;
	
	public AuthyRequestToken ()
	  {
	    this.aliases = new ArrayList();
	    this.aliases.add("atreqt");
	    this.aliases.add("authyrequesttoken");
	  }

	  @Override
	  public String getCommandName()
	  {
	    return "Authy token request";
	  }

	  @Override
	  public String getCommandUsage(ICommandSender icommandsender)
	  {
	    return "atreqt 123456";
	  }

	  @Override
	  public List getCommandAliases()
	  {
	    return this.aliases;
	  }

	  @Override
	  public void processCommand(ICommandSender icommandsender, String[] argStrings)
	  {
		  // Get the player entity so we can communicate back.
		  EntityPlayer player = getCommandSenderAsPlayer(icommandsender);
		  // Check if the right number of arguments was sent.
			if (argStrings.length > 0 && argStrings.length < 2) {
				// Check to see if user is already registered by looking at their extended properties.
	            ExtendedPlayer props = (ExtendedPlayer) player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);
	            if (!props.getAuthyCell().equals("")) {
		            String strAuthyID = props.getAuthyID();		// If they are registered, we should have a valid AuthyID stored.
		          
		            // Validate token with Authy.
		            // TODO: Ideally put a try around this to catch any problems with communicating to Authy.
					AuthyAPI authyAPI = new AuthyAPI();
					boolean tokenSuccess = authyAPI.validateAuthyToken(argStrings[0], strAuthyID);
	
					if (tokenSuccess) {
						player.addChatComponentMessage(new ChatComponentText("Authy authentication success!"));
						props.setPlayerAwaitingAuthy(false);
						// Set the time/date the user successfully passed the 2FA. This is used to determine 2FA session period.
						props.setAuthySuccessDate(new Date());
						System.out.println("[AUTHY CRAFT] Player " + player.getDisplayName() + " passsed 2FA on " + props.getAuthySuccessDate());
						
					} else {
						player.addChatComponentMessage(new ChatComponentText("Authy authentication failed :("));
						props.setPlayerAwaitingAuthy(true);
						// Wipe any value for the 2FA last success date now that they failed.
						props.setAuthySuccessDate(null);
					}
	            } else {
	            	// No Authy cell number on user extended props, therefore they are not registered.
	            	player.addChatComponentMessage(new ChatComponentText("You have not registered with Authy, please run /atreg"));	
	            }
			} else {
				// To few or too many arguments passed.
				System.out.println("[AUTHY CRAFT] Incorrect arguments sent for command /atreg");
				player.addChatComponentMessage(new ChatComponentText("Incorrect use of command, /atcode <authy token>"));
			}
	  }

	  @Override
	  public boolean canCommandSenderUseCommand(ICommandSender icommandsender)
	  {
	    return true;
	  }

	  @Override
	  public List addTabCompletionOptions(ICommandSender icommandsender,
	      String[] astring)
	  {
	    return null;
	  }

	  @Override
	  public boolean isUsernameIndex(String[] astring, int i)
	  {
	    return false;
	  }

	  @Override
	  public int compareTo(Object o)
	  {
	    return 0;
	  }
}
