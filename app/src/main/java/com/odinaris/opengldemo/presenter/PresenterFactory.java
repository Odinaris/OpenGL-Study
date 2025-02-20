package com.odinaris.opengldemo.presenter;

public interface PresenterFactory {
    BasePresenter create(MainCategoryPresenter mainCategoryPresenter);
}