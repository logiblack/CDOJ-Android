package cn.edu.uestc.acm.cdoj.net;

import android.os.Bundle;

import cn.edu.uestc.acm.cdoj.net.user.UserInfoReceived;

/**
 * Created by lagranmoon on 2017/7/25.
 */

public interface UserInfoCallback {
    void loginStatus(Bundle bundle);
    void getUserInfo(UserInfoReceived.UserBean userBean);
}