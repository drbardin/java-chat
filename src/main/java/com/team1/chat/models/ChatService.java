package com.team1.chat.models;

import com.team1.chat.interfaces.ChatServiceInterface;
import java.util.ArrayList;

public class ChatService implements ChatServiceInterface
{
    // class-level instantiation of DatabaseSupport object.
    private DatabaseSupport dbs = null;

    /**
     * Constructor
     */
    public ChatService()
    {
    	dbs = this.getDatabaseSupportInstance();
    }

    /**
     * Creates a new User in the database.
     */
    public boolean createAccount(String username, String password)
    {
        //check if name is available
        if(!getDatabaseSupportInstance().nameAvailable(username)) {
            System.out.println("Error creating account: Name already in use.");
            return false;
        }

        //check if password is well-formed
        if(!checkWellFormed(password)) {
            return false;
        }

        User u = new User();
        u.createUser(username, password);

        return dbs.putUser(u);
    }

    /**
     * Gets User data from the database.
     * User joins the default channel.
     *
     * @return Returns the user's id.
     * If the database could not retrieve a valid user, id returned is empty.
     */
    public String login(String username, String password)
    {
        User u = dbs.getUser(username, password);
        if (u == null){
        	return null;
        }

        String id = u.getId();

        if (joinChannel("Lobby",id))
        {
            return id;
        }
        return null;
    }

    /**
     * User logs out and leaves the default channel.
     *
     * @return true on success, false on fail.
     */
    public boolean logout(String uid)
    {
        User u = dbs.getUserById(uid);

        return u != null && leaveChannel(u.getCurrentChannel(), uid);
    }

    /**
     * Fetch this uid associated object from database and update username.
     *
     * @param uid user id
     * @param newUsername new username for user
     * @return true on successful update, false otherwise.
     */
    public boolean setUsername(String uid, String newUsername)
    {
        // get User object from database.
        User u = this.getDatabaseSupportInstance().getUserById(uid);
        if (u == null)
        {
        	return false;
        }

        // Check if username is same as first.
        if (u.getUsername().equals(newUsername))
        {
        	System.out.println("Username entered is same as the existing username.");
        	return false;
        }

        // Check if username is already taken.
        if (!this.getDatabaseSupportInstance().nameAvailable(newUsername))
        {
        	System.out.println("That username is not available.");
        	return false;
        }
        
        // use setUsername method in User object to place new username.
        if (!u.setUsername(uid, newUsername)){
        	System.out.println("User.setUsername(uid, newUsername) Error: uid parameter does not match this user's id."); 
        	return false;
        }

        // return User object to database, if returns false, we have an error.
        if (!dbs.putUser(u)) {
            System.out.println("Database put error");
            return false;
        }
        return true;
    }

    /**
     * Fetch this uid associated object from database and update username.
     *
     * @param uid user id
     * @param newPassword new password for user
     * @return true on successful update, false otherwise.
     */
    public boolean setPassword(String uid, String newPassword)
    {
        // get User object from database.
        User u = getDatabaseSupportInstance().getUserById(uid);
        if (u == null)
        {
        	return false;
        }

        // check if new password is same as old password.
        if (u.getPassword().equals(newPassword))
        {
        	System.out.println("New password is same as existing password.");
        	return false;
        }

        // check if password if of a valid format.
        if (!this.checkWellFormed(newPassword))
        {
        	return false;
        }

        // use setUsername method in User object to place new username.
        if (!u.setPassword(uid, newPassword))
        {
        	System.out.println("User.setPassword(uid, newPassword) Error: uid does not match this user's id.");
        	return false;
        }

        // return User object to database, if returns false, we have an error.
        if (!dbs.putUser(u))
        {
            System.out.println("Database put error");
            return false;
        }
        return true;
    }

    /**
     * Method that removes a user from the channel by calling the removeChannelUser() method
     *
     * @param cname channel name
     * @param uid   user id
     * @return true if successful: false otherwise
     */
    public boolean leaveChannel(String cname, String uid)
    {
        Channel ch = this.getDatabaseSupportInstance().getChannelByName(cname);
        if (ch == null)
        {
        	System.out.println("Specified channel name does not exist.");
        	return false;
        }

        User u = this.getDatabaseSupportInstance().getUserById(uid);
        if (u == null)
        {
        	System.out.println("Specified user does not exist.");
        	return false;
        }

        if (ch.removeChannelUser(u))
        {
            return u.setCurrentChannel(u.getId(), null) &&
                   this.getDatabaseSupportInstance().putUser(u) &&
                   this.getDatabaseSupportInstance().putChannel(ch);
        }

        System.out.println("Leave channel failed.");
        return false;
    }

    /**
     * Method that adds a user to the channel by calling the addChannelUser() method
     *
     * @param cname channel id
     * @param uid   user id
     * @return true if successful: false otherwise
     */
    public boolean joinChannel(String cname, String uid)
    {

        Channel ch = this.getDatabaseSupportInstance().getChannelByName(cname);
        User u = this.getDatabaseSupportInstance().getUserById(uid);

        return ch != null &&
                u != null &&
                u.getCurrentChannel() == null &&
                u.setCurrentChannel(uid, cname) &&
                ch.addChannelUser(u) &&
                this.getDatabaseSupportInstance().putUser(u) &&
                this.getDatabaseSupportInstance().putChannel(ch);
    }

    /**
     * Method that returns a list of users in the channel by calling the listChannelUsers() method
     *
     * @param cname channel name
     * @param uid   user id
     * @return a list of channel users if successful: null otherwise
     */
    public ArrayList<User> listChannelUsers(String cname, String uid)
    {
        Channel ch = this.getDatabaseSupportInstance().getChannelByName(cname);
        User u = this.getDatabaseSupportInstance().getUserById(uid);

        if (ch != null && u != null && ch.isWhiteListed(u))
        {
            return ch.getCurrentUsers();
        }

        return null;
    }

    /**
     * Method that creates a new channel and adds the current user to that channel
     *
     * @param cname channel name
     * @param aid   users id
     * @return true on success
     */
    public boolean createChannel(String cname, String aid)
    {
        if (this.getDatabaseSupportInstance().getChannelByName(cname) == null) {
        	if (cname==null || cname.isEmpty()){
        		System.out.println("A name was not provided for the new channel.");
        		return false;
        	}
        	if (cname.length()>10){
        		System.out.println("A channel name may be no longer than 10 characters.");
        		return false;
        	}
            Channel c = new Channel(cname, aid);
            User u = this.getDatabaseSupportInstance().getUserById(aid);
            if (u == null)
            {
            	return false;
            }
            u.addPrivateChannel(c);
            c.whiteListUser(aid, u);

            return this.getDatabaseSupportInstance().putChannel(c) &&
                   this.getDatabaseSupportInstance().putUser(u);
        }
        return false;
    }

    /**
     * Method that deletes a channel
     *
     * @param cname channel name
     * @param aid   users id
     * @return true on success
     */
    public boolean deleteChannel(String cname, String aid)
    {
        int i;
        Channel c;
        User user;
        ArrayList<User> users;

        c = this.getDatabaseSupportInstance().getChannelByName(cname);
        if (c != null) {
            users = c.deleteChannel(aid);
            if (users != null) {
                // the user is confirmed to be the admin and we have a list of users in the channel.
                for (i = 0; i < users.size(); i++) {
                    user = this.getDatabaseSupportInstance().getUserById(users.get(i).getId());
                    user.deleteChannel(c);
                    if (!this.getDatabaseSupportInstance().putUser(user))
                    {
                        return false;
                    }
                }
                if (this.getDatabaseSupportInstance().deleteChannel(c.getName())){
                	System.out.println("Channel removed from database.");
                	return true;
                }
                else {
                	System.out.println("Unable to remove channel from database.");
                	return false;
                }
            }
            else {
            	System.out.println("This user does not have sufficient privileges to delete the channel.");
            	return false;
            }
        }
        else{
        	System.out.println("Channel could not be found");
        	return false;
        }
    }

    /**
     * Method that invites a user to a channel
     *
     * @param cname channel name
     * @param aid   current user id
     * @param uname name of user to invite
     * @return true on success
     */
    public boolean inviteUserToChannel(String cname, String aid, String uname)
    {
        Channel c = this.getDatabaseSupportInstance().getChannelByName(cname);
        User u = this.getDatabaseSupportInstance().getUserByName(uname);

        if (c != null && u != null)
        {
            if (c.whiteListUser(aid, u) && u.addChannelInvite(c))
            {
                return this.getDatabaseSupportInstance().putChannel(c) &&
                       this.getDatabaseSupportInstance().putUser(u);
            }
        }
        return false;
    }

    /**
     * Method that removes a user from a channel
     *
     * @param cname channel name
     * @param aid   current user id
     * @param uname name of user to remove
     * @return true on success
     */
    public boolean removeUserFromChannel(String cname, String aid, String uname)
    {
        Channel c = this.getDatabaseSupportInstance().getChannelByName(cname);
        User u = this.getDatabaseSupportInstance().getUserByName(uname);

        if (c != null && u != null)
        {
            if (c.removeUser(aid, u) && u.removeFromChannel(c))
            {
                return this.getDatabaseSupportInstance().putChannel(c) &&
                       this.getDatabaseSupportInstance().putUser(u);
            }
        }
        return false;
    }

    /**
     * Method that gets a list of channels the user is invited to
     *
     * @param uid users id
     * @return a list of channels the user is invited to
     */
    public ArrayList<String> viewInvitedChannels(String uid)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);

        if (u != null)
        {
        	return u.getInvitedChannels();
        }
        return null;
    }

    /**
     * Method that gets a list of channels the user has joined
     *
     * @param uid users id
     * @return a list of private channels
     */
    public ArrayList<String> viewPrivateChannels(String uid)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);

        if (u != null)
        {
        	return u.getInvitedChannels();
        }
        return null;
    }

    /**
     * Method that gets a list of public channels
     *
     * @return a list of public channels
     */
    public ArrayList<String> viewPublicChannels(String uid)
    {
        User u;

        u = this.getDatabaseSupportInstance().getUserById(uid);
        if (u==null){
        	return null;
        }
        return u.getInvitedChannels();
    }

    /**
     * Method to check if password is well-formed
     * Criteria:
     * 1) 8 <= length <= 45
     * 2) string contains at least two numerics [0-9]
     *
     * @param newPass password to check
     * @return true if well-formed
     */
    public boolean checkWellFormed(String newPass)
    {

        // Check min & max length limits
        if (newPass.length() > 45) {
            System.out.println("Error: Password must be at most 45 characters. Length: " + newPass.length());
            return false;
        }
        if (newPass.length() < 8) {
            System.out.println("Error: Password must be at least 8 characters. Length: " +  newPass.length());
            return false;
        }

        int count = 0;
        // loop newPass char by char, increment count if isDigit.
        for (int i = 0; i < newPass.length(); i++) {
            if (Character.isDigit(newPass.charAt(i)))
                count++;
        }
        if (count >= 2)
            return true;
        else{
            System.out.println("Password must contain at least two digits [0-9]. Number of digits: " + count);
            return false;
        }
    }

    /**
     * Method that toggles visibility of a channel
     *
     * @param cname channel name
     * @param aid   users id
     * @return true on success
     */
    public boolean toggleChannelVisibility(String cname, String aid)
    {
        Channel c = this.getDatabaseSupportInstance().getChannelByName(cname);

        if (c != null)
        {
        	if (c.toggleChannelVisibility(aid))
            {
        		if(dbs.putChannel(c))
                {
        			return true;
        		}
        		System.out.println("Failed to put channel in database.");
        		return false;
        	}
        	else
            {
        		System.out.println("Failed to toggle channel visibility.");
        		return false;
        	}
        }
        else
        {
        	System.out.println("Channel does not exist.");
        	return false;
        }
    }

    /**
     * Method that adds a friend to a user
     *
     * @param uid user id
     * @param username friend username
     * @return true on success
     */
    public boolean addFriend(String uid, String username)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);
        User f = this.getDatabaseSupportInstance().getUserByName(username);

        return u != null &&
               f != null &&
               u.addFriend(f) &&
               this.getDatabaseSupportInstance().putUser(u);
    }

    /**
     * Method that removes a friend from a user
     *
     * @param uid user id
     * @param username friend username
     * @return true on success
     */
    public boolean removeFriend(String uid, String username)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);
        User f = this.getDatabaseSupportInstance().getUserByName(username);

        return u != null &&
               f != null &&
               u.removeFriend(f) &&
               this.getDatabaseSupportInstance().putUser(u);
    }

    /**
     * Method that returns a users friend list
     *
     * @param uid user id
     * @return a list of friends
     */
    public ArrayList<User> viewFriends(String uid)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);

        if (u != null)
        {
        	return u.getFriends();
        }

        return null;
    }

    /**
     * Method that adds users to a user's block list
     *
     * @param uid user id
     * @param username blocked users username
     * @return true on success
     */
    public boolean addBlockedUser(String uid, String username)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);
        User f = this.getDatabaseSupportInstance().getUserByName(username);

        return u != null &&
               f != null &&
               u.blockUser(f) &&
               this.getDatabaseSupportInstance().putUser(u);
    }

    /**
     * Method that removes a user from a user's block list
     *
     * @param uid user id
     * @param username blocked users username
     * @return true on success
     */
    public boolean removeBlockedUser(String uid, String username)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);
        User f = this.getDatabaseSupportInstance().getUserByName(username);

        return u != null &&
               f != null &&
               u.removeBlockedUser(f) &&
               this.getDatabaseSupportInstance().putUser(u);
    }

    /**
     * Method that returns a user's block list
     *
     * @param uid user id
     * @return a list of blocked users
     */
    public ArrayList<User> viewBlockedUsers(String uid)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);

        if (u != null)
        {
        	return u.getBlockedUsers();
        }

        return null;
    }

    /**
     * Method that sets the public name of a user
     *
     * @param uid user id
     * @param publicName public name for user
     * @return true on success
     */
    public boolean setPublicName(String uid, String publicName)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);

        return u != null &&
               u.setPublicName(publicName) &&
               this.getDatabaseSupportInstance().putUser(u);
    }

    /**
     * Method that handles accepting an invite to a channel
     *
     * @param uid user id
     * @param cname channel name
     * @return true on success
     */
    public boolean acceptInviteToChannel(String uid, String cname)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);
        Channel c = this.getDatabaseSupportInstance().getChannelByName(cname);

        return u != null &&
               c != null &&
               c.isWhiteListed(u) &&
               u.acceptChannelInvite(c) &&
               this.getDatabaseSupportInstance().putUser(u);
    }

    /**
     * Method that handles declining an invite to a channel
     *
     * @param uid user id
     * @param cname channel name
     * @return true on success
     */
    public boolean declineInviteToChannel(String uid, String cname)
    {
        User u = this.getDatabaseSupportInstance().getUserById(uid);
        Channel c = this.getDatabaseSupportInstance().getChannelByName(cname);

        return c != null &&
               u != null &&
               c.isWhiteListed(u) &&
               c.removeDeclinedInviteFromWhiteList(uid) &&
               u.declineChannelInvite(c) &&
               this.getDatabaseSupportInstance().putChannel(c) &&
               this.getDatabaseSupportInstance().putUser(u);
    }

    /**
     * @return a new instance of DatabaseSupport class
     */
    private DatabaseSupport getDatabaseSupportInstance()
    {
        if (dbs == null)
        {
            dbs = new DatabaseSupport();
        }
        return dbs;
    }
}
