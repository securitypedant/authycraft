package com.authycraft.secondFactor.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.authycraft.secondFactor.Authy;
import com.authycraft.secondFactor.ExtendedPlayer;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class AuthyAdmin extends CommandBase {
	
	private List aliases;
	private IChatComponent chatcomp;
	
	public AuthyAdmin ()
	  {
	    this.aliases = new ArrayList();
	    this.aliases.add("atadmin");
	    this.aliases.add("authyadmin");
	  }

	  @Override
	  public String getCommandName()
	  {
	    return "Authy Admin, edit Authy 2FA settings.";
	  }

	  @Override
	  public String getCommandUsage(ICommandSender icommandsender)
	  {
	    return "atadmin set <setting> <value>";
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
		  
		  // Disable Authy
		  
		  // Enable Authy
		  
		  // SetAPIKey
		  
		  

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
