package com.infoworks.lab.components.crud.components.views.search;

public class SearchBarConfigurator {
    private boolean hideAddNewButton;

    public boolean isHideAddNewButton() {
        return hideAddNewButton;
    }

    public SearchBarConfigurator setHideAddNewButton(boolean hideAddNewButton) {
        this.hideAddNewButton = hideAddNewButton;
        return this;
    }
}
