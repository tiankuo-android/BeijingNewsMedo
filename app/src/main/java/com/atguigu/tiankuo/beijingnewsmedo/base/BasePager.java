package com.atguigu.tiankuo.beijingnewsmedo.base;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.atguigu.tiankuo.beijingnewsmedo.MainActivity;
import com.atguigu.tiankuo.beijingnewsmedo.R;

/**
 * 作者：田阔
 * 邮箱：1226147264@qq.com
 * Created by Administrator on 2017/6/2 0002.
 */

public class BasePager {

    public Context context;

    public View rootView;
    public TextView tv_title;
    public ImageButton ib_menu;

    public FrameLayout fl_content;

    public BasePager(final Context context){
        this.context = context;

        rootView = View.inflate(context, R.layout.base_pager,null);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        ib_menu = (ImageButton) rootView.findViewById(R.id.ib_menu);
        fl_content = (FrameLayout) rootView.findViewById(R.id.fl_content);

        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).getSlidingMenu().toggle();
            }
        });
    }

    public void initData(){

    }
}
