package com.attendance.bk.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException

/**
 * 图片辅助类
 *
 * @author Chen Xujie
 * @since 2019-04-08
 */
object ImageHelper {

    const val REQUEST_CAMERA = 0x210
    const val REQUEST_ALBUM = 0x211

    /**
     * 读取相机拍摄的照片路径
     *
     * @return
     */
    var cameraFilePath: String? = null
        private set

    const val IMG_UPLOAD_DIR = "imageUpload"


    /**
     * 获取记账流水图片url 返回本地图片地址
     *
     * @param context   Context
     * @param imageName 图片名
     * @return uri
     */
    fun getLocalImage(context: Context, imageName: String): File? {
        if (TextUtils.isEmpty(imageName)) {
            return null
        }
        val dir = getLocalImageStoreDir(context)
        val f = File(dir, imageName)
        return if (f.exists() && f.isFile) {
            f
        } else null
    }

    /**
     * 获取本地记账图片保存的文件夹
     *
     * @param context Context
     * @return 文件夹
     */
    private fun getLocalImageStoreDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMG_UPLOAD_DIR)
        if (!dir.exists() || !dir.isDirectory) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * 打开相机获取图片
     */
    fun openCamera(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoUri(activity))
        activity.startActivityForResult(intent, REQUEST_CAMERA)
    }

    /**
     * 打开相机获取图片
     */
    fun openCamera(fragment: Fragment) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoUri(fragment.context))
        fragment.startActivityForResult(intent, REQUEST_CAMERA)
    }


    /**
     * 压缩裁剪旋转图片
     *
     *
     * 保存图片到指定文件夹等待上传
     *
     * @param originalImagePath 图片路径
     * @param saveName          保存文件名（不带后缀） 缩略图最大宽高
     * @return 压缩裁剪后的图片位置
     */
    fun saveImageToUpload(context: Context, originalImagePath: String, saveName: String) {
        //该方法已在工作线程执行，所以这里直接同步执行
        try {
            Compressor(context)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath(getLocalImageStoreDir(context).absolutePath)
                    .compressToFile(File(originalImagePath), saveName)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * 获取相机拍照存储的图片位置
     * 为了保证第三方相机也可以保存图片，因此设置为存储卡指定文件夹
     *
     * @param context Context
     * @return 文件位置
     */
    private fun generateTmpSaveFilePath(context: Context): File {
        val f = File(context.externalCacheDir, "/camera/tmp" + System.currentTimeMillis() + ".jpg")
        if (!f.parentFile.exists()) {
            f.parentFile.mkdirs()
        }
        cameraFilePath = f.path
        return f
    }

    /**
     * 获取相机拍照存储的图片位置Uri
     *
     * @return 图片uri
     */
    private fun getPhotoUri(context: Context?): Uri {
        val saveFile = generateTmpSaveFilePath(context!!)
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            try {
                FileProvider.getUriForFile(context, SPKey.FILE_PROVIDER_AUTHORITIES, saveFile)
            } catch (e: Throwable) {
                Uri.fromFile(saveFile)
            }

        } else {
            Uri.fromFile(saveFile)
        }
    }

    /**
     * 获取指定位置的fileUri
     *
     * @param context
     * @param file
     * @return
     */
    fun getFileUri(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(context, SPKey.FILE_PROVIDER_AUTHORITIES, file)
        } else {
            Uri.fromFile(file)
        }
    }

}
