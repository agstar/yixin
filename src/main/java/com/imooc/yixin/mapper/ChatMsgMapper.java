package com.imooc.yixin.mapper;

import com.imooc.yixin.pojo.ChatMsg;
import com.imooc.yixin.utils.MyMapper;

import java.util.List;

public interface ChatMsgMapper extends MyMapper<ChatMsg> {



    void batchUpdateMsgSigned(List<String> msgIdList);
}