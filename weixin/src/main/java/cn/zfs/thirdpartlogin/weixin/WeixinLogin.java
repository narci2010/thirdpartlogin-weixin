package cn.zfs.thirdpartlogin.weixin;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import cn.zfs.thirdpartlogin.core.BaseLogin;
import cn.zfs.thirdpartlogin.core.Callback;
import cn.zfs.thirdpartlogin.core.LoginCallback;
import cn.zfs.thirdpartlogin.core.UserInfo;
import cn.zfs.thirdpartlogin.core.Utils;

/**
 * 时间: 2017/9/28 15:41
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 微信登录
 * 需要在app包名目录下建立wxapi包，并在其中新建WXEntryActivity，继承自WXEventActivity，内容不需要写。
 * 然后在AndroidManifest.xml添加如下：
 <activity
 android:theme="@android:style/Theme.NoDisplay"
 android:name=".wxapi.WXEntryActivity"
 android:exported="true"/>
 */

public class WeixinLogin extends BaseLogin {
    private String appid;
    private String secret;
    //获取token
    private String toekenUrlPattern = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    //获取userInfo
    private String userInfoUrlPattern = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";

    public WeixinLogin(Context context) {
        super(context);
        appid = Utils.getString(context, "weixin_appid");
        secret = Utils.getString(context, "weixin_secret");
    }

    public void onDestroy() {
        WXEventActivity.callback = null;
    }
    
    @Override
    public void login(Activity activity, LoginCallback callback) {
        super.login(activity, callback);     
        IWXAPI api = WXAPIFactory.createWXAPI(activity, appid);
        if (!api.isWXAppInstalled()) {
            onError(8888, Utils.getString(context, "tpl_wx_not_install"));
        } else if (!api.isWXAppSupportAPI()) {
            onError(BaseResp.ErrCode.ERR_UNSUPPORT, Utils.getString(context, "tpl_wx_not_support_api"));
        } else {
            WXEventActivity.callback = respCallback;
            api.registerApp(appid);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "third_login_state";
            api.sendReq(req);
        }
    }

    private Callback<BaseResp> respCallback = new Callback<BaseResp>() {
        @Override
        public void onCallback(BaseResp baseResp) {
            if (BaseResp.ErrCode.ERR_OK == baseResp.errCode) {
                SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                if ("third_login_state".equals(resp.state)) {
                    requestUserInfo(resp.code);
                } else {
                    onError(baseResp.errCode, Utils.getString(context, "tpl_login_fail"));
                }
            } else {
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        onError(baseResp.errCode, Utils.getString(context, "tpl_author_denied"));
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        onCancel();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        onError(baseResp.errCode, Utils.getString(context, "tpl_author_denied"));
                        break;
                    default:
                        onError(baseResp.errCode, Utils.getString(context, "tpl_login_fail"));
                        break;
                }
            }
        }
    };
        
    private void requestUserInfo(String code) {
        new AsyncTask<String, Void, Object[]>() {
            @Override
            protected Object[] doInBackground(String... params) {
                try {
                    String tokenResp = Utils.request(String.format(toekenUrlPattern, appid, secret, params[0]));
                    JSONObject tokenJson = new JSONObject(tokenResp);
                    String infoResp = Utils.request(String.format(userInfoUrlPattern, tokenJson.getString("access_token"), tokenJson.getString("openid")));
                    JSONObject infoJson = new JSONObject(infoResp);
                    UserInfo info = new UserInfo();
                    info.id = infoJson.getString("unionid");
                    info.nickname = infoJson.optString("nickname");
                    info.gender = infoJson.optInt("sex", 1) == 1 ? "M" : "F";
                    String province = infoJson.optString("province");
                    String city = infoJson.optString("city");
                    info.location = infoJson.optString("country") + (province.isEmpty() ? "" : " " + province) + (city.isEmpty() ? "" : " " + city);
                    info.figureUrl = infoJson.optString("headimgurl");
                    //如果头像链接不为空，将链接将成大图
                    if (!info.figureUrl.isEmpty()) {
                        int lastIndex = info.figureUrl.lastIndexOf("/");
                        if (lastIndex != -1 && lastIndex < info.figureUrl.length() - 1) {
                            String lastNum = info.figureUrl.substring(lastIndex + 1);
                            try {
                                int a = Integer.parseInt(lastNum);
                                if (a != 0) {
                                    info.figureUrl = info.figureUrl.substring(0, lastIndex) + "/" + 0;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return new Object[]{info, infoJson};
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object[] objs) {
                if (objs == null) {
                    onError(8888, Utils.getString(context, "tpl_login_fail"));
                } else {
                    onSuccess((UserInfo) objs[0], (JSONObject) objs[1]);
                }
            }
        }.execute(code);
    }

    @Override
    public int loginType() {
        return WEI_XIN;
    }
}
