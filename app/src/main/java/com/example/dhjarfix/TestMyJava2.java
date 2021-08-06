package com.example.dhjarfix;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * @des: 类
 * @auth: ldh
 * @date: 2021/3/29 3:35 PM
 */
public class TestMyJava2 extends Fragment {
    public boolean isReturn = true;
    public TestMyJava2(AppCompatActivity activity) {
        FragmentManager fm  =activity.getSupportFragmentManager();
        fm.getPrimaryNavigationFragment();
        String name = "222";
    }

    public int getData2()  {
        String name = "aaa";
        return 1111;
    }

    public void getData3()  {
        if(isReturn)
            return;
        String name = "aaa";
        String name2 = "aaaa";
    }
}
