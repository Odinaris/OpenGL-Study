package com.odinaris.opengldemo.presenter;

import android.util.Log;

public class MainCategoryPresenter {

    private static final String TAG = "MainCategoryPresenter";

    public MainCategoryPresenter() {
        BasePresenter presenter = PresenterRegistry.createPresenter(this, "2");
        Log.d(TAG, "presenter = " + presenter.getClass().getSimpleName());
    }
}
