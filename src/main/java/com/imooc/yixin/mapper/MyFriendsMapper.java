package com.imooc.yixin.mapper;


import com.imooc.yixin.pojo.MyFriends;
import com.imooc.yixin.pojo.vo.MyFriendsVO;
import com.imooc.yixin.utils.MyMapper;

import java.util.List;

public interface MyFriendsMapper extends MyMapper<MyFriends> {


    List<MyFriendsVO> selectAllFriend(String myUserId);

}