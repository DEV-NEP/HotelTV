package com.newitventure.hoteltv.mainactivity;

import com.newitventure.hoteltv.utils.GetData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by NITV-Vinay on 1/5/2018.
 */

public interface MainApiInterface {

    @GET("api/thirdparty/hoteltv/fbpage.jsp")
    Call<GetData> getFBData();

    interface MainDataView{
        void onGettingFBData(GetData getData);
        void onNotGettingFBData(String errMsg);
    }

    interface MainDataPresenter {
        void getFBData();
    }

    interface MainDataListener {
        void onGettingFBData(GetData getData);
        void onNotGettingFBData(String errMsg);
    }

    interface MainDataInteractor {
        void getFBData();
    }
}
