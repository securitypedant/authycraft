package com.authycraft.secondFactor;

import java.util.HashMap;
import java.util.Map;

import com.authy.AuthyApiClient;
import com.authy.api.*;
import com.authy.api.Error;

public class AuthyAPI {
	// TODO: Need better error handling and ensure we respect configAuthyFailSecure.
	// Get Authy connection information from config file.
	// String authyAPIKEY = configAuthyAPIKey;
	
	AuthyApiClient client = new AuthyApiClient(CommonProxy.AUTHYAPIKEY, CommonProxy.AUTHYAPIURL);
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
			// Something must have gone wrong. Need better error handling here.
			Error error = user.getError();
			System.out.println(error.getMessage());
		}
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
	public final boolean requestAuthySMSToken (String strAuthyID, Boolean bForce) {
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
			return true;
		}
		else {
			// Problem deleting user.
			Error error = response.getError();
			System.out.println(error.getMessage());
			return false;
		}
	}
	
	/**
	 * Trigger the sending of an Authy code via a voice call to a registered user. 
	 * Requires the AuthyID as a string.
	 * 
	 * @param strAuthyID
	 */
	public final boolean requestAuthyVoiceToken (String strAuthyID, Boolean bForce) {
		// When the Authy mobile app is being used, by default SMS messages cannot be sent.
		// This can be overridden.
		Map<String, String> options = new HashMap<String, String>();
		
		if (bForce) {
			// Force sending of SMS even when mobile app is in use.
			options.put("force", "true");
		} else {
			options.put("force", "false");
		}
	
		// Fire the request for a voice call to the user.
		Hash response = users.requestVoice(Integer.parseInt(strAuthyID), options);
	
		if (response.isOk()) {
			// Voice call successfully started.
			return true;
		}
		else {
			// Problem initiating voice call.
			Error error = response.getError();
			System.out.println(error.getMessage());
			return false;
		}
	}
	
	/**
	 * Trigger the sending of an Authy push notification to a registered user. 
	 * Requires the AuthyID as a string.
	 * 
	 * @param strAuthyID
	 */
	public final String requestAuthyPushNotification (String authyID, String message, String playerName, String serverName, String playerUUID) {
		// Send a push notification to the Authy app.
		Map<String, String> options = new HashMap<String, String>();
		options.put("message", message);
		options.put("details[Player Name]", playerName);
		options.put("details[Server Name]", serverName);
		options.put("details[Player UUID]", playerUUID);
	
		// Fire the request for a voice call to the user.
		Hash response = users.requestOneTouchNotify(Integer.parseInt(authyID),  message,  playerName,  serverName,  playerUUID);
	
		if (response.isOk()) {
			// Push notification sen.
			return "UUID";
		}
		else {
			// Problem initiating voice call.
			Error error = response.getError();
			System.out.println(error.getMessage());
			return "UUID";
		}
	}	
}
