package com.authycraft.secondFactor.door;

import com.authycraft.secondFactor.CommonProxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AuthyDoorBlock extends BlockDoor {
	
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
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        // Do something when the door is clicked on.
        return true;
    }

	
}
