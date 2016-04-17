package com.team1.chat.interfaces;

import com.team1.chat.models.Channel;
import com.team1.chat.models.User;

public interface DatabaseSupportInterface
{
    // Iteration 1
    boolean putUser(User u);

    User getUser(String username, String password);

    User getUserById(String uid);

    boolean nameAvailable(String newUsername);

    boolean putChannel(Channel c);

    void close();

    // Iteration 2
    Channel getChannelByName(String cname);

    boolean deleteChannel(String cname);

    User getUserByName(String uname);
}
