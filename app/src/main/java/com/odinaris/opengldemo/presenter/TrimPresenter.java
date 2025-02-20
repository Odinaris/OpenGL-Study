package com.odinaris.opengldemo.presenter;

import com.odinaris.lib_annotation.FunctionConfig;

@FunctionConfig(functionId = 1, functionLevel = 1)
public class TrimPresenter extends BasePresenter {
    private static final String TAG = "TrimPresenter";

    public TrimPresenter(MainCategoryPresenter mainCategoryPresenter) {
        super(mainCategoryPresenter);
    }
}
