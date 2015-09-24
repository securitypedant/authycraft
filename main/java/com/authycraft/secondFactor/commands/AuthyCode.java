package com.authycraft.secondFactor.commands;

import java.util.ArrayList;
import java.util.List;

import com.authycraft.secondFactor.Authy;
import com.authycraft.secondFactor.ExtendedPlayer;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class AuthyCode extends CommandBase {
	
	private List aliases;
	private IChatComponent chatcomp;
	
	public AuthyCode ()
	  {
	    this.aliases = new ArrayList();
	    this.aliases.add("atcode");
	    this.aliases.add("authycode");
	  }

	  @Override
	  public String getCommandName()
	  {
	    return "Authy SMS code response";
	  }

	  @Override
	  public String getCommandUsage(ICommandSender icommandsender)
	  {
	    return "atcode 123456";
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
	            String strAuthyID = props.getAuthyID();
	            
	            // Register the user with Authy.
				Authy authyAPI = new Authy();
				boolean tokenSuccess = authyAPI.validateAuthyToken(argStrings[0], strAuthyID);
				
				if (tokenSuccess) {
					player.addChatComponentMessage(new ChatComponentText("Authy authentication success!"));
					props.setPlayerAwaitingAuthy(false);
					
				} else {
					player.addChatComponentMessage(new ChatComponentText("Authy authentication failed :("));
					props.setPlayerAwaitingAuthy(true);
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
