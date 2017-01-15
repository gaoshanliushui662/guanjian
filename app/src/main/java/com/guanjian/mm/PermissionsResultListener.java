package com.guanjian.mm;

/**
 * Created by Administrator on 2017/1/15.
 */

public interface PermissionsResultListener {
    void onPermissionGranted();

    void onPermissionDenied();
}
