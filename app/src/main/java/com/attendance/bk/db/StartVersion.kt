package com.boss.bk.db

/**
 * mark the version that new field first add
 * Created by Chen xuJie on 2018/12/14/014
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class StartVersion(
        /**
         * the version that new field first add
         */
        val value: Double)
