/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.activity

import android.os.Bundle
import cn.rongcloud.voiceroomdemo.R
import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.common.setAndroidNativeLightStatusBar
import cn.rongcloud.voiceroomdemo.common.showToast


class LauncherActivity : cn.rongcloud.voiceroomdemo.mvp.activity.PermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndroidNativeLightStatusBar(true)
        setContentView(R.layout.activity_launcher)
    }

    override fun onSetPermissions(): Array<String> {
        return PERMISSIONS
    }

    override fun onAccept(accept: Boolean) {
        if (accept) {
            turnToActivity()
        } else {
            showToast("请赋予必要权限！")
            finish()
        }
    }

    private fun turnToActivity() {
        if (AccountStore.getImToken().isNullOrBlank()) {
            LoginActivity.startActivity(this)
            finish()
        } else {
            HomeActivity.startActivity(this)
            finish()
        }
    }
}