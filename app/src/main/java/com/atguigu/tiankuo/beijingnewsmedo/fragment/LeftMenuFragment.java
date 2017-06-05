package com.atguigu.tiankuo.beijingnewsmedo.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.atguigu.tiankuo.beijingnewsmedo.MainActivity;
import com.atguigu.tiankuo.beijingnewsmedo.R;
import com.atguigu.tiankuo.beijingnewsmedo.base.BaseFragment;
import com.atguigu.tiankuo.beijingnewsmedo.domain.NewsCenterBean;
import com.atguigu.tiankuo.beijingnewsmedo.pager.NewsPager;

import java.util.List;

/**
 * 作者：田阔
 * 邮箱：1226147264@qq.com
 * Created by Administrator on 2017/6/2 0002.
 */

public class LeftMenuFragment extends BaseFragment {
    private List<NewsCenterBean.DataBean> datas;
    private ListView listView;
    private LeftMenuAdapter adapter;
    private int prePosition = 0;

    @Override
    public View initView() {
        listView = new ListView(context);
        listView.setPadding(0, 40, 0, 0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                prePosition = position;
                adapter.notifyDataSetChanged();

                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();

                switchPager(prePosition);
            }
        });
        return listView;
    }

    @Override
    public void initData() {
        super.initData();
    }

    public void setData(List<NewsCenterBean.DataBean> datas) {
        this.datas = datas;
        adapter = new LeftMenuAdapter();
        listView.setAdapter(adapter);
        switchPager(prePosition);

    }

    private void switchPager(int position) {
        MainActivity mainActivity = (MainActivity) context;
        //2.得到ContentFragment
        ContentFragment contentFragment = mainActivity.getContentFragment();
        //3.得到NewsPager
        NewsPager newsPager = contentFragment.getNewsPager();
        //4.调用切换方法
        newsPager.swichPager(position);
    }

    class LeftMenuAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu, null);

            if (prePosition == position) {
                textView.setEnabled(true);
            } else {
                textView.setEnabled(false);
            }
            NewsCenterBean.DataBean dataBean = datas.get(position);
            textView.setText(dataBean.getTitle());
            return textView;
        }
    }
}
