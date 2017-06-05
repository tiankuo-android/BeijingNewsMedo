package com.atguigu.tiankuo.beijingnewsmedo.base;

import android.content.Context;
import android.view.View;

/**
 * 作者：田阔
 * 邮箱：1226147264@qq.com
 * Created by Administrator on 2017/6/3 0003.
 */

public abstract class MenuDetailBasePager {

    public final Context context;
    public View rootView;

    public MenuDetailBasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    public abstract View initView();

    public void initData(){

    }
}
