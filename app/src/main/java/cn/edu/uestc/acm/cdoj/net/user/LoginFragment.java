package cn.edu.uestc.acm.cdoj.net.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.DebugUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.Map;

import cn.edu.uestc.acm.cdoj.MainActivity;
import cn.edu.uestc.acm.cdoj.R;
import cn.edu.uestc.acm.cdoj.net.UserInfoCallback;
import cn.edu.uestc.acm.cdoj.utils.DigestUtil;
import cn.edu.uestc.acm.cdoj.utils.FileUtil;
import cn.edu.uestc.acm.cdoj.utils.SharedPreferenceUtil;

/**
 * Created by lagranmoon on 2017/8/3.
 */

public class LoginFragment extends Fragment implements View.OnClickListener,UserInfoCallback{
    private static final String TAG = "LoginFragment";

    private final HandleUserData handleUserData = new HandleUserData(this);
    String login_request =handleUserData.handle_login_json();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        Button button_login = view.findViewById(R.id.button_login);
        Button button_register = view.findViewById(R.id.button_register);
        TextView text_forgot_password = view.findViewById(R.id.text_forgot_password);
        button_login.setOnClickListener(this);
        button_register.setOnClickListener(this);
        text_forgot_password.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_register:

                break;
            case R.id.button_login:
                UserConnection.getInstance().login(login_request,LoginFragment.this);
                break;
            case R.id.text_forgot_password:
                break;
        }
    }

    @Override
    public void loginStatus(Bundle bundle) {
        String[] data = bundle.getStringArray("data");
        if (data!=null&&data[0].equals("success")){
            String userName = data[1];
            UserConnection.getInstance().getUserInfo(getActivity(), userName,this,120);
        }else {
            Toast.makeText(getActivity(),"登陆失败",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void getUserInfo(UserInfo userInfo) {
        FileUtil.saveUserInfo(getActivity(),JSON.toJSONString(userInfo),userInfo.getUserName());
        UserInfo user_password = JSON.parseObject(login_request,UserInfo.class);

        save_user_password(user_password);

        Log.d(TAG, "userName:"+userInfo.getUserName());
        Intent intent  = new Intent(getActivity(),MainActivity.class);
        intent.putExtra("userName",userInfo.getUserName());
        startActivity(intent);
    }

    private void save_user_password(UserInfo user_password) {
        String[] key = {DigestUtil.md5(user_password.getUserName()),DigestUtil.md5(user_password.getPassword())};
        String[] value = {user_password.getUserName(),DigestUtil.md5(user_password.getPassword())+"_password"};
        SharedPreferenceUtil.saveSharedPreference(getActivity(),DigestUtil.md5("User"),key,value);
    }


}
