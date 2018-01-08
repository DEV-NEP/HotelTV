package com.newitventure.hoteltv.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.newitventure.hoteltv.R;
import com.newitventure.hoteltv.mainactivity.MainActivity;

/**
 * Created by NITV VNE on 4/1/2018.
 */

public class FBLoginActivity extends Activity {

    private String TAG = FBLoginActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private AccessTokenTracker tokenTracker;
    private ProfileTracker profileTracker;
    LoginButton loginButton;
    private TextView day, month;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fblogin_activity);

        day = (TextView) findViewById(R.id.day);
        day.setText(DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_WEEKDAY));
        month = (TextView) findViewById(R.id.month);
        month.setText(DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_ALL));


        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (newToken != null && Profile.getCurrentProfile() != null) {
                    nextActivity(Profile.getCurrentProfile(), newToken.getToken());
                }
            }
        };
        tokenTracker.startTracking();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d(TAG, "onCurrentProfileChanged: NewProfile: " + newProfile);
                this.stopTracking();
                Profile.setCurrentProfile(newProfile);
                nextActivity(newProfile, AccessToken.getCurrentAccessToken().getToken());
            }

        };
        profileTracker.startTracking();


        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                loginButton.setVisibility(View.INVISIBLE);

                final AccessToken accessToken = loginResult.getAccessToken();
                Log.d(TAG, "onSuccess: accessToken: " + accessToken.getToken());
                final Profile profile = Profile.getCurrentProfile();
                Log.d(TAG, "onSuccess: Profile: " + profile);

                /*At first login, profile returns "null" and app crashes. So this is handled by following if-else statement*/
                if (profile == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                            this.stopTracking();
                            Profile.setCurrentProfile(newProfile);
                            nextActivity(newProfile, accessToken.getToken());
                        }

                    };
                    profileTracker.startTracking();
                } else
                    nextActivity(profile, accessToken.getToken());

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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        callbackManager.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:");
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (profile != null) {
            nextActivity(profile, accessToken.getToken());
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
        tokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    private void nextActivity(Profile profile, String accessToken) {

        Intent intent = new Intent(FBLoginActivity.this, MainActivity.class);
        intent.putExtra("name", profile.getFirstName());
        intent.putExtra("token", accessToken);
//            intent.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
        startActivity(intent);
//        }
    }

}
