package com.hodoo.webapp.framework.model;

/**
 * 사용자 OS 종류.
 */
public enum UserOperatingSystem {

    WINDOWS("WINDOWS"),
    IOS("IOS"),
    ANDROID("ANDROID"),
    UNKNOWN("ETC");

    /**
     * OS 종류 라벨
     */
    String label;

    UserOperatingSystem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
