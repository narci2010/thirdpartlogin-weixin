package cn.zfs.thirdpartlogin.weixin;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import cn.zfs.thirdpartlogin.core.Callback;

public class WXEventActivity extends Activity implements IWXAPIEventHandler { 
    public static Callback<BaseResp> callback;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, null);
        iwxapi.handleIntent(getIntent(), this);
    }
    
    @Override
    public void onReq(BaseReq baseReq) {}

    @Override
    public void onResp(BaseResp baseResp) {
        if (callback != null) {
            callback.onCallback(baseResp);
        }
        finish();
    }
}