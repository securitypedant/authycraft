package com.authycraft.secondFactor;

import java.time.Instant;
import java.util.Date;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	public final static String EXT_PROP_NAME = "ExtendedTwilioPlayer";
	private final EntityPlayer player;
	
	// These variables are what we will be adding to the player data profile.
	private String authyCell, authyCountryCode, playerEmail, authyID;
	private Date authySuccessDate;
	private Boolean playerAwaitingAuthy;
	private Double loginPosX, loginPosY, loginPosZ;
	
	/*
	The default constructor takes no arguments, but I put in the Entity so I can initialize the above variable 'player'
	Also, it's best to initialize any other variables you may have added, just like in any constructor.
	*/
	public ExtendedPlayer(EntityPlayer player)
	{
		// TODO: Do I need to worry that this constructor might be overwriting stored values?
		this.player = player;
		this.authyCell = "";
		this.playerEmail = "";
		this.authyID = "";						
		this.authySuccessDate = null;			// By default user has never successfully completed 2FA phase.
		this.playerAwaitingAuthy = false;		// By default player is not enforced to perform 2FA.
		this.authyCountryCode = "1"; 			// Default to US for the country of the phone number.
	}
	
	//Date now = new Date().;
	//Instant iNow = new Instant().;
	
	/**
	* Used to register these extended properties for the player during EntityConstructing event
	* This method is for convenience only; it will make your code look nicer
	*/
	public static final void register(EntityPlayer player)
	{
		player.registerExtendedProperties(ExtendedPlayer.EXT_PROP_NAME, new ExtendedPlayer(player));
	}
	
	/**
	* Returns ExtendedPlayer properties for player
	* This method is for convenience only; it will make your code look nicer
	*/
	public static final ExtendedPlayer get(EntityPlayer player)
	{
		return (ExtendedPlayer) player.getExtendedProperties(EXT_PROP_NAME);
	}
	
	// Save Authy user data on the Minecraft user
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		// We need to create a new tag compound that will save everything for our Extended Properties
		NBTTagCompound properties = new NBTTagCompound();

		// Store the Authy cell number and player email
		properties.setString("AuthyCell", this.authyCell);
		properties.setString("PlayerEmail", this.playerEmail);
		properties.setString("AuthyCountryCode", this.authyCountryCode);
		properties.setString("AuthyID", this.authyID);
		// Store the date as milliseconds since Jan 1st 1970 because the properties class cannot store dates.
		if (this.authySuccessDate == null) {
			properties.setLong("AuthyLast2FASuccess", 0);
		} else {
			properties.setLong("AuthyLast2FASuccess", this.authySuccessDate.getTime());
		}		
		compound.setTag(EXT_PROP_NAME, properties);
	}
	
	// Load Authy user data on the Minecraft user
	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		// Here we fetch the unique tag compound we set for this class of Extended Properties
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		// Get our data from the custom tag compound
		this.authyCell = properties.getString("AuthyCell");
		this.playerEmail = properties.getString("PlayerEmail");
		this.authyCountryCode = properties.getString("AuthyCountryCode");
		this.authyID = properties.getString("AuthyID");
		// Get the data stored as a long and initialize new date.
		if (properties.getLong("AuthyLast2FASuccess") == 0) {
			this.authySuccessDate = null;
		} else {
			this.authySuccessDate = new Date(properties.getLong("AuthyLast2FASuccess"));
		}
		
		// Dump to console the information for a loaded user.
		System.out.println("[AUTHY CRAFT] Loaded Authy data, cell: " + this.authyCell + " CC:" + this.authyCountryCode + " email:" + this.playerEmail + " authyID:" + this.authyID);
	}

	@Override
		public void init(Entity entity, World world) {
	}
	
	public String getAuthyCell() {
		// Get the Authy Cell number
		return this.authyCell;
	}
	
	public String getAuthyCountryCode () {
		// Get the Authy country code
		return this.authyCountryCode;
	}
	
	public String getPlayerEmail () {
		// Get player email address
		return this.playerEmail;
	}
	
	public String getAuthyID () {
		// Get player AuthyID address
		return this.authyID;
	}
	
	public Date getAuthySuccessDate () {
		// Get the date of the last successful Authy auth.
		return this.authySuccessDate;
	}
	
	public Boolean getPlayerAwaitingAuthy () {
		// Return if the player should be unable to move and be static and awaiting some response from Authy.
		return this.playerAwaitingAuthy;
	}
	
	public Double getLoginPosX() {
		// Return the X position at time of login.
		return this.loginPosX;
	}

	public Double getLoginPosY() {
		// Return the X position at time of login.
		return this.loginPosY;
	}
	
	public Double getLoginPosZ() {
		// Return the X position at time of login.
		return this.loginPosZ;
	}
	
	public void setAuthyCell (String cell){
		// Set the Authy cell number.
		this.authyCell = cell;
	}
	
	public void setAuthyCountryCode (String countrycode) {
		// Set the Authy country code.
		this.authyCountryCode = countrycode;
	}
	
	public void setPlayerEmail (String email) {
		// Set the players email.
		this.playerEmail = email;
	}
	
	public void setAuthyID (String authyID) {
		// Set the players email.
		this.authyID = authyID;
	}
	
	/**
	 *  Set the date when the last successful 2FA activity occurred.
	 * @param date
	 */
	public void setAuthySuccessDate (Date date) {
		// Set the players last successful Authy auth date.
		this.authySuccessDate = date;
	}
	
	public void setPlayerAwaitingAuthy(Boolean playerAwaitingAuthy) {
		// Set if the player is in the process of waiting for an Authy registration AND token validation.
		this.playerAwaitingAuthy = playerAwaitingAuthy;
	}
	
	public void setLoginPosX (Double positionX) {
		// Set the players initial login X position
		this.loginPosX = positionX;
	}
	
	public void setLoginPosY (Double positionY) {
		// Set the players initial login Y position
		this.loginPosY = positionY;
	}

	public void setLoginPosZ (Double positionZ) {
		// Set the players initial login Z position
		this.loginPosZ = positionZ;
	}
}
