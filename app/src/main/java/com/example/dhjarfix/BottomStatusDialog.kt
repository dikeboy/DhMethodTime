package com.example.dhjarfix

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer


/**
 *
 * @des:     类
 * @auth:         ldh
 * @date:     2020/10/28 3:41 PM
 */
 abstract  class BottomStatusDialog:DialogFragment(){
    private var loadingView: View? = null

    private var progressView: View? = null

    private var emptyView: View? = null

    var contentView: FrameLayout? = null

    @get:LayoutRes
    protected abstract val layoutId: Int


    private var errorDataView
            : View? = null

    override fun onDestroy() {
        super.onDestroy()
        if (activity?.isChangingConfigurations != true) {
            viewModelStore?.clear()
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var mainView = inflater.inflate(layoutId, container, false)
        return mainView
    }

    abstract  fun getMainView(view:View):FrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentView = getMainView(view)
    }

    fun showLoadingView() {
        if (isViewDestroyed())
            return
        contentView?.visibility = View.VISIBLE
        hideErrorDataView()
        contentView?.apply {
            if (loadingView != null) {
                removeView(loadingView)
            }
            loadingView = LayoutInflater.from(activity).inflate(R.layout.activity_main, null)
            loadingView?.let { lv ->
                contentView?.addView(lv, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                lv.setOnClickListener(View.OnClickListener { })

                var topMargin =100

                (lv.layoutParams as? FrameLayout.LayoutParams)?.let { param ->
                    param.topMargin = topMargin
                    lv.layoutParams = param
                }
            }


        }


    }

    fun showProgressView() {
        if (isViewDestroyed())
            return
        hideErrorDataView()
        contentView?.apply {
            if (loadingView != null) {
                removeView(loadingView)
            }
            progressView = LayoutInflater.from(activity).inflate(R.layout.activity_main, null)
            progressView?.let { lv ->
                contentView?.addView(lv, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                lv.setOnClickListener(View.OnClickListener { })

                var topMargin = 100

                (lv.layoutParams as? FrameLayout.LayoutParams)?.let { param ->
                    param.topMargin = topMargin
                    lv.layoutParams = param
                }
            }
        }
    }

    fun showEmptyLayout(text:String?) {
        if (isViewDestroyed())
            return
        contentView?.visibility = View.VISIBLE
        contentView?.apply {
            if (emptyView != null) {
                removeView(emptyView)
            }
            emptyView = LayoutInflater.from(activity).inflate(R.layout.activity_main, null)
            emptyView?.let { lv ->

            }
        }
    }

    fun hideEmptyView() {
        if (isViewDestroyed()||emptyView==null)
            return
        contentView?.visibility = View.GONE
        contentView?.removeView(emptyView)
        emptyView = null
    }

    fun hideProgressView() {
        if (isViewDestroyed()||progressView==null)
            return
        contentView?.removeView(progressView)
        progressView = null
    }
    fun hideLoadingView() {
        if (isViewDestroyed()||loadingView==null)
            return
        hideErrorDataView()
        contentView?.visibility = View.GONE
        contentView?.removeView(loadingView)
        loadingView = null
    }

    fun showErrorView() {
        if (isViewDestroyed())
            return
        hideLoadingView()
        contentView?.visibility = View.VISIBLE
        if (errorDataView != null) {
            contentView?.removeView(errorDataView)
        }
        errorDataView = LayoutInflater.from(activity).inflate(R.layout.activity_main, null)
        contentView?.addView(errorDataView)
        errorDataView?.setOnClickListener(View.OnClickListener {
            showLoadingView()
            onErrorClick()
        })

    }

    private fun hideErrorDataView() {
        if (isViewDestroyed()||errorDataView==null)
            return
        contentView?.visibility = View.GONE
        if (errorDataView != null) {
            contentView?.removeView(errorDataView)
            errorDataView = null
        }
    }


    abstract fun onErrorClick()

    private fun isViewDestroyed(): Boolean {
        if (contentView == null || activity == null || activity?.isFinishing == true)
            return true
        return false
    }

    open fun getLoadingViewTopPadding(): Int {
        return 0
    }


}