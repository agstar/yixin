package com.imooc.yixin.mapper;


import com.imooc.yixin.pojo.FriendsRequest;
import com.imooc.yixin.pojo.vo.FriendRequestVO;
import com.imooc.yixin.utils.MyMapper;

import java.util.List;

public interface FriendsRequestMapper extends MyMapper<FriendsRequest> {

    List<FriendRequestVO> selectFriend(String myUserId);

}