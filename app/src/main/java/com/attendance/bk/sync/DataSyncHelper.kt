package com.boss.bk.sync

import com.attendance.bk.BkApp
import com.attendance.bk.bus.SYNC_FAIL
import com.attendance.bk.bus.SYNC_OK
import com.attendance.bk.bus.SyncDataEvent
import com.attendance.bk.db.BkDb
import com.attendance.bk.sync.SyncJsonObject
import com.attendance.bk.utils.workerThreadChange
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.ZipUtils
import io.reactivex.Completable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chen xuJie on 2019/8/11.
 */
object DataSyncHelper {


    fun doSyncData(userId: String) {
        Completable.create {

            val lastVersion = BkDb.instance.syncDao().getLastVersionL(userId)

            val response = BkApp.apiService.syncData(userId,lastVersion).execute()
            val serverRes = if (response.isSuccessful) {
                try {
                    val stream = response.body()!!.bytes()
                    parseServerZipFile(stream)
                } finally {
                    response.body()?.close()
                }
            } else {
                SyncJsonObject(-1, "同步数据失败")
            }
            if (serverRes.code != -1) {
                // 合并数据
                if (BkDb.instance.dataSyncDao().mergeSyncJsonObject(userId,serverRes)) {
                    updateUserExtraVip()
                    BkApp.eventBus.post(SyncDataEvent(SYNC_OK))
                } else {
                    BkApp.eventBus.post(SyncDataEvent(SYNC_FAIL))
                }
            } else {
                ToastUtils.showShort(serverRes.desc)
                BkApp.eventBus.post(SyncDataEvent(SYNC_FAIL))
            }
        }.workerThreadChange().subscribe()
    }


    /**
     * 更新用户相关的信息
     */
    private fun updateUserExtraVip() {
//        val user = BkDb.instance.userDao().getCurrUser()
//        val userExtra = BkDb.instance.userExtraDao().getUserExtra(user.userId)
//        user.setUserExtra(userExtra)
//        val userVip = BkDb.instance.userVipDao().getUserVip(user.userId)
//        user.setUserVip(userVip)
//        BkApp.currUser = user
    }

    /**
     * 解析后端的zip流
     * @param bytes 后端返回的zip流
     * @param destFile 保存解析后的json文件到此文件中(方便调试)
     */
    private fun parseServerZipFile(bytes: ByteArray): SyncJsonObject {
        val destDir = BkApp.appContext.cacheDir
        // 将流写入到zip文件
        val serverZipFile = File(destDir, "serverSync.zip")
        FileIOUtils.writeFileFromBytesByStream(serverZipFile, bytes)


        // 清理解压文件夹
        val serverFileDir = File(destDir, "unZipSyncDir")
        if (!serverFileDir.isDirectory) {
            serverFileDir.mkdirs()
        } else { // 解压前删除之前的旧文件
            for (f in serverFileDir.listFiles()) {
                f.delete()
            }
        }
        // 解压缩
        ZipUtils.unzipFile(serverZipFile, serverFileDir)

        // 在解压缩目录找到json文件
        var serverJsonFile: File? = null
        val files = serverFileDir.listFiles()
        for (f in files) {
            if (f.isFile && f.name.endsWith(".json")) {
                serverJsonFile = f
                break
            }
        }
        if (serverJsonFile == null) {
            return SyncJsonObject(-1, "未找到同步文件")
        }

        val d = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(Date())
        val destFile = File(serverFileDir, "sync_$d.json")
        // json文件重命名为目标文件（方便调试）
        serverJsonFile.renameTo(destFile)

        return BkApp.bkJackson.readValue(destFile, SyncJsonObject::class.java)
    }

}