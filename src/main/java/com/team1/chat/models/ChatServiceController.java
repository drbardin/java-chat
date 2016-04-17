package com.team1.chat.models;

import com.team1.chat.interfaces.ChatServiceControllerInterface;
import java.util.ArrayList;

public class ChatServiceController implements ChatServiceControllerInterface
{
    private ChatService cs = null;
	
    public ChatServiceController(){
    	cs = this.getChatServiceInstance();
    }

    public boolean createAccount(String username, String password)
    {
    	
    	return cs.createAccount(username, password);
    }

    public String login(String username, String password)
    {
        return cs.login(username, password);
    }

    public boolean logout(String uid)
    {
        return cs.logout(uid);
    }

    public boolean setUsername(String uid, String newUsername)
    {
        return this.getChatServiceInstance().setUsername(uid, newUsername);
    }

    public boolean setPassword(String uid, String newPassword)
    {
        return this.getChatServiceInstance().setPassword(uid, newPassword);
    }

    public boolean leaveChannel(String cid, String uid)
    {
        return this.getChatServiceInstance().leaveChannel(cid, uid);
    }

    public boolean joinChannel(String cid, String uid)
    {
        return this.getChatServiceInstance().joinChannel(cid, uid);
    }

    public ArrayList<User> listChannelUsers(String cname, String uid)
    {
        return this.getChatServiceInstance().listChannelUsers(cname, uid);
    }

    // Iteration 2
    public boolean createChannel(String cname, String aid)
    {
        return this.getChatServiceInstance().createChannel(cname, aid);
    }

    public boolean deleteChannel(String cname, String aid)
    {
        return this.getChatServiceInstance().deleteChannel(cname, aid);
    }

    public boolean inviteUserToChannel(String cname, String aid, String uname)
    {
        return this.getChatServiceInstance().inviteUserToChannel(cname, aid, uname);
    }

    public boolean removeUserFromChannel(String cname, String aid, String uname)
    {
        return this.getChatServiceInstance().removeUserFromChannel(cname, aid, uname);
    }

    public ArrayList<String> viewInvitedChannels(String uid)
    {
        return this.getChatServiceInstance().viewInvitedChannels(uid);
    }

    public ArrayList<String> viewPrivateChannels(String uid)
    {
        return this.getChatServiceInstance().viewPrivateChannels(uid);
    }

    public ArrayList<String> viewPublicChannels(String uid)
    {
        return this.getChatServiceInstance().viewPublicChannels(uid);
    }

    public boolean toggleChannelVisibility(String cname, String aid)
    {
        return this.getChatServiceInstance().toggleChannelVisibility(cname, aid);
    }

    // Iteration 3
    public boolean addFriend(String uid, String username)
    {
        return this.getChatServiceInstance().addFriend(uid, username);
    }

    public boolean removeFriend(String uid, String username)
    {
        return this.getChatServiceInstance().removeFriend(uid, username);
    }

    public ArrayList<User> viewFriends(String uid)
    {
        return this.getChatServiceInstance().viewFriends(uid);
    }

    public boolean addBlockedUser(String uid, String username)
    {
        return this.getChatServiceInstance().addBlockedUser(uid, username);
    }

    public boolean removeBlockedUser(String uid, String username)
    {
        return this.getChatServiceInstance().removeBlockedUser(uid, username);
    }

    public ArrayList<User> viewBlockedUsers(String uid)
    {
        return this.getChatServiceInstance().viewBlockedUsers(uid);
    }

    public boolean setPublicName(String uid, String publicName)
    {
        return this.getChatServiceInstance().setPublicName(uid, publicName);
    }

    public boolean acceptInviteToChannel(String uid, String cname)
    {
        return this.getChatServiceInstance().acceptInviteToChannel(uid, cname);
    }

    public boolean declineInviteToChannel(String uid, String cname)
    {
        return this.getChatServiceInstance().declineInviteToChannel(uid, cname);
    }

    private ChatService getChatServiceInstance()
    {
        if (cs == null)
        {
            cs = new ChatService();
        }
        return cs;
    }
}
