package com.team1.chat.interfaces;

import com.team1.chat.models.Channel;
import com.team1.chat.models.User;

import java.util.ArrayList;

public interface UserInterface
{
    // Iteration 1
    boolean createUser(String username, String password);

    String getId();

    boolean setUsername(String uid, String newUsername);

    String getUsername();

    boolean setPassword(String uid, String newPassword);

    String getPassword();

    // Iteration 2
    boolean addPrivateChannel(Channel c);

    boolean deleteChannel(Channel c);

    boolean addChannelInvite(Channel c);

    boolean removeFromChannel(Channel c);

    ArrayList<String> getInvitedChannels();

    ArrayList<String> getPrivateChannels();

    // Iteration 3
    boolean addFriend(User f);

    boolean removeFriend(User f);

    ArrayList<User> getFriends();

    boolean blockUser(User f);

    boolean removeBlockedUser(User f);

    ArrayList<User> getBlockedUsers();

    boolean setPublicName(String publicName);

    boolean acceptChannelInvite(Channel c);

    boolean declineChannelInvite(Channel c);

    String getPublicName();

    String getCurrentChannel();

    boolean setCurrentChannel(String uid, String cname);
}
