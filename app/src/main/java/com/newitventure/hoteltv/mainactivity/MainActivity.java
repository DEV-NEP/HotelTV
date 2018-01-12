package com.newitventure.hoteltv.mainactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.newitventure.hoteltv.R;
import com.newitventure.hoteltv.facebook.FbParser;
import com.newitventure.hoteltv.utils.GetData;
import com.newitventure.hoteltv.utils.HttpHandler;

import org.json.JSONObject;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends Activity implements MainApiInterface.MainDataView {

    final static String TAG = MainActivity.class.getSimpleName();

    String access_token, profileName;

    @BindView(R.id.refreshBtn)
    ImageView refreshBtn;

    TextView text, fbUserName, text2, text3, language;
    ImageView flag, alarm, mail, setting;
    ListView listView;
    ProgressBar progressBar, refreshProgress;
    Toast toast;
    RelativeLayout wholeLayout;
    Button logoutButton;

    int count = 0;

    MainDataPresImpl mainDataPres;

    private CallbackManager callbackManager;
    private AccessTokenTracker tokenTracker;
    private ProfileTracker profileTracker;
    LoginButton loginButton;
    boolean isLoggedIn, isNewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        loginButton = findViewById(R.id.login_button);
        logoutButton = findViewById(R.id.logoutBtn);
        if (!isLoggedIn){
            logoutButton.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Log.d(TAG, "onClick: login");
                    login();
            }
        });
        callbackManager = CallbackManager.Factory.create();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
//                AccessToken.setCurrentAccessToken(null);
                isLoggedIn = false;
                logoutButton.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                fbUserName.setText("Welcome,");

//                Intent logout = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(logout);
            }
        });

        final View view0 = getLayoutInflater().inflate(R.layout.toast_activity, null);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 50);
        toast.setView(view0);
        text = (TextView) view0.findViewById(R.id.toast_txt);

        alarm = (ImageView) findViewById(R.id.alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("Alarm Clicked!");
                toast.show();
            }
        });

        mail = (ImageView) findViewById(R.id.mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("Mail Clicked!");
                toast.show();
            }
        });

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });

        listView = findViewById(R.id.list);
        progressBar = findViewById(R.id.progress_bar1);
        refreshProgress = findViewById(R.id.refreshProgress);
        fbUserName = findViewById(R.id.profileName);
        language = findViewById(R.id.language);
        flag = findViewById(R.id.flag);

        text2 = findViewById(R.id.text2);
        text2.setText(DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL));

        text3 = findViewById(R.id.text3);
        text3.setText((DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_WEEKDAY)) + ", ");

        wholeLayout = findViewById(R.id.whole_layout);
        final int[] background_images = {R.drawable.bg_0, R.drawable.bg, R.drawable.bg_2};
//        Runnable r = new Runnable(){
//            int i = 0;
//            public void run(){
//                wholeLayout.setBackgroundResource(background_images[i]);
//                i++;
//                if(i > background_images.length -1){
//                    i = 0;
//                }
//                wholeLayout.postDelayed(this, 3000); //set to go off again in 3 seconds.
//            }
//        };
//        wholeLayout.postDelayed(r,3000);
        new ChangeBackGround(background_images).start();

        //to add more items to tablayout add here:
        int[] arr_drawable = {R.drawable.hotel_tv, R.drawable.room_service, R.drawable.travel_guide, R.drawable.call_taxi, R.drawable.my_bill, R.drawable.youtube, R.drawable.netflix, R.drawable.abema_tv, R.drawable.hulu, R.drawable.about_hotel, R.drawable.translate};
        String[] tabName = {"HOTEL TV", "ROOM SERVICE", "TRAVEL GUIDE", "CALL TAXI", "MY BILL", "YOUTUBE", "NETFLIX", "ABEMA TV", "HULU", "ABOUT HOTEL", "TRANSLATE"};

        TabLayout tabLayout = findViewById(R.id.tabs);
        int hei = getResources().getDisplayMetrics().heightPixels / 5;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabLayout.getLayoutParams();
        params.height = hei;
        tabLayout.setLayoutParams(params);

        for (int i = 0; i < tabName.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.custom_tab, null);

            ImageView imageView = view.findViewById(R.id.pics);
            imageView.getLayoutParams().width = (int) (getResources().getDisplayMetrics().widthPixels / 6);

            imageView.setImageResource(arr_drawable[i]);
            TextView tabTitle = view.findViewById(R.id.txt);
            tabTitle.setText(tabName[i]);
//            tabLayout.getTabAt(i).setText(tabName[i]);
//            tabLayout.getTabAt(i).setIcon(arr_drawable[i]);
            tabLayout.addTab(tabLayout.newTab().setCustomView(view));
        }

        RelativeLayout layoutFeed = findViewById(R.id.list_view);
        params = (RelativeLayout.LayoutParams) layoutFeed.getLayoutParams();
        params.height = (int) (getResources().getDisplayMetrics().heightPixels / 2.2);
        layoutFeed.setLayoutParams(params);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mainDataPres = new MainDataPresImpl(this);
//                mainDataPres.getFBData();
                refreshBtn.setVisibility(View.GONE);
                new GetPageID().execute();
            }
        });

        final LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.language:
                    case R.id.flag:
                    case R.id.linearLayout:

                        @SuppressLint("RestrictedApi") Context context = new ContextThemeWrapper(MainActivity.this, R.style.CustomPopupTheme);
                        PopupMenu popupMenu = new PopupMenu(context, linearLayout);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                        try {
                            Field mFieldPopup=popupMenu.getClass().getDeclaredField("mPopup");
                            mFieldPopup.setAccessible(true);
                            MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popupMenu);
                            mPopup.setForceShowIcon(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        popupMenu.show();

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.chinese:
                                        language.setText("CH");
                                        flag.setImageResource(R.drawable.flag_china);
                                        flag.setScaleY((float) 1.4);
                                        return true;

                                    case R.id.english:
                                        language.setText("EN");
                                        flag.setImageResource(R.drawable.flag_uk);
                                        flag.setScaleY(1);
                                        return true;

                                    case R.id.german:
                                        language.setText("GR");
                                        flag.setImageResource(R.drawable.flag_germany);
                                        flag.setScaleY((float) 1.4);
                                        return true;

                                    case R.id.japanese:
                                        language.setText("JP");
                                        flag.setImageResource(R.drawable.flag_japan);
                                        flag.setScaleY((float) 1.4);
                                        return true;

                                    case R.id.korean:
                                        language.setText("KR");
                                        flag.setImageResource(R.drawable.flag_korea);
                                        flag.setScaleY((float) 1.4);
                                        return true;

                                    default:
                                        return false;
                                }

                            }
                        });
                }

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected: " + tab.getPosition());
                View view = tab.getCustomView();
                TextView tabTitle = view.findViewById(R.id.txt);
                tabTitle.setTextColor(getResources().getColor(R.color.white));
                switch (tab.getPosition()) {
                    case 0:
                        startAppActivity("com.worldtvgo");
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;

                    case 5:
                        startAppActivity("com.google.android.youtube");
                        break;

                    case 6:
                        startAppActivity("com.netflix.mediaclient");
                        break;

                    case 7:
                        startAppActivity("tv.abema");
                        break;
                        
                    case 8:
                        startAppActivity("com.hulu.plus");
                        break;

                    case 9:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.apahotel.com/ja_en/about/"));
                        startActivity(browserIntent);
                        break;

                    case 10:
                        startAppActivity("com.google.android.apps.translate");
                        break;

                    default:

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabUnselected: ");

                View view = tab.getCustomView();
                TextView tabTitle = view.findViewById(R.id.txt);
                tabTitle.setTextColor(getResources().getColor(R.color.textColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabReselected: ");
                switch (tab.getPosition()) {
                    case 0:
                        startAppActivity("com.worldtvgo");
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;

                    case 5:
                        startAppActivity("com.google.android.youtube");
                        break;

                    case 6:
                        startAppActivity("com.netflix.mediaclient");
                        break;

                    case 7:
                        startAppActivity("tv.abema");
                        break;

                    case 8:
                        startAppActivity("com.hulu.plus");
                        break;

                    case 9:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.apahotel.com/ja_en/about/"));
                        startActivity(browserIntent);
                        break;

                    case 10:
                        startAppActivity("com.google.android.apps.translate");
                        break;

                    default:
                }
            }
        });

//        if(listAdapter.getCount()>2){
//            View item = listAdapter.getView(0,null,listView);
//            item.measure(0,0);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (3 * item.getMeasuredHeight()));
//            listView.setLayoutParams(params);
//        }
    }

    private void login() {
        Log.d(TAG, "login: ");
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (newToken != null && Profile.getCurrentProfile() != null) {
                    profileName = Profile.getCurrentProfile().getFirstName();
                    access_token = newToken.getToken();
                }
            }
        };
        tokenTracker.startTracking();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d(TAG, "onCurrentProfileChanged: NewProfile: " + newProfile);
                Log.d(TAG, "onCurrentProfileChanged: OldProfile: " + oldProfile);
                this.stopTracking();
                Profile.setCurrentProfile(newProfile);
                profileName = newProfile.getFirstName();
                Log.d(TAG, "onCurrentProfileChanged: ===> "+profileName);
                access_token = AccessToken.getCurrentAccessToken().getToken();
                fbUserName.setText("Welcome " + newProfile.getFirstName().toUpperCase() + ",");
            }

        };


        profileTracker.startTracking();
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "onSuccess: ");
                isLoggedIn = true;
                loginButton.setVisibility(View.GONE);
                logoutButton.setVisibility(View.VISIBLE);

                final AccessToken accessToken = loginResult.getAccessToken();
                Log.d(TAG, "onSuccess: accessToken: " + accessToken.getToken());
                final Profile profile = Profile.getCurrentProfile();
                Log.d(TAG, "onSuccess: Profile: " + profile);

                /*At first login, profile returns "null" and app crashes. So this is handled by following if-else statement*/
                if (profile == null) {
                    Log.d(TAG, "onSuccess: profilr null");
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                            this.stopTracking();
                            Profile.setCurrentProfile(newProfile);
                            profileName = newProfile.getFirstName();
                            access_token = accessToken.getToken();
                            fbUserName.setText("Welcome " + profileName.toUpperCase() + ",");
                        }

                    };
                    profileTracker.startTracking();
                } else {
                    profileName = profile.getFirstName();
                    access_token = accessToken.getToken();

                }
                Log.d(TAG, "onSuccess: ======>"+profileName);
                fbUserName.setText("Welcome " + profileName.toUpperCase() + ",");
                //        mainDataPres = new MainDataPresImpl(this);
//        mainDataPres.getFBData();
                new GetPageID().execute();
                progressBar.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                Log.e(TAG, "onError: Cannot Login to Facebook");
            }
        });
    }

    @Override
    public void onGettingFBData(GetData getData) {
        Log.d(TAG, "onGettingFBData: ");

        String id = getData.getId();
        FbParser fbParser = new FbParser(getApplicationContext(), listView, id, progressBar, access_token, toast, text);
        fbParser.jsonParser();
    }

    @Override
    public void onNotGettingFBData(String errMsg) {

    }

    public class GetPageID extends AsyncTask<Void, Void, Void> {
        String url = "http://worldondemand.net/app/json_v5/binay.php";
        String id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            if (refreshBtn.getVisibility() == View.GONE) {
                refreshProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler httpHandler = new HttpHandler();
            String jsonStr = httpHandler.makeServiceCall(url);
            Log.d(TAG, "preparePage: Response from url: " + jsonStr);
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
//                    String pageName = jsonObject.getString("page");
                id = jsonObject.getString("id");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refreshProgress.setVisibility(View.GONE);
            refreshBtn.setVisibility(View.VISIBLE);

            FbParser fbParser = new FbParser(getApplicationContext(), listView, id, progressBar, access_token, toast, text);
            fbParser.jsonParser();

        }
    }

    public void startAppActivity(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Redirect user to the play store or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (count == 1) {
            toast.cancel();
            count = 0;
            finishAffinity();
        } else {

            text.setText("Press Back again to Exit");
            toast.show();
            count++;
        }
        return;
    }

    class ChangeBackGround extends Thread {
        int[] imgList;
        int i = 0;

        ChangeBackGround(int[] imgList) {
            this.imgList = imgList;
        }

        @Override
        public void run() {
            super.run();

            while (true) {
                Log.d(TAG, "run: " + (imgList.length - 1) + "/t" + i);

                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      wholeLayout.setBackgroundDrawable(getResources().getDrawable(imgList[i]));
                                  }
                              }
                );

                i++;
                if (i >= imgList.length - 1)
                    i = 0;
                try {
                    sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        callbackManager.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: isLogin" +isLoggedIn);
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        Log.d(TAG, "onResume: profile=== "+profile);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.d(TAG, "onResume: token=== "+accessToken);

        if (profile != null || accessToken != null) {

            if (profile != null){
                Log.d(TAG, "onResume: already logged in  "+profile.getFirstName());
                profileName = profile.getFirstName();
                fbUserName.setText("Welcome " + profileName.toUpperCase() + ",");

            }

            if (!isLoggedIn) {
                new GetPageID().execute();
            }
            loginButton.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            isLoggedIn = true;

            access_token = accessToken.getToken();

        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause:");
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        //Facebook login
//        tokenTracker.stopTracking();
//        profileTracker.stopTracking();
    }

}
