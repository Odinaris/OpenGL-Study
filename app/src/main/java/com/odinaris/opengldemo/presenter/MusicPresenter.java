package com.odinaris.opengldemo.presenter;

import com.odinaris.lib_annotation.FunctionConfig;

@FunctionConfig(functionId = 2, functionLevel = 1)
public class MusicPresenter extends BasePresenter {
    private static final String TAG = "MusicPresenter";

    public MusicPresenter(MainCategoryPresenter mainCategoryPresenter) {
        super(mainCategoryPresenter);
    }
}
