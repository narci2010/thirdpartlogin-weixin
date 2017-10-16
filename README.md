### Step 1 ###
在根build.gradle加入jitpack的仓库:

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }

### Step 2 ###
在module的build.gradle添加依赖

    dependencies {
        compile 'com.github.fszeng2011:thirdpartlogin-core:1.0'
        compile 'com.github.fszeng2011:thirdpartlogin-weixin:1.0'
    }

### Step 3 ###
在你的包名相应目录下新建一个wxapi目录，并在该wxapi目录下新增一个WXEntryActivity类，该类继承自WXEventActivity，内容不需要写。

### Step 4 ###
在AndroidManifest.xml添加如下：    

    <activity
        android:theme="@android:style/Theme.NoDisplay"
        android:name=".wxapi.WXEntryActivity"
        android:exported="true"/>

### Step 5 ###
在字符串资源xml文件中添加appid和secret

    <string name="weixin_appid">wxde56b4c4fcd755c7</string>
    <string name="weixin_secret">33702de56b4c4fcd755c76bbf7468943</string>

### Demo ###
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
	        weixinLogin = new QQLogin(this);
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
	        qqLogin.login(this, new LoginCallback() {
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