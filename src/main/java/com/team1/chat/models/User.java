package com.team1.chat.models;

import com.team1.chat.interfaces.UserInterface;
import java.util.ArrayList;

public class User implements UserInterface
{
	private String uid;
    private String publicName;
	private String username;
	private String password;
    private String currentChannel;

    public ArrayList<String> invitedChannels;
    public ArrayList<String> privateChannels;

    private ArrayList<User> friends;
    private ArrayList<User> blocked;

    /**
     * Constructor
     */
	public User()
	{
		this.uid=null;
        this.publicName = null;
        this.currentChannel = null;
        this.friends = new ArrayList<User>();
        this.blocked = new ArrayList<User>();
        this.invitedChannels = new ArrayList<String>();
        this.privateChannels = new ArrayList<String>();
	}
    /**
     * Constructor
     *
     * @param id user id
     * @param username user username
     * @param password user password
     */
	public User(String id, String username, String password){
        this.uid=id;
        this.publicName = null;
		this.username=username;
		this.password=password;
        this.friends = new ArrayList<User>();
        this.blocked = new ArrayList<User>();
        this.currentChannel = null;
        this.invitedChannels = new ArrayList<String>();
        this.privateChannels = new ArrayList<String>();
	}

    public String getId()
    {
        return uid;
    }

    public String getPublicName()
    {
        return publicName;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public ArrayList<User> getFriends()
    {
        return friends;
    }

    public ArrayList<User> getBlockedUsers()
    {
        return blocked;
    }

    public String getCurrentChannel()
    {
        return currentChannel;
    }

    public ArrayList<String> getInvitedChannels()
    {
        return this.invitedChannels;
    }

    public ArrayList<String> getPrivateChannels()
    {
        return this.privateChannels;
    }

    /**
     * Method to create a user using a username and password
     *
     * @param username user username
     * @param password user password
     * @return true
     */
    public boolean createUser(String username, String password)
    {
        this.username = username;
        this.password = password;
        return true;
    }

    /**
     * Method that changes the public name of the user
     *
     * @param n name to set as public name
     * @return true on success
     */
    public boolean setPublicName(String n)
    {
        this.publicName = n;
        return true;
    }

    /**
     * Sets the username of this User object.
     *
     * @param uid users id
     * @param newUsername the new username for this user
     * @return true if uid match and username set, false otherwise
     */
    public boolean setUsername(String uid, String newUsername)
    {
        // make sure we have correct User
        if(this.getId().equals(uid))
        {
            this.username = newUsername;
            return true;
        }
        return false;
    }

    /**
     * Sets the password of this User object.
     *
     * @param uid user id
     * @param newPassword new password for user
     * @return true if uid match and password set, false otherwise
     */
    public boolean setPassword(String uid, String newPassword)
    {
        // make sure we have correct User
        if(this.getId().equals(uid))
        {
            this.password = newPassword;
            return true;
        }
        return false;
    }

    /**
     * Method that adds a user to the user's friends list
     *
     * @param f friend to add
     * @return true on success
     */
    public boolean addFriend(User f)
    {
        int i;

        for (i = 0; i < friends.size(); i++)
        {
            if (friends.get(i).getUsername().equals(f.getUsername()))
            {
                return true;
            }
        }

        friends.add(f);
        return true;
    }

    /**
     * Method that removes a user from the user's friends list
     *
     * @param f friend to remove
     * @return true on success
     */
    public boolean removeFriend(User f)
    {
        int i;

        for (i = 0; i < friends.size(); i++)
        {
            if (friends.get(i).getUsername().equals(f.getUsername()))
            {
                friends.remove(i);
                return true;
            }
        }

        return true;
    }

    /**
     * Method that adds a user to the user's blocked list
     *
     * @param f user to add
     * @return true on success
     */
    public boolean blockUser(User f)
    {
        int i;

        for (i = 0; i < blocked.size(); i++)
        {
            if (blocked.get(i).getUsername().equals(f.getUsername()))
            {
                return true;
            }
        }

        blocked.add(f);
        return true;
    }

    /**
     * Method that removes a user from the user's blocked list
     *
     * @param f user to remove
     * @return true on success
     */
    public boolean removeBlockedUser(User f)
    {
        int i;

        for (i = 0; i < blocked.size(); i++)
        {
            if (blocked.get(i).getUsername().equals(f.getUsername()))
            {
                blocked.remove(i);
                return true;
            }
        }

        return true;
    }

    /**
     *  Set the currentChannel of a User
     */
    public boolean setCurrentChannel(String uid, String cname){
        if (this.getId().equals(uid))
        {
            currentChannel = cname;
            return true;
        }
        return false;
    }

    /**
     * Method that adds a channel to the users ArrayList of private channels
     * this is called when a user creates a new channel
     *
     * @param c Channel to add
     * @return true on success
     */
    public boolean addPrivateChannel(Channel c)
    {
        privateChannels.add(c.getName());
        return true;
    }

    /**
     * Method that takes care of removing a deleted channels information
     * from a user
     *
     * @param c channel that has been deleted
     * @return true on success
     */
    public boolean deleteChannel(Channel c)
    {
        int i;
        String deletedChannel = c.getName();

        if (currentChannel != null && currentChannel.equals(deletedChannel))
        {
            currentChannel = null;
        }

        for (i = 0; i < invitedChannels.size(); i++)
        {
            if (invitedChannels.get(i).equals(deletedChannel))
            {
                invitedChannels.remove(i);
            }
        }

        for (i = 0; i < privateChannels.size(); i++)
        {
            if (privateChannels.get(i).equals(deletedChannel))
            {
                privateChannels.remove(i);
            }
        }

        return true;
    }

    /**
     * Method that updates a user's list of invited channels
     *
     * @param c channel user has been invited to
     * @return true on success
     */
    public boolean addChannelInvite(Channel c)
    {
        int i;

        for (i = 0; i < invitedChannels.size(); i++)
        {
            if (invitedChannels.get(i).equals(c.getName()))
            {
                return false;
            }
        }

        for (i = 0; i < privateChannels.size(); i++)
        {
            if (privateChannels.get(i).equals(c.getName()))
            {
                return false;
            }
        }

        invitedChannels.add(c.getName());
        return true;
    }

    /**
     * Method that removes a channel from a user's list of channels
     *
     * @param c channel user has been remove from
     * @return true on success
     */
    public boolean removeFromChannel(Channel c)
    {
        int i;

        if (currentChannel.equals(c.getName()))
        {
            currentChannel = null;
        }

        for (i = 0; i < invitedChannels.size(); i++)
        {
            if (invitedChannels.get(i).equals(c.getName()))
            {
                invitedChannels.remove(i);
                return true;
            }
        }

        for (i = 0; i < privateChannels.size(); i++)
        {
            if (privateChannels.get(i).equals(c.getName()))
            {
                privateChannels.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Method that handles accepting a channel invite
     *
     * @param c channel
     * @return true on success
     */
    public boolean acceptChannelInvite(Channel c)
    {
        int i;

        for (i = 0; i < invitedChannels.size(); i++)
        {
            if (invitedChannels.get(i).equals(c.getName()))
            {
                invitedChannels.remove(i);

                if (!c.isPublic())
                {
                    privateChannels.add(c.getName());
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Method that handles declining a channel invite
     *
     * @param c channel
     * @return true on success
     */
    public boolean declineChannelInvite(Channel c)
    {
        int i;

        for (i = 0; i < invitedChannels.size(); i++)
        {
            if (invitedChannels.get(i).equals(c.getName()))
            {
                invitedChannels.remove(i);
                return true;
            }
        }

        return false;
    }
}
