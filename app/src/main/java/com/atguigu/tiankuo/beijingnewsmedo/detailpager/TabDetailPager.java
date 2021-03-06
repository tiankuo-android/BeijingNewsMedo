package com.atguigu.tiankuo.beijingnewsmedo.detailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.tiankuo.beijingnewsmedo.R;
import com.atguigu.tiankuo.beijingnewsmedo.base.MenuDetailBasePager;
import com.atguigu.tiankuo.beijingnewsmedo.domain.NewsCenterBean;
import com.atguigu.tiankuo.beijingnewsmedo.domain.TabDetailPagerBean;
import com.atguigu.tiankuo.beijingnewsmedo.util.ConstantUtils;
import com.atguigu.tiankuo.beijingnewsmedo.util.DensityUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 作者：田阔
 * 邮箱：1226147264@qq.com
 * Created by Administrator on 2017/6/5 0005.
 */

public class TabDetailPager extends MenuDetailBasePager {
    private final NewsCenterBean.DataBean.ChildrenBean childrenBean;
    private String url;
    private int prePosition = 0;
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;
    private List<TabDetailPagerBean.DataBean.NewsBean> newsBeanList;
    private ListAdapter adapter;
    private boolean isLoadingMore = false;

    HorizontalScrollViewPager viewpager;
    TextView tvTitle;
    LinearLayout llPointGroup;

    @InjectView(R.id.pull_refresh_list)
    PullToRefreshListView pull_refresh_list;

    ListView lv;
    private String moreUrl ;
    private ListBeanAdapter adapterlist;

    public TabDetailPager(Context context, NewsCenterBean.DataBean.ChildrenBean childrenBean) {
        super(context);
        this.childrenBean = childrenBean;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_tab_detail, null);
        ButterKnife.inject(this, view);
        lv = pull_refresh_list.getRefreshableView();

        View viewTopNews = View.inflate(context, R.layout.tab_detail_topnews, null);
        viewpager = (HorizontalScrollViewPager) viewTopNews.findViewById(R.id.viewpager);
        tvTitle = (TextView) viewTopNews.findViewById(R.id.tv_title);
        llPointGroup = (LinearLayout) viewTopNews.findViewById(R.id.ll_point_group);
        //把顶部部分以添加头的方式添加到ListVie
        lv.addHeaderView(viewTopNews);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //把之前的设置默认
                llPointGroup.getChildAt(prePosition).setEnabled(false);
                //当前的设置true
                llPointGroup.getChildAt(position).setEnabled(true);
                //记录当前值
                prePosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                String title = topnews.get(position).getTitle();
                tvTitle.setText(title);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pull_refresh_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isLoadingMore = false;
                getDataFromNet(url);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if(!TextUtils.isEmpty(moreUrl)) {
                    isLoadingMore = true;
                    getDataFromNet(moreUrl);
                }else{
                    Toast.makeText(context, "没有新数据可添加...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;

    }

    @Override
    public void initData() {
        super.initData();
        url = ConstantUtils.BASE_URL + childrenBean.getUrl();
        Log.e("TAG", "url==" + url);
        getDataFromNet(url);
    }

    private void getDataFromNet(String url) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        processData(response);
                        pull_refresh_list.onRefreshComplete();
                    }
                });
    }

    private void processData(String response) {
        TabDetailPagerBean bean = new Gson().fromJson(response, TabDetailPagerBean.class);
        String more = bean.getData().getMore();
        if(!TextUtils.isEmpty(more)) {
            moreUrl = ConstantUtils.BASE_URL + more;
        }
        if(!isLoadingMore) {
            topnews = bean.getData().getTopnews();
            viewpager.setAdapter(new MyPagerAdapter());

            Log.e("TAG", "" + bean.getData().getNews().get(0).getTitle());

            tvTitle.setText(topnews.get(0).getTitle());

            llPointGroup.removeAllViews();

            for (int i = 0; i < topnews.size(); i++) {
                ImageView point = new ImageView(context);
                point.setBackgroundResource(R.drawable.point_selector);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 8), DensityUtil.dip2px(context, 8));
                point.setLayoutParams(params);

                if (i == 0) {
                    point.setEnabled(true);
                } else {
                    point.setEnabled(false);
                    params.leftMargin = 8;
                }
                llPointGroup.addView(point);
            }
            newsBeanList = bean.getData().getNews();
            adapterlist = new ListBeanAdapter();
            lv.setAdapter(adapterlist);
        }else{
            newsBeanList.addAll(bean.getData().getNews());
            adapterlist.notifyDataSetChanged();
        }
    }

    class ListBeanAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return newsBeanList.size();
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_tab_detail, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TabDetailPagerBean.DataBean.NewsBean newsBean = newsBeanList.get(position);
            viewHolder.tvDesc.setText(newsBean.getTitle());
            viewHolder.tvTime.setText(newsBean.getPubdate());

            String imageUrl = ConstantUtils.BASE_URL + newsBean.getListimage();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.pic_item_list_default)
                    .error(R.drawable.pic_item_list_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivIcon);
            return convertView;
        }
    }

    static class ViewHolder {
        @InjectView(R.id.iv_icon)
        ImageView ivIcon;
        @InjectView(R.id.tv_desc)
        TextView tvDesc;
        @InjectView(R.id.tv_time_data)
        TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return topnews.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.pic_item_list_default);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            String imageUrl = ConstantUtils.BASE_URL + topnews.get(position).getTopimage();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.pic_item_list_default)
                    .error(R.drawable.pic_item_list_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            container.addView(imageView);
            return imageView;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


}
