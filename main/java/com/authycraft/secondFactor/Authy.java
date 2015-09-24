package com.authycraft.secondFactor;

import java.util.HashMap;
import java.util.Map;

import com.authy.AuthyApiClient;
import com.authy.api.*;
import com.authy.api.Error;

public class Authy {
	
	// TODO: Need to get the API key and maybe URL from a config file instead.
	AuthyApiClient client = new AuthyApiClient("ULYctV0ujuNyQK6GbWlVJGZwvn5GuCh6", "https://api.authy.com/");
	// Maybe only invoke these when needed? What's the cost of creation?
	Users users = client.getUsers();
	Tokens tokens = client.getTokens();
	
	/**
	 * Register (aka create) a user in the Authy system.
	 * 
	 * @param strAuthyCell
	 * @param strAuthyCountryCode
	 * @param strAuthyEmail
	 */
	public final String registerAuthyUser(String strAuthyCell, String strAuthyCountryCode, String strAuthyEmail) {
		// createUser takes as arguments the email, phone number and country code
		User user = users.createUser(strAuthyEmail, strAuthyCell, strAuthyCountryCode);
		String strAuthyID = "";
		// Was user created ok?
		if (user.isOk()) {
			// Take userID and store
			strAuthyID = Integer.toString(user.getId());
			System.out.println(strAuthyID);
		} else {
			// Something must have gone wrong.
			Error error = user.getError();
			System.out.println(error.getMessage());
		}
	
		// TODO: Need to examine the return. What information do we get?
		return strAuthyID;
	}
	
	/**
	 * Check if an Authy token is valid for current user.
	 * 
	 * @param strToken
	 * @param strAuthyID
	 */
	public final boolean validateAuthyToken(String strToken, String strAuthyID) {
		// Verify the token.
		Token verification = tokens.verify(Integer.parseInt(strAuthyID), strToken);
		
		if(verification.isOk()) {
			// Valid token
			System.out.println("User " + strToken + " sucessfully validated token " + strToken);
			return true;
		} else {
			// Invalid token
			Error error = verification.getError();
			System.out.println(error.getMessage());
			return false;
		}
		    
	}
	/**
	 * Delete a user that has been registered with Authy. Requires the AuthyID as a string.
	 * 
	 * @param strAuthyID
	 */
	public final void deleteAuthyUser (String strAuthyID) {
		Hash response = users.deleteUser(Integer.parseInt(strAuthyID));
		
		if (response.isOk()) {
			// User successfully deleted
		}
		else {
			// Problem deleting user.
			Error error = response.getError();
			System.out.println(error.getMessage());
		}
	}
	
	/**
	 * Trigger the sending of an Authy code via SMS to a registered user. 
	 * Requires the AuthyID as a string.
	 * 
	 * @param strAuthyID
	 */
	public final void requestAuthySMSToken (String strAuthyID, Boolean bForce) {
		// When the Authy mobile app is being used, by default SMS messages cannot be sent.
		// This can be overridden.
		Map<String, String> options = new HashMap<String, String>();
		
		if (bForce) {
			// Force sending of SMS even when mobile app is in use.
			options.put("force", "true");
		} else {
			options.put("force", "false");
		}
	
		// Fire the SMS request.
		Hash response = users.requestSms(Integer.parseInt(strAuthyID), options);
		
		if (response.isOk()) {
			// User successfully deleted
		}
		else {
			// Problem deleting user.
			Error error = response.getError();
			System.out.println(error.getMessage());
		}
	}
	
}
