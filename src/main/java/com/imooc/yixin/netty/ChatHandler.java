package com.imooc.yixin.netty;

import com.imooc.yixin.enums.MsgActionEnum;
import com.imooc.yixin.service.UserService;
import com.imooc.yixin.utils.ConfigUtil;
import com.imooc.yixin.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 *
 * @author rcl
 * @date 2018/10/13 19:46
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // 用于记录和管理所有客户端的channle
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
// 获取客户端传输过来的消息
        String text = textWebSocketFrame.text();

        Channel currentChannel = channelHandlerContext.channel();
// 1. 获取客户端发来的消息
        DataContent dataContent = JsonUtils.jsonToPojo(text, DataContent.class);
        Integer action = dataContent.getAction();
        // 2. 判断消息类型，根据不同的类型来处理不同的业务
        if (MsgActionEnum.CONNECT.type.equals(action)) {
// 	2.1  当websocket 第一次open的时候，初始化channel，把用的channel和userid关联起来
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRelationship.put(senderId, currentChannel);
            UserChannelRelationship.output();
        } else if (MsgActionEnum.CHAT.type.equals(action)) {
            //  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();
            // 保存消息到数据库，并且标记为 未签收
            UserService userService = (UserService) ConfigUtil.getBean("userServiceImpl");

            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);
            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            Channel receiverChannel = UserChannelRelationship.get(receiverId);

            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
            } else {
                // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = clients.find(receiverChannel.id());
                if (findChannel != null) {
                    // 用户在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg)));
                } else {
                    // 用户离线 TODO 推送消息
                }
            }
        } else if (MsgActionEnum.SIGNED.type.equals(action)) {
            //  2.3  签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
            UserService userService = (UserService) ConfigUtil.getBean("userServiceImpl");

            String msgIdsStr = dataContent.getExtand();
            String[] msgIds = msgIdsStr.split(",");
            List<String> msgIdList = Arrays.stream(msgIds).filter(StringUtils::isNoneBlank).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(msgIdList)) {
                userService.updateMsgSigned(msgIdList);
            }
        } else if (MsgActionEnum.KEEPALIVE.type.equals(action)) {
            //  2.4  心跳类型的消息
            System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
        }

    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
//        super.handlerRemoved(ctx);
        System.out.println("客户端断开，channel对应的长id为" + ctx.channel().id().asLongText());
        System.out.println("客户端断开，channel对应的短id为" + ctx.channel().id().asShortText());
        clients.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        clients.remove(ctx.channel());
    }
}
