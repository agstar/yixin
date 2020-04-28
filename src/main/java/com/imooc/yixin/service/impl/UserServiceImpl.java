package com.imooc.yixin.service.impl;

import com.imooc.yixin.enums.MsgSignFlagEnum;
import com.imooc.yixin.mapper.ChatMsgMapper;
import com.imooc.yixin.mapper.FriendsRequestMapper;
import com.imooc.yixin.mapper.MyFriendsMapper;
import com.imooc.yixin.mapper.UsersMapper;
import com.imooc.yixin.netty.ChatMsg;
import com.imooc.yixin.pojo.FriendsRequest;
import com.imooc.yixin.pojo.MyFriends;
import com.imooc.yixin.pojo.Users;
import com.imooc.yixin.pojo.vo.FriendRequestVO;
import com.imooc.yixin.pojo.vo.MyFriendsVO;
import com.imooc.yixin.pojo.vo.UsersVO;
import com.imooc.yixin.service.UserService;
import com.imooc.yixin.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static String[] words = {"A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z", "#"};

    private final UsersMapper userMapper;
    private final Sid sid;
    private final QRCodeUtils qrCodeUtils;
    private final FastDFSClient fastDFSClient;
    private final MyFriendsMapper myFriendsMapper;
    private final FriendsRequestMapper friendsRequestMapper;
    private final ChatMsgMapper chatMsgMapper;

    @Autowired
    public UserServiceImpl(UsersMapper userMapper, Sid sid, QRCodeUtils qrCodeUtils, FastDFSClient fastDFSClient, MyFriendsMapper myFriendsMapper, FriendsRequestMapper friendsRequestMapper, ChatMsgMapper chatMsgMapper) {
        this.userMapper = userMapper;
        this.sid = sid;
        this.qrCodeUtils = qrCodeUtils;
        this.fastDFSClient = fastDFSClient;
        this.myFriendsMapper = myFriendsMapper;
        this.friendsRequestMapper = friendsRequestMapper;
        this.chatMsgMapper = chatMsgMapper;
    }

    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);

        Users result = userMapper.selectOne(user);

        return result != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String pwd) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", pwd);

        Users result = userMapper.selectOneByExample(userExample);

        return result;
    }

    @Override
    public Users saveUser(Users user) {

        String userId = sid.nextShort();

//        // 为每个用户生成一个唯一的二维码
        String qrCodePath = "G://user" + userId + "qrcode.png";
        // muxin_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath, "muxin_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrCodeUrl);

        user.setId(userId);
        userMapper.insert(user);

        return user;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user) {
        userMapper.updateByPrimaryKeySelective(user);
        return userMapper.selectByPrimaryKey(user.getId());
    }

    /**
     * 发送好友请求
     *
     * @author rcl
     * @date 10/20/2018 11:55 AM
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public JSONResult addFriendRequest(String myUserId, String friendUsername) {
        //满足条件后
        JSONResult result = search(myUserId, friendUsername);
        if (result.isOK()) {
            UsersVO friend = (UsersVO) result.getData();

            FriendsRequest friendsRequest = new FriendsRequest();
            friendsRequest.setSendUserId(myUserId);
            friendsRequest.setAcceptUserId(friend.getId());

            List<FriendsRequest> requests = friendsRequestMapper.select(friendsRequest);
            if (requests == null || requests.size() == 0) {

                friendsRequest.setRequestDateTime(new Date());
                friendsRequest.setId(sid.nextShort());
                friendsRequestMapper.insert(friendsRequest);
                return JSONResult.ok();
            } else {
                return JSONResult.errorMsg("已发送好友请求，无需重复发送");
            }
        }
        return result;
    }

    /**
     * 搜索好友
     *
     * @author rcl
     * @date 10/20/2018 11:54 AM
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public JSONResult search(String myUserId, String friendName) {
        // 0. 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId)
                || StringUtils.isBlank(friendName)) {
            return JSONResult.errorMsg();
        }

        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Users users = new Users();
        users.setUsername(friendName);
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", friendName);
        Users friend = userMapper.selectOneByExample(example);
        if (friend == null) {
            return JSONResult.errorMsg("用户不存在");
        }
        if (friend.getId().equals(myUserId)) {
            return JSONResult.errorMsg("不能提娜佳自己");
        }
        MyFriends condition = new MyFriends();
        condition.setMyUserId(myUserId);
        condition.setMyFriendUserId(friend.getId());
        MyFriends myFriend = myFriendsMapper.selectOne(condition);
        if (myFriend != null) {
            return JSONResult.errorMsg("该用户已经是你的好友");
        }
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(friend, userVO);
        return JSONResult.ok(userVO);
    }

    /**
     * 查询好友请求
     *
     * @author rcl
     * @date 10/20/2018 11:54 AM
     */
    @Override
    public JSONResult queryFriendRequest(String userId) {
        List<FriendRequestVO> friendRequestVOList = friendsRequestMapper.selectFriend(userId);

        return JSONResult.ok(friendRequestVOList);
    }


    /**
     * 操作好友请求
     *
     * @author rcl
     * @date 10/20/2018 11:54 AM
     */
    @Override
    public JSONResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType) {
        //先删除请求记录
        FriendsRequest friendsRequest = new FriendsRequest();

        friendsRequest.setAcceptUserId(acceptUserId);
        friendsRequest.setSendUserId(sendUserId);
        FriendsRequest friendsRequest1 = friendsRequestMapper.selectOne(friendsRequest);
        friendsRequestMapper.deleteByPrimaryKey(friendsRequest1);

        //接受
        if (operType.equals(1)) {
            //为自己构建好友信息
            MyFriends myFriends = new MyFriends();
            myFriends.setId(sid.nextShort());
            myFriends.setMyUserId(acceptUserId);
            myFriends.setMyFriendUserId(sendUserId);
            myFriendsMapper.insert(myFriends);
            //将我的信息构建给发送好友请求人
            MyFriends friends = new MyFriends();
            friends.setId(sid.nextShort());
            friends.setMyUserId(sendUserId);
            friends.setMyFriendUserId(acceptUserId);
            myFriendsMapper.insert(friends);

        }
        return JSONResult.ok();
    }

    /**
     * 获取联系人
     *
     * @author rcl
     * @date 10/20/2018 11:55 AM
     */
    @Override
    public JSONResult getContact(String userId) {
        List<MyFriendsVO> friendList = myFriendsMapper.selectAllFriend(userId);
        Map<String, List<MyFriendsVO>> map = new HashMap<>(48);
        for (MyFriendsVO myFriend : friendList) {
            String s = PinYinUtil.cn2FirstSpell(myFriend.getFriendNickname());
            map.putIfAbsent(s, new ArrayList<>());
            map.get(s).add(myFriend);
        }
        Map<String, List<MyFriendsVO>> result = new LinkedHashMap<>();
        for (String word : words) {
            List<MyFriendsVO> myFriendsVOS = map.get(word);
            if (myFriendsVOS != null && myFriendsVOS.size() != 0) {
                result.put(word, myFriendsVOS);
            }
        }
        LinkedList list = new LinkedList();
        return JSONResult.ok(result);
    }

    @Override
    public JSONResult myFriends(String userId) {
        List<MyFriendsVO> friendList = myFriendsMapper.selectAllFriend(userId);
        return JSONResult.ok(friendList);
    }

    @Override
    public JSONResult getUnReadMsgList(String acceptUserId) {

        com.imooc.yixin.pojo.ChatMsg chatMsg = new com.imooc.yixin.pojo.ChatMsg();
        chatMsg.setSignFlag(0);
        chatMsg.setAcceptUserId(acceptUserId);
        List<com.imooc.yixin.pojo.ChatMsg> chatMsgList = chatMsgMapper.select(chatMsg);
        return JSONResult.ok(chatMsgList);
    }

    @Override
    public String saveMsg(ChatMsg chatMsg) {
        com.imooc.yixin.pojo.ChatMsg msg = new com.imooc.yixin.pojo.ChatMsg();
        String msgId = sid.nextShort();
        msg.setId(msgId);
        msg.setAcceptUserId(chatMsg.getReceiverId());
        msg.setSendUserId(chatMsg.getSenderId());
        msg.setCreateTime(new Date());
        msg.setSignFlag(MsgSignFlagEnum.unsign.type);
        msg.setMsg(chatMsg.getMsg());
        chatMsgMapper.insert(msg);
        return msgId;
    }


    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        chatMsgMapper.batchUpdateMsgSigned(msgIdList);
    }
}
