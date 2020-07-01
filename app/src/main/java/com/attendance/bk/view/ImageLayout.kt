//package com.attendance.bk.view
//
//import android.content.Context
//import android.media.Image
//import android.util.AttributeSet
//import android.widget.ImageView
//import android.widget.LinearLayout
//import com.blankj.utilcode.util.ActivityUtils.startActivity
//import com.blankj.utilcode.util.ConvertUtils
//import com.blankj.utilcode.util.ToastUtils
//import com.attendance.bk.utils.ImageHelper
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.resource.bitmap.CenterCrop
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import java.util.*
//
///**
// * 流水图片的父容器，图片横向放置
// * Created by CXJ
// * Created date 2019/4/10/010
// */
//class ImageLayout : LinearLayout {
//
//
//    private val imageNameList = ArrayList<String>()
//
//    constructor(context: Context) : super(context) {
//        init()
//    }
//
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        init()
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        init()
//    }
//
//    private fun init() {
//        orientation = HORIZONTAL
//    }
//
//    fun updateImages(imageList: List<Image>?) {
//        if (childCount > 0) {
//            imageNameList.clear()
//            removeAllViews()
//        }
//
//        if (imageList == null || imageList.isEmpty()) {
//            return
//        }
//
//        for ((i, image) in imageList.withIndex()) {
//            val imageView = ImageView(context)
//            val lp = LayoutParams(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
//            lp.rightMargin = ConvertUtils.dp2px(9f)
//            lp.width = ConvertUtils.dp2px(45f)
//            lp.height = ConvertUtils.dp2px(45f)
//            imageView.layoutParams = lp
//            imageView.setImageResource(R.drawable.bg_image_holder)
//            addView(imageView, i)
//
//            val localFile = ImageHelper.getLocalImage(context, image.imageName)
//            if (localFile == null) {
//                BkApp.ossImageService.resize(image.imageName, lp.width, lp.height, object : OnImageCompletedCallback {
//                    override fun onSuccess(result: GetObjectResult) {
//                        val byteArray = IOUtils.readStreamAsBytesArray(result.objectContent)
//                        BkApp.mainHandler.post {
//                            Glide.with(context)
//                                    .load(byteArray)
//                                    .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(4f)))
//                                    .into(imageView)
//                        }
//                    }
//
//                    override fun onFailed() {
//                        ToastUtils.showShort("加载图片失败")
//                    }
//                })
//
//            } else {
//                Glide.with(context)
//                        .load(localFile)
//                        .placeholder(R.drawable.bg_image_holder)
//                        .transform(CenterCrop(), RoundedCorners(ConvertUtils.dp2px(4f)))
//                        .into(imageView)
//            }
//            imageNameList.add(image.imageName)
//        }
//
//
//        for (i in 0 until childCount) {
//            val imageView = getChildAt(i) as ImageView
//            imageView.setOnClickListener { startActivity(ImageActivity.startIntent(imageNameList, i)) }
//        }
//    }
//}
