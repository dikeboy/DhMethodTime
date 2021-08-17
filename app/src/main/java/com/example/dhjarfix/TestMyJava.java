package com.example.dhjarfix;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * @des: 类
 * @auth: ldh
 * @date: 2021/3/29 3:35 PM
 */
public class TestMyJava extends Fragment {
    public boolean isReturn = true;
    public int getName() throws InterruptedException {
        String name = "aaa";
        Thread.sleep(2000);
        AppCompatActivity activity = new AppCompatActivity();
        FragmentManager fm  =activity.getSupportFragmentManager();
        return 1111;
    }

    public int getData2()  {
        String name = "aaa";

        return 1111;
    }
    public int getData2(Context context)  {
        String name = "aaa";
        View view =new View(context);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String  name = "bbb";
            }
        });
        return 1111;
    }
    public void getData3()  {
        if(isReturn)
            return;
        String name = "aaa";
        String name2 = "aaaa";
    }
}
