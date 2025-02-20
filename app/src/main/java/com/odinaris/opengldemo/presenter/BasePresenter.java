package com.odinaris.opengldemo.presenter;

import android.util.Log;

public abstract class BasePresenter {

    protected String TAG = "BasePresenter";

    BasePresenter(MainCategoryPresenter mainCategoryPresenter) {
        Log.d(TAG, this.getClass().getSimpleName());
    }
}
