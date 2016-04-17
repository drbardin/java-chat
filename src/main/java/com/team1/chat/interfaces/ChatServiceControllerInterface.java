package com.team1.chat.interfaces;

import com.team1.chat.models.Channel;
import com.team1.chat.models.User;

import java.util.ArrayList;

public interface ChatServiceControllerInterface
{
    // Iteration 1
    boolean createAccount(String username, String password);

    String login(String username, String password);

    boolean logout(String uid);

    boolean setUsername(String uid, String newUsername);

    boolean setPassword(String uid, String newPassword);

    boolean leaveChannel(String cid, String uid);

    boolean joinChannel(String cid, String uid);

    ArrayList<User> listChannelUsers(String cid, String uid);

    // Iteration 2
    boolean createChannel(String cname, String aid);

    boolean deleteChannel(String cname, String aid);

    boolean inviteUserToChannel(String cname, String aid, String uname);

    boolean removeUserFromChannel(String cname, String aid, String uname);

    ArrayList<String> viewInvitedChannels(String uid);

    ArrayList<String> viewPrivateChannels(String uid);

    ArrayList<String> viewPublicChannels(String uid);

    boolean toggleChannelVisibility(String cname, String aid);

    // Iteration 3
    boolean addFriend(String uid, String username);

    boolean removeFriend(String uid, String username);

    ArrayList<User> viewFriends(String uid);

    boolean addBlockedUser(String uid, String username);

    boolean removeBlockedUser(String uid, String username);

    ArrayList<User> viewBlockedUsers(String uid);

    boolean setPublicName(String uid, String publicName);

    boolean acceptInviteToChannel(String uid, String cname);

    boolean declineInviteToChannel(String uid, String cname);
}
