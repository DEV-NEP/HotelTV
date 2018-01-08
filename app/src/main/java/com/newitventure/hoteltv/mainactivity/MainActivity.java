package com.newitventure.hoteltv.mainactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.facebook.login.LoginManager;
import com.newitventure.hoteltv.R;
import com.newitventure.hoteltv.facebook.FBLoginActivity;
import com.newitventure.hoteltv.facebook.FbParser;
import com.newitventure.hoteltv.utils.GetData;
import com.newitventure.hoteltv.utils.HttpHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

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

    int count = 0;

    MainDataPresImpl mainDataPres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        access_token = intent.getExtras().get("token").toString();
        Log.d(TAG, "onCreate: Access Token: " + access_token);
        profileName = intent.getExtras().get("name").toString();

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

        setting = (ImageView) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("Setting Clicked!");
                toast.show();
            }
        });

        listView = (ListView) findViewById(R.id.list);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar1);
        refreshProgress = findViewById(R.id.refreshProgress);

        //        mainDataPres = new MainDataPresImpl(this);
//        mainDataPres.getFBData();
        new GetPageID().execute();

        fbUserName = (TextView) findViewById(R.id.profileName);
        fbUserName.setText("Welcome " + profileName.toUpperCase() + ",");

        language = (TextView) findViewById(R.id.language);
        flag = (ImageView) findViewById(R.id.flag);

        text2 = (TextView) findViewById(R.id.text2);
        text2.setText(DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL));

        text3 = (TextView) findViewById(R.id.text3);
        text3.setText(DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_WEEKDAY));


        wholeLayout = findViewById(R.id.whole_layout);
        final int[] background_images = {R.drawable.bg, R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3};
        new ChangeBackGround(background_images).start();
        /*
        final android.os.Handler handler = new android.os.Handler();
        Runnable runnable = new Runnable() {
            int i = 0;
            @Override
            public void run() {
                wholeLayout.setBackgroundResource(background_images[i]);
                i++;
                if (i > background_images.length - 1) {
                    i = 0;
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 5000);
*/


        int[] arr_drawable = {R.drawable.room_service, R.drawable.hotel_tv, R.drawable.travel_guide, R.drawable.call_taxi, R.drawable.my_bill, R.drawable.about_hotel, R.drawable.placeholder,R.drawable.placeholder,R.drawable.placeholder};

        String[] tabName = {"ROOM SERVICE", "HOTEL TV", "TRAVEL GUIDE", "CALL TAXI", "MY BILL", "ABOUT HOTEL","NETFLIX","YOUTUBE","HULU"};

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        int hei=getResources().getDisplayMetrics().heightPixels/5;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabLayout.getLayoutParams();
        params.height = hei;
        tabLayout.setLayoutParams(params);

        RelativeLayout layoutFeed = (RelativeLayout)findViewById(R.id.list_view);
        params = (RelativeLayout.LayoutParams) layoutFeed.getLayoutParams();
        params.height =(int)(getResources().getDisplayMetrics().heightPixels/2.5);
        layoutFeed.setLayoutParams(params);

        Button button = (Button) findViewById(R.id.logoutBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
                Intent logout = new Intent(MainActivity.this, FBLoginActivity.class);
                startActivity(logout);
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mainDataPres = new MainDataPresImpl(this);
//                mainDataPres.getFBData();
                refreshBtn.setVisibility(View.GONE);
                new GetPageID().execute();
            }
        });

        for (int i = 0; i < tabName.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
            RelativeLayout tabChild = (RelativeLayout) view.findViewById(R.id.tab);
            /*RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)tabChild.getLayoutParams();
            params1.width = getResources().getDisplayMetrics().heightPixels/6;
            tabChild.setLayoutParams(params1);*/

            ImageView imageView = (ImageView) view.findViewById(R.id.pics);
            imageView.getLayoutParams().width = (int) (getResources().getDisplayMetrics().widthPixels/6.8);

            imageView.setImageResource(arr_drawable[i]);
            TextView tabTitle = (TextView) view.findViewById(R.id.txt);
            tabTitle.setText(tabName[i]);
//            tabLayout.getTabAt(i).setText(tabName[i]);
//            tabLayout.getTabAt(i).setIcon(arr_drawable[i]);
            tabLayout.addTab(tabLayout.newTab().setCustomView(view));
        }

        final LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.language:
                    case R.id.flag:
                    case R.id.linearLayout:
                        @SuppressLint("RestrictedApi") Context context = new ContextThemeWrapper(MainActivity.this, R.style.CustomPopupTheme);
                        PopupMenu popupMenu = new PopupMenu(context, linearLayout);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
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

                                    case R.id.nepali:
                                        language.setText("NP");
                                        flag.setImageResource(R.drawable.flag_nepal);
                                        flag.setScaleY(1);
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
                switch (tab.getPosition()) {
                    case 1:
                        startNewActivity("com.worldtvgo");
                        break;

                    default:

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        if(listAdapter.getCount()>2){
//            View item = listAdapter.getView(0,null,listView);
//            item.measure(0,0);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (3 * item.getMeasuredHeight()));
//            listView.setLayoutParams(params);
//        }
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
            refreshProgress.setVisibility(View.VISIBLE);
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

    public void startNewActivity(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
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
    class ChangeBackGround extends Thread{
        int[] imgList;
        int i = 0;
        public ChangeBackGround(int[] imgList){
            this.imgList = imgList;
        }
        @Override
        public void run() {
            super.run();

            while (true){
                Log.d(TAG, "run: "+(imgList.length-1)+"/t"+i);
                if(i==imgList.length-1)
                    i=0;
//                wholeLayout.setBackground(imgList[i]);
                runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      wholeLayout.setBackgroundDrawable( getResources().getDrawable(imgList[i]) );
                                  }
                              }
                );

                i++;
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
