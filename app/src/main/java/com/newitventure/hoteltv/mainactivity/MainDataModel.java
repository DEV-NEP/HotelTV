package com.newitventure.hoteltv.mainactivity;

import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.newitventure.hoteltv.utils.GetData;
import com.newitventure.hoteltv.utils.LinkConfig;
import com.newitventure.hoteltv.utils.SetupRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by NITV-Vinay on 1/5/2018.
 */

public class MainDataModel implements MainApiInterface.MainDataInteractor {

    public String TAG = getClass().getSimpleName();

    MainApiInterface.MainDataListener mainDataListener;
    SetupRetrofit setupRetrofit;

    public MainDataModel(MainApiInterface.MainDataListener mainDataListener) {
        this.mainDataListener = mainDataListener;
        setupRetrofit = new SetupRetrofit();
    }

    @Override
    public void getFBData() {
        Log.d(TAG, "getFBData: ");

        Retrofit retrofit = setupRetrofit.getRetrofit(LinkConfig.BASE_URL);
        MainApiInterface apiInterface = retrofit.create(MainApiInterface.class);

        Call<GetData> call = apiInterface.getFBData();
        call.enqueue(new Callback<GetData>() {
            @Override
            public void onResponse(Call<GetData> call, Response<GetData> response) {
                Log.d(TAG, "onResponse: ");
                GetData fbData = response.body();
                mainDataListener.onGettingFBData(fbData);
            }

            @Override
            public void onFailure(Call<GetData> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }
}
