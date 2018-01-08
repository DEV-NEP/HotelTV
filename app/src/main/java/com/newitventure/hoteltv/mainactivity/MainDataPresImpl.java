package com.newitventure.hoteltv.mainactivity;

import com.newitventure.hoteltv.utils.GetData;

/**
 * Created by NITV-Vinay on 1/5/2018.
 */

public class MainDataPresImpl implements MainApiInterface.MainDataListener, MainApiInterface.MainDataPresenter {

    MainApiInterface.MainDataView mainDataView;
    MainApiInterface.MainDataInteractor mainDataInteractor;

    public MainDataPresImpl(MainApiInterface.MainDataView mainDataView) {
        this.mainDataView = mainDataView;
        mainDataInteractor = new MainDataModel(this);
    }

    @Override
    public void getFBData() {
        mainDataInteractor.getFBData();
    }

    @Override
    public void onGettingFBData(GetData getData) {
        mainDataView.onGettingFBData(getData);
    }

    @Override
    public void onNotGettingFBData(String errMsg) {
        mainDataView.onNotGettingFBData(errMsg);
    }
}
