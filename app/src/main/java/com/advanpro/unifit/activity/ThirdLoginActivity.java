package com.advanpro.unifit.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.advanpro.unifit.R;
import com.advanpro.unifit.utils.Utils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.zfs.thirdpartlogin.core.LoginCallback;
import cn.zfs.thirdpartlogin.core.UserInfo;
import cn.zfs.thirdpartlogin.weixin.WeixinLogin;

/**
 * 时间: 2017/9/20 15:35
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 第三方登录
 */

public class ThirdLoginActivity extends AppCompatActivity {
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.tv)
    TextView tv;
    private Toast toast;
    private WeixinLogin weixinLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_login);
        ButterKnife.bind(this);
        weixinLogin = new WeixinLogin(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weixinLogin.onDestroy();
    }

    public void showToast(int resid) {
        Toast t = getToast();
        t.setText(resid);
        t.show();
    }

    public void showToast(String s) {
        Toast t = getToast();
        t.setText(s);
        t.show();
    }

    private Toast getToast() {
        if (toast == null) {
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        return toast;
    }

    @OnClick(R.id.btnLogin)
    public void onViewClicked() {
        weixinLogin.login(this, new LoginCallback() {
            @Override
            public void onSuccess(int loginType, final UserInfo userInfo, JSONObject origin) {
                tv.append("id: " + userInfo.id + "\n");
                tv.append("性别: " + userInfo.gender + "\n");
                tv.append("昵称: " + userInfo.nickname + "\n");
                tv.append("所在地: " + userInfo.location + "\n");
                tv.append("头像: " + userInfo.figureUrl + "\n\n");
                tv.append("原始数据\n");
                tv.append(origin.toString().replace(",", "\n"));
                if (!TextUtils.isEmpty(userInfo.figureUrl)) {
                    new Thread() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = Utils.getNetBitmap(userInfo.figureUrl);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }.start();
                }
            }

            @Override
            public void onCancel() {
                tv.setText("");
                iv.setImageBitmap(null);
                showToast("用户取消");
            }

            @Override
            public void onError(int errorCode, String errorDetail) {
                tv.setText("");
                iv.setImageBitmap(null);
                showToast(errorDetail);
            }
        });
    }
}
