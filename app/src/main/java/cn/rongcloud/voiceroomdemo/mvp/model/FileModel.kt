/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.model

import android.content.Context
import android.os.Environment
import android.util.Log
import cn.rongcloud.voiceroomdemo.MyApp
import cn.rongcloud.voiceroomdemo.common.AccountStore
import cn.rongcloud.voiceroomdemo.common.showToast
import cn.rongcloud.voiceroomdemo.net.RetrofitManager
import cn.rongcloud.voiceroomdemo.net.api.ApiConstant
import cn.rongcloud.voiceroomdemo.utils.FileUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.rong.imlib.MD5
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import top.zibin.luban.Luban
import java.io.File
import java.util.*

/**
 * @author gusd
 * @Date 2021/06/15
 */

private const val TAG = "FileModel"

object FileModel {
    fun imageUpload(imagePath: String, context: Context): Single<String> {
        return Flowable
            .just(imagePath)
            .observeOn(Schedulers.io())
            .map {
                return@map Luban.with(context).ignoreBy(100).setFocusAlpha(true).load(it).get()[0]
            }
            .first(File(imagePath))
            .flatMap {
                val requestBody = RequestBody.create(MediaType.parse("image/*"), it)
                val part = MultipartBody.Part.createFormData("file", it.name, requestBody)
                return@flatMap RetrofitManager
                    .commonService
                    .fileUpload(part)
                    .map { shortUrl ->
                        return@map "${shortUrl.data}"
                    }
            }
    }

    private var isDownloading = false

    fun downloadMusic(
        context: Context,
        url: String,
        displayName: String,
        fileName: String,
        block: ((Long, Long) -> Unit)? = null
    ): Completable {
        if (isDownloading) {
            // 正在下载中
            context.showToast("正在下载中")
            return Completable.never()
        }
        MyApp.context.showToast("开始下载: $displayName")
        isDownloading = true
        return Completable.create { emitter ->
            RetrofitManager
                .downloadService
                .downloadFile(url)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    FileUtil.writeFile(
                        it.byteStream(),
                        getCompleteMusicPathByName(fileName)
                    ) { progress ->
                        block?.invoke(it.contentLength(), progress)
                    }
                }.subscribe({

                }, {
                    isDownloading = false
                    emitter.onError(it)
                }, {
                    isDownloading = false
                    MyApp.context.showToast("下载完成: $displayName")
                    emitter.onComplete()
                })
        }

    }

    fun getNameFromUrl(url: String): String? {
        val split = url.split("/")
        return split.lastOrNull()?.replace(" ", "_")
    }

    fun checkOrDownLoadMusic(
        name: String,
        url: String,
        block: ((Long, Long) -> Unit)? = null
    ): Completable {
        return Completable.create { emitter ->
            if (!FileUtil.exists(getCompleteMusicPathByName(getNameFromUrl(url) ?: ""))) {
                this@FileModel.downloadMusic(
                    MyApp.context,
                    url,
                    name,
                    getNameFromUrl(url) ?: "",
                    block
                )
                    .subscribe({
                        Log.d(TAG, "checkOrDownLoadMusic: onComplete")
                        emitter.onComplete()
                    }, {
                        emitter.onError(it)
                    })
            } else {
                emitter.onComplete()
            }
        }

    }

    fun getCompleteMusicPathByName(name: String): String {
        return "${MyApp.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}${File.separator}${
            name.replace(" ", "_")
        }"
    }

    fun musicUpload(context: Context, path: String): Single<String> {
        return Flowable.just(path)
            .observeOn(Schedulers.io())
            .flatMapSingle {
                val requestBody = RequestBody.create(MediaType.parse("audio/*"), File(it))
                val part =
                    MultipartBody.Part.createFormData(
                        "file",
                        getUploadNameByUrl(url = it),
                        requestBody
                    )
                return@flatMapSingle RetrofitManager
                    .commonService
                    .fileUpload(part)
                    .map { shortUrl ->
                        return@map "${ApiConstant.FILE_URL}${shortUrl.data}"
                    }
            }.firstOrError()
    }


    private fun getUploadNameByUrl(url: String): String {
        return getNameFromUrl(url)?.apply {
            val index = this.lastIndexOf(".")
            if (index > -1) {
                val extension = this.subSequence(index, this.length)
                val uploadName = "${AccountStore.getUserId()}${System.currentTimeMillis()}"
                val md5Name = MD5.encrypt(uploadName)
                return md5Name + extension
            }
            return ""

        } ?: ""
    }

    fun moveMusicToCache(url: String, path: String) {
        getNameFromUrl(url)?.let {
            val destFilePath = getCompleteMusicPathByName(it)
            val destFile = File(destFilePath)
            if (destFile.exists()) {
                return
            }
            val srcFile = File(path)
            FileUtil.copyFile(srcFile, destFile)
        }

    }
}