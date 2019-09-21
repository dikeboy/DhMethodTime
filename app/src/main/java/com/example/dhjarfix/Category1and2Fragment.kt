package com.example.dhjarfix

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment

/**
 * @author  wangjd;
 * @date 2019/7/18 0018
 * email jdwang@vova.com.hk;
 * description ; 一/二级分类页面(合并了)
 */
class Category1and2Fragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doTransaction()
    }
    /**
     * 旧打点
     * */
    private var isViewInitiated: Boolean = false
    private var isVisibleToUser: Boolean = false
    private var isDataInitiated: Boolean = false

    /**
     * 在父组件HomeNavFragment的位置
     * */
    private var tabPosition: Int = 0

    /**
     * 进入页面的默认route_sn
     * */
    private var routeSn: String = "0"

    /**
     * 头部未刷新
     * 头部只能刷新一次控制变量
     * */
    var unRefreshHeader: Boolean = true


    /**
     * 排序置顶商品
     * */
    var virtualGoodsIds = ""



    companion object {

        /**
         * @param categoryData
         */
        fun newInstance(
                        homeTabPosition: Int,
                        virtualGoodsIds:String?): Category1and2Fragment {
            val categoryFrag = Category1and2Fragment()
            val bundle = Bundle()
            categoryFrag.arguments = bundle
            return categoryFrag
        }
    }

     fun doTransaction() {
        setListener()
        //一些老的打点相关控制
        isViewInitiated = true
        prepareFetchData(false)
         setCustom2TopFunction {
             Log.e("lin","nihao")
         }
      var view = TextView(context)
         view.setOnClickListener {
             setCustom2TopFunction{
                 Log.e("lin","nihao")
             }
         }
         view.setOnClickListener(MyClickListerer(context!!){
//             setCustom2TopFunction {
//                 Log.e("lin","nihao")
//             }
             initTab()
         })
    }

    private fun initIntent() {

    }

    private fun initTab() {

    }

    private fun setListener() {

    }

    private fun TextView?.setDrawableEnd(@DrawableRes drawableResId: Int) {
        if (this == null) {
            return
        }


    }

    private fun prepareFetchData(forceUpdate: Boolean) {

    }


    override fun onResume() {
        super.onResume()
        //刷新收藏,老逻辑
    }

    fun setCustom2TopFunction(custom2Top: () -> Unit) {
        var name = "aaa"
        var len = name.length
        custom2Top.invoke()
    }

    class MyClickListerer(val context: Context,
                              var itemClick: ((Int) -> Unit)? = null
    ) : View.OnClickListener {
        override fun onClick(p0: View?) {
            itemClick?.invoke(1)
        }

          fun  getName(){
              var name = "aaa"
              var len = name.length
          }
    }
}