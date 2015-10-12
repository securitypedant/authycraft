package com.authycraft.secondFactor.door;

import java.io.IOException;

import com.authycraft.secondFactor.CommonProxy;
import com.authycraft.secondFactor.ExtendedPlayer;
import com.authycraft.secondFactor.SecondFactor;
import com.authycraft.secondFactor.net.AuthyREST;
import com.authycraft.secondFactor.net.AuthyReqPushPacket;
import com.authycraft.secondFactor.net.AuthyShow2FAGuiPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AuthyDoorBlock extends BlockDoor {
	CommonProxy proxy = new CommonProxy();
	public Item placerItem;
	@SideOnly(Side.CLIENT)
    private IIcon[] icon_upper;
    @SideOnly(Side.CLIENT)
    private IIcon[] icon_lower;

	public AuthyDoorBlock() {
		super(Material.iron);
	}
	
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
	  @SideOnly(Side.CLIENT)
	  @Override
	  public void registerBlockIcons(IIconRegister iconRegister) {
	    icon_upper = new IIcon[2];
	    icon_lower = new IIcon[2];
	    icon_upper[0] = iconRegister.registerIcon("authycraft:door_authy_upper");
	    icon_lower[0] = iconRegister.registerIcon("authycraft:door_authy_lower");
	    icon_upper[1] = new IconFlipped(icon_upper[0], true, false);
	    icon_lower[1] = new IconFlipped(icon_lower[0], true, false);
	  }
	/* 
	public int idPicked(World par1World, int par2, int par3, int par4) {
		return AuthyDoorItem.itemID;
	}

	public int idDropped(int par1, Random par2Random, int par3) {
		return (par1 & 8) != 0 ? 0 : (AdobeBlock.AdobeDoorItem.itemID);
	}	 
	*/
	  @SideOnly(Side.CLIENT)
	  @Override
	  public IIcon getIcon(IBlockAccess access, int x, int y, int z, int direction) {
	    if (direction != 1 && direction != 0) {
	      int i1 = this.func_150012_g(access, x, y, z);
	      int j1 = i1 & 3;
	      boolean flag = (i1 & 4) != 0;
	      boolean flag1 = false;
	      boolean flag2 = (i1 & 8) != 0;

	      if (flag) {
	        if (j1 == 0 && direction == 2) {
	          flag1 = !flag1;
	        } else if (j1 == 1 && direction == 5) {
	          flag1 = !flag1;
	        } else if (j1 == 2 && direction == 3) {
	          flag1 = !flag1;
	        } else if (j1 == 3 && direction == 4) {
	          flag1 = !flag1;
	        }
	      } else {
	        if (j1 == 0 && direction == 5) {
	          flag1 = !flag1;
	        } else if (j1 == 1 && direction == 3) {
	          flag1 = !flag1;
	        } else if (j1 == 2 && direction == 4) {
	          flag1 = !flag1;
	        } else if (j1 == 3 && direction == 2) {
	          flag1 = !flag1;
	        }

	        if ((i1 & 16) != 0) {
	          flag1 = !flag1;
	        }
	      }

	      return flag2 ? this.icon_upper[flag1?1:0] : this.icon_lower[flag1?1:0];
	    } else {
	      return this.icon_lower[0];
	    }
	  }
	
	 /**
     * Called upon block activation (right click on the block.)
     * 
     * (World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
     */
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ)
    {
        // Do something when the door is clicked on.
    	System.out.println("Ooh someone clicked on an Authy secured door!");
    	// Send the push notification.
    	// FIXME: In future we need to figure out who the door is owned by so we can send the right notification.
    	// FIXME: Need to send in an array of strings to the REST object, so we can send any data easily from here.
		String strPlayerName = player.getDisplayName();
		String strMinecraftVersion = "1.7.10";
		String strServerName = "A Minecraft Server";
		String strPlayerUUID = player.getUniqueID().toString();
		String strMessage = "Player attempting to open secured door:";
		
		// We need the player's extended properties so we can get their Authy ID
		ExtendedPlayer props = (ExtendedPlayer) player.getExtendedProperties(ExtendedPlayer.EXT_PROP_NAME);
		// Check to make sure they have registered, just in case this packet comes from an unregistered user.
		// FIXME: Need to also ensure that before a player PUTS an Authy door down, they've already registered.
		if (!props.getAuthyCell().equals("")) {
			// If they are registered, we should have a valid AuthyID stored.
	         String strAuthyID = props.getAuthyID();		
			  
            // TODO: Ideally put a try around this to catch any problems with communicating to Authy.
			//AuthyAPI authyAPI = new AuthyAPI();
			//String authyPushUUID = authyAPI.requestAuthyPushNotification(strAuthyID, strMessage, strPlayerName, strServerName, strPlayerUUID);
	        AuthyREST authyRest = new AuthyREST();
	     	try {
	     		String pushRequestID = authyRest.postNotify(strAuthyID, CommonProxy.AUTHYAPIKEY, strMessage, strPlayerName, strPlayerUUID, strServerName, strMinecraftVersion);
	     		System.out.println("Push Request ID=" + pushRequestID);
	  			props.setpushRequestUUID(pushRequestID);
	  		} catch (IOException e) {
	  			
	  		}
		}
		
    	// Show Authy secured door gui. This is only displayed while we wait for response from push notification.
    	player.openGui(SecondFactor.instance, 1, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
    	
    	// Handle door open and close.
        int i1 = this.func_150012_g(worldIn, x, y, z);
        int j1 = i1 & 7;
        j1 ^= 4;
    	
    	// Store co-ordinates of the player, so we can open/close the door later player tick.
		int[] location = { x, y, z, i1 ,j1 };
		props.setAuthySecuredDoorLocation(location);
    	
    	return true;
    }

	
}
