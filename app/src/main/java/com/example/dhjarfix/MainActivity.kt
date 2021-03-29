package com.example.dhjarfix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.vova.testlibrary.TestFile
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {
    var list = listOf<String>("aaa","bbb","ccc")
    companion object{
        fun   getName(){
            var name = "zhangshan11"
            name = name  + "aaa"
            return
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fillListRight()
        TestMyJava().name
//        var test = TestFile()
//        test.string
    }

    override fun getDelegate(): AppCompatDelegate {
        var name: String? = null
        return super.getDelegate()
    }


    interface NameInter{
        fun getName()
    }

    private fun fillListRight(rightEdge: Int, dx: Int) {
        while (rightEdge <6&&rightEdge>0) {
            var name = rightEdge*2
            name =  rightEdge/3
        }
    }
    private fun fillListRight() {
        list.forEach {
            Log.e("lin","list="+it);
            Thread.sleep(500)
        }
    }
    private fun fillListRight(rightEdge: Int, dx: Int,dy:Int):Int {
        var a = 5
        var b = 3
        while (rightEdge <6&&rightEdge>0) {
            var name = rightEdge*2
            name =  rightEdge/3
        }
        return a + b
    }

    private fun doPostDelayedTask() {
        window.decorView.postDelayed({
            if (!isFinishing) {
            }
        }, 1000)
    }


}
