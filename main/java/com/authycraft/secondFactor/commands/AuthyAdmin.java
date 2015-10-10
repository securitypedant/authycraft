package com.authycraft.secondFactor.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.authycraft.secondFactor.AuthyAPI;
import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.ExtendedPlayer;
import com.authycraft.secondFactor.SecondFactor;
import com.authycraft.secondFactor.net.AuthyREST;
import com.authycraft.secondFactor.net.AuthyShow2FAGuiPacket;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class AuthyAdmin extends CommandBase {
	
	public static CommonProxy proxy = new CommonProxy();
	
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
		  AuthyREST authyRest = new AuthyREST();
          ExtendedPlayer props = (ExtendedPlayer) player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME); 

		  // Figure out the command.
		  switch (argStrings[1].toLowerCase()) {
		  	case "disable":
		  		if (argStrings[2].toLowerCase() == "true") {
		  			// Disable Authy
		  		} else {
		  			// Enable Authy
		  		}
		  		break;
		  	case "apikey":
		  		// SetAPIKey
		  		break;
		  	case "showgui":
			  	// Show GUI test
				proxy.network.sendTo(new AuthyShow2FAGuiPacket(1), (EntityPlayerMP) player);
				break;
		  	case "push":  		
		  		String authyID, pushRequestID = "";
		  		
	            // If a value for the AuthyID was passed, use it.
	            if (argStrings.length <= 2) {
	            	authyID = props.getAuthyID();
	            } else {
	            	authyID = argStrings[2];
	            }
	            
	            
		  		try {
		  			pushRequestID = authyRest.postNotify(authyID, CommonProxy.AUTHYAPIKEY, "Minecraft player login", player.getDisplayName(), player.getUniqueID().toString(), "nameTest", "versionTest");
		  			System.out.println("Push Request ID=" + pushRequestID);
		  			
		  		} catch (IOException e) {
		  			
		  		}
		  		player.addChatComponentMessage(new ChatComponentText("Push notification sent with UUID=" + pushRequestID));
		  		// Store the UUID on the user's extended properties, so we can examine it when we need to query the status of the request.
		  		props.setpushRequestUUID(pushRequestID);
		  		break;
		  	case "checkpush":
		  		try {
		  			String pushRequestStatus;
		  			pushRequestStatus = authyRest.getNotify(props.getpushRequestUUID(), CommonProxy.AUTHYAPIKEY);
		  			System.out.println("Push Request status=" + pushRequestStatus);
		  			props.setPushRequestStatus(pushRequestStatus);
		  			
		  		} catch (IOException e) {
		  			
		  		}
		  		// TODO: Check a push notification status.
		  		break;
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
