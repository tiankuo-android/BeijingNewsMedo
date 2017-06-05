package com.atguigu.tiankuo.beijingnewsmedo.fragment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.atguigu.tiankuo.beijingnewsmedo.MainActivity;
import com.atguigu.tiankuo.beijingnewsmedo.NoViewPager;
import com.atguigu.tiankuo.beijingnewsmedo.R;
import com.atguigu.tiankuo.beijingnewsmedo.base.BaseFragment;
import com.atguigu.tiankuo.beijingnewsmedo.base.BasePager;
import com.atguigu.tiankuo.beijingnewsmedo.pager.NewsPager;
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者：田阔
 * 邮箱：1226147264@qq.com
 * Created by Administrator on 2017/6/2 0002.
 */

public class ContentFragment extends BaseFragment {

    @InjectView(R.id.vp)
    NoViewPager vp;
    @InjectView(R.id.rg_main)
    RadioGroup rgMain;

    private ArrayList<BasePager> pagers;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_content, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        pagers = new ArrayList<>();
        pagers.add(new NewsPager(context));

        vp.setAdapter(new MyAdapter());



        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_news :
                        vp.setCurrentItem(1,false);
//                        pagers.get(1).initData();
                        break;
                }
            }
        });

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagers.get(position).initData();
                if(position == 1) {
                    isEnableSlidingMenu(context, SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else{
                    isEnableSlidingMenu((MainActivity)context, SlidingMenu.TOUCHMODE_NONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pagers.get(0).initData();
        //默认选中主页
        rgMain.check(R.id.rb_home);
        isEnableSlidingMenu((MainActivity)context, SlidingMenu.TOUCHMODE_NONE);
    }

    private static void isEnableSlidingMenu(Context context, int touchmodeFullscreen) {
        MainActivity mainactivity = (MainActivity) context;
        mainactivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }


    public NewsPager getNewsPager() {
        return (NewsPager) pagers.get(0);
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager basePager = pagers.get(position);
            View rootView = basePager.rootView;
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return pagers.size();

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
