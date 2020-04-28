package com.imooc.yixin.enums;

/**
 * 
 *  添加好友前置状态 枚举
 */
public enum SearchFriendsStatusEnum {
	/**
	 * OK
	 */
	SUCCESS(0, "OK"),
	/**
	 * 无此用户
	 */
	USER_NOT_EXIST(1, "无此用户..."),
	/**
	 * 不能添加你自己
	 */
	NOT_YOURSELF(2, "不能添加你自己..."),
	/**
	 * 该用户已经是你的好友
	 */
	ALREADY_FRIENDS(3, "该用户已经是你的好友...");
	
	public final Integer status;
	public final String msg;
	
	SearchFriendsStatusEnum(Integer status, String msg){
		this.status = status;
		this.msg = msg;
	}
	
	public Integer getStatus() {
		return status;
	}  
	
	public static String getMsgByKey(Integer status) {
		for (SearchFriendsStatusEnum type : SearchFriendsStatusEnum.values()) {
			if (type.getStatus() == status) {
				return type.msg;
			}
		}
		return null;
	}
	
}
