package com.atguigu.tiankuo.beijingnewsmedo.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.tiankuo.beijingnewsmedo.base.BasePager;
import com.atguigu.tiankuo.beijingnewsmedo.fragment.LeftMenuFragment;
import com.atguigu.tiankuo.beijingnewsmedo.MainActivity;
import com.atguigu.tiankuo.beijingnewsmedo.base.MenuDetailBasePager;
import com.atguigu.tiankuo.beijingnewsmedo.domain.NewsCenterBean;
import com.atguigu.tiankuo.beijingnewsmedo.detailpager.NewsMenuDetailPager;
import com.atguigu.tiankuo.beijingnewsmedo.util.CacheUtils;
import com.atguigu.tiankuo.beijingnewsmedo.util.ConstantUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 作者：田阔
 * 邮箱：1226147264@qq.com
 * Created by Administrator on 2017/6/2 0002.
 */
public class NewsPager extends BasePager {

    private List<NewsCenterBean.DataBean> datas;
    private List<MenuDetailBasePager> basePagers;

    public NewsPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        //把数据绑定在视图上
        tv_title.setText("新闻");
        ib_menu.setVisibility(View.VISIBLE);

        //创建子类的视图
        TextView textView = new TextView(context);
//        textView.setText("新闻页面的内容");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);

        fl_content.addView(textView);

        String json = CacheUtils.getString(context, ConstantUtils.NEWSCENTER_PAGER_URL,null);
        if(!TextUtils.isEmpty(json)){
            processData(json);
        }
        getDataFromNet();
    }

    private void getDataFromNet() {
        //新闻中心的网络路径
        String url = ConstantUtils.NEWSCENTER_PAGER_URL;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("TAG", "请求失败==" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("TAG", "请求成功==" + response);
                        processData(response);

                        CacheUtils.putString(context,ConstantUtils.NEWSCENTER_PAGER_URL, response);
                        processData(response);
                    }
                });
    }

    private void processData(String json) {
//        NewsCenterBean newsCenterBean = new Gson().fromJson(json, NewsCenterBean.class);

        NewsCenterBean newsCenterBean = parseJson(json);

        Log.e("TAG", "解析成功了哦==" + newsCenterBean.getData().get(0).getChildren().get(0).getTitle());
        datas = newsCenterBean.getData();

        //传到左侧菜单
        MainActivity mainActivity = (MainActivity) context;

        basePagers = new ArrayList<>();
        basePagers.add(new NewsMenuDetailPager(context,datas.get(0).getChildren()));//新闻详情页面

        LeftMenuFragment leftMenuFragment = mainActivity.getLeftMenuFragment();
        leftMenuFragment.setData(datas);
    }

    private NewsCenterBean parseJson(String json) {
        NewsCenterBean newsCenterBean = new NewsCenterBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            //解析retcode
            int retcode = jsonObject.optInt("retcode");
            //设置数据
            newsCenterBean.setRetcode(retcode);

            JSONArray jsonArray = jsonObject.optJSONArray("data");
            //集合
            List<NewsCenterBean.DataBean> data = new ArrayList<>();
            newsCenterBean.setData(data);
            for (int i = 0; i < jsonArray.length(); i++) {

                //数据
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                if (jsonObject1 != null) {
                    NewsCenterBean.DataBean dataBean = new NewsCenterBean.DataBean();

                    dataBean.setId(jsonObject1.optInt("id"));
                    dataBean.setType(jsonObject1.optInt("type"));
                    String title = jsonObject1.optString("title");
                    dataBean.setTitle(title);
                    String url = jsonObject1.optString("url");
                    dataBean.setUrl(url);

                    JSONArray jsonArray1 = jsonObject1.optJSONArray("children");


                    if (jsonArray1 != null) {
                        List<NewsCenterBean.DataBean.ChildrenBean> children = new ArrayList<>();
                        //设置children数据的
                        dataBean.setChildren(children);
                        for (int i1 = 0; i1 < jsonArray1.length(); i1++) {

                            JSONObject jsonObject2 = jsonArray1.getJSONObject(i1);

                            NewsCenterBean.DataBean.ChildrenBean childrenBean = new NewsCenterBean.DataBean.ChildrenBean();

                            //解析数据了
                            childrenBean.setId(jsonObject2.optInt("id"));
                            childrenBean.setType(jsonObject2.optInt("type"));
                            childrenBean.setTitle(jsonObject2.optString("title"));
                            childrenBean.setUrl(jsonObject2.optString("url"));

                            //添加数据到集合中
                            children.add(childrenBean);
                        }
                    }
                    //添加到集合中
                    data.add(dataBean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsCenterBean;
    }

    public void swichPager(int prePosition) {
        MenuDetailBasePager basePager = basePagers.get(prePosition);//NewsMenuDetailPager,TopicMenuDetailPager...
        View rootView = basePager.rootView;
        fl_content.removeAllViews();//把之前显示的给移除
        fl_content.addView(rootView);
        //调用InitData
        basePager.initData();
    }

}
