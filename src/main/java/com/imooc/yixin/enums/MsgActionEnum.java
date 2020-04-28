package com.imooc.yixin.enums;

/**
 * 发送消息的动作 枚举
 */
public enum MsgActionEnum {
    /**
     * 第一次(或重连)初始化连接
     */
    CONNECT(1, "第一次(或重连)初始化连接"),
    /**
     * 聊天消息
     */
    CHAT(2, "聊天消息"),
    /**
     * 消息签收
     */
    SIGNED(3, "消息签收"),
    /**
     * 客户端保持心跳
     */
    KEEPALIVE(4, "客户端保持心跳"),
    /**
     * 拉取好友
     */
    PULL_FRIEND(5, "拉取好友");

    public final Integer type;
    public final String content;

    MsgActionEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }

    public Integer getType() {
        return type;
    }
}