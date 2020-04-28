package com.imooc.yixin.service;

import com.imooc.yixin.netty.ChatMsg;
import com.imooc.yixin.pojo.Users;
import com.imooc.yixin.utils.JSONResult;

import java.util.List;

public interface UserService {

    /**
     * 判断用户名是否存在
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 查询用户是否存在
     */
    Users queryUserForLogin(String username, String pwd);

    /**
     * 用户注册
     */
    Users saveUser(Users user);

    Users updateUserInfo(Users user);

    JSONResult addFriendRequest(String myUserId, String friendUsername);

    JSONResult search(String myUserId, String friendName);

    JSONResult queryFriendRequest(String userId);

    JSONResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType);
    JSONResult getContact(String userId);
    JSONResult myFriends(String userId);
    JSONResult getUnReadMsgList(String acceptUserId);

    String saveMsg(ChatMsg chatMsg);

    void updateMsgSigned(List<String> msgIdList);
}
