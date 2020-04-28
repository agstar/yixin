package com.imooc.yixin.controller;

import com.imooc.yixin.pojo.Users;
import com.imooc.yixin.pojo.bo.UsersBO;
import com.imooc.yixin.pojo.vo.UsersVO;
import com.imooc.yixin.service.UserService;
import com.imooc.yixin.utils.FastDFSClient;
import com.imooc.yixin.utils.FileUtils;
import com.imooc.yixin.utils.JSONResult;
import com.imooc.yixin.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("u")
public class UserController {
    private final UserService userService;
    private final FastDFSClient fastDFSClient;

    @Autowired
    public UserController(UserService userService, FastDFSClient fastDFSClient) {
        this.userService = userService;
        this.fastDFSClient = fastDFSClient;
    }


    @PostMapping("/registOrLogin")
    public JSONResult registOrLogin(@RequestBody Users user) throws Exception {

        // 0. 判断用户名和密码不能为空
        if (StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名或密码不能为空...");
        }

        // 1. 判断用户名是否存在，如果存在就登录，如果不存在则注册
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult;
        if (usernameIsExist) {
            // 1.1 登录
            userResult = userService.queryUserForLogin(user.getUsername(),
                    MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return JSONResult.errorMsg("用户名或密码不正确...");
            }
        } else {
            // 1.2 注册
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUser(user);
        }

        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userResult, userVO);

        return JSONResult.ok(userVO);
    }

    @PostMapping("uploadFaceBase64")
    public JSONResult uploadFaceBase64(@RequestBody UsersBO usersBO) throws Exception {

        String base64Data = usersBO.getFaceData();
        String userFacePath = "G:\\" + usersBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        //上传文件到fastdfs
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println(url);
        String thump = "_80x80";
        String[] arr = url.split("\\.");

        String thumpImgUrl = arr[0] + thump + "." + arr[1];
        Users user = new Users();

        user.setId(usersBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(url);
        user = userService.updateUserInfo(user);
        return JSONResult.ok(user);
    }

    /**
     * 设置用户昵称
     */
    @PostMapping("/setNickname")
    public JSONResult setNickname(@RequestBody UsersBO userBO) {

        Users user = new Users();
        user.setId(userBO.getUserId());
        user.setNickname(userBO.getNickname());

        Users result = userService.updateUserInfo(user);

        return JSONResult.ok(result);
    }

    @PostMapping("addFriendRequest")
    public JSONResult addFriendRequest(String myUserId, String friendUsername) {
        return userService.addFriendRequest(myUserId, friendUsername);
    }

    @PostMapping("search")
    public JSONResult search(String myUserId, String friendName) {
        return userService.search(myUserId, friendName);
    }

    @PostMapping("queryFriendRequest")
    public JSONResult queryFriendRequest(String userId) {
        return userService.queryFriendRequest(userId);
    }

    @PostMapping("operFriendRequest")
    public JSONResult operFriendRequest(String acceptUserId, String sendUserId, Integer operType) {
        return userService.operFriendRequest(acceptUserId, sendUserId, operType);
    }

    @PostMapping("getContact")
    public JSONResult getContact(String userId) {
        return userService.getContact(userId);
    }

    /**
     * 查询我的好友列表
     */
    @PostMapping("myFriends")
    public JSONResult myFriends(String userId) {
        return userService.myFriends(userId);
    }
    @PostMapping("getUnReadMsgList")
    public JSONResult getUnReadMsgList(String acceptUserId){
        return null;
    }

}
