package com.shjeong.logintest

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo





class MyAccessibilityService : AccessibilityService() {

    private var mDebugDepth = 0
    private var mNodeInfo : AccessibilityNodeInfo? = null

    override fun onInterrupt() {
        Log.d("service!@#","onInterrupt")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d("service!@#","onAccessibilityEvent")
        Log.d("service!@#", String.format(
            "onAccessibilityEvent: type = [ ${event!!.eventType} ], class = [ ${event.className} ], package = [ ${event.packageName} ], time = [ ${event.eventTime} ], text = [ ${event.text} ]"))

        mDebugDepth = 0
        mNodeInfo = event.source
        printAllViews(mNodeInfo)

    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        Log.d("service!@#","onServiceConnected")

        val info = AccessibilityServiceInfo()
        info.flags = AccessibilityServiceInfo.DEFAULT
        info.packageNames = arrayOf("com.shjeong.logintest")
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        serviceInfo = info
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service!@#","onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("service!@#","onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("service!@#","onDestroy")
    }

    private fun printAllViews(mNodeInfo: AccessibilityNodeInfo?) {
        if (mNodeInfo == null) return
        var log = ""
        for (i in 0 until mDebugDepth) {
            log += "."
        }
        log += "(" + mNodeInfo.text + " <-- " +
                mNodeInfo.viewIdResourceName + ")"
        Log.d("service!@#", log)
        if (mNodeInfo.childCount < 1) return
        mDebugDepth++

        for (i in 0 until mNodeInfo.childCount) {
            printAllViews(mNodeInfo.getChild(i))
        }
        mDebugDepth--
    }
}
