package cn.sh.smg.motianlun.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.sh.smg.motianlun.R;

//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;

/**
 * ViewPager实现的轮播图广告自定义视图，
 * 既支持自动轮播页面也支持手势滑动切换页面
 * Created by jl_luo on 2015/7/9.
 */
public class SlideShowView extends FrameLayout implements Handler.Callback{
    //自动轮播的时间间隔
    private final static int TIME_INTERVAL = 5;
    private final static int MARGIN_IMG = 4;
    //图片数量
//    private int imgCount;
    //自动轮播启用开关
    private final static boolean isAutoPlay = false;
    //自定义轮播图的资源
    private String[] imgUrls;
    //放轮播图片的ImageView 的list
    private ArrayList<ImageView> imgViewsList;
    //放圆点的View的list
    private ArrayList<View> dotViewsList;
    private ViewPager viewPager;
    //当前轮播页
    private int currentItem  = 0;

    //定时任务
    private ScheduledExecutorService scheduledExecutorService;

    private Context context;
    private Handler handler;
//    private RequestQueue mQueue;
//    private StringRequest stringRequest;


    public SlideShowView(Context context) {
        this(context, null);
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.handler = new Handler(this);
//        mQueue = Volley.newRequestQueue(context);
        initData();
        if(isAutoPlay){
            startPlay();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case 0:
                viewPager.setCurrentItem(currentItem);
                break;
        }
        return false;
    }
    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
    }
    /**
     * 停止轮播图切换
     */
    private void stopPlay(){
        scheduledExecutorService.shutdown();
    }

    /**
     * 初始化相关Data
     */
    private void initData(){
        imgViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();

        // 一步任务获取图片
        getList();
    }



    private void getList(){
//        String url = Constant.CONNECT_HOST_API+Constant.HOME_API;
//        Log.w("Tt","url="+url+"");
//        stringRequest = new StringRequest(url , new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try{
//                    Log.w("Tt",response);
//                    if(null!=response){
//                        JSONObject res = new JSONObject(response);
//                        if(res.optInt("statuscode")==1){
//                            JSONObject data = res.optJSONObject("data");
//                            JSONArray array = data.optJSONArray("top_events");
//                            int length = array.length();
//                            imgUrls = new String[length];
//                            for(int i=0;i<length;i++){
//                                HuoDong h = new HuoDong(array.getJSONObject(i));
//                                imgUrls[i] = h.gallaryid;
//                            }
//                        }
//                    }
//                    if (imgUrls!=null) {
//                        initUI(context);
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//        mQueue.add(stringRequest);
    }

    /**
     * 初始化Views等UI
     */
    private void initUI(Context context){
        if(imgUrls == null || imgUrls.length == 0)
            return;

        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);

        LinearLayout dotLayout = (LinearLayout)findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();

        // 热点个数与图片特殊相等
        for (int i = 0; i < imgUrls.length; i++) {
            ImageView view =  new ImageView(context);
            view.setTag(imgUrls[i]);
            if(i==0)//给一个默认图
                view.setBackgroundResource(R.drawable.atlas_default_pic);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            imgViewsList.add(view);

            ImageView dotView =  new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            params.leftMargin = MARGIN_IMG;
            params.rightMargin = MARGIN_IMG;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);

        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    /**
     * 填充ViewPager的页面适配器
     *
     */
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(imgViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageView imageView = imgViewsList.get(position);

//            imageLoader.displayImage(imageView.getTag() + "", imageView);
            Glide.with(context).load(imageView.getTag()+"")
            .centerCrop()
            .crossFade()
            .into(imageView);

            ((ViewPager) container).addView(imgViewsList.get(position));
            return imgViewsList.get(position);
        }

        @Override
        public int getCount() {
            return imgViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     *
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {

            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    ((View)dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_focus);
                }else {
                    ((View)dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_blur);
                }
            }
        }

    }
    /**
     *执行轮播图切换任务
     *
     */
    private class SlideShowTask implements Runnable{

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem+1)%imgViewsList.size();
                handler.obtainMessage(1).sendToTarget();
            }
        }

    }

    /**
     * 销毁ImageView资源，回收内存
     *
     */
    private void destoryBitmaps() {
        int length = imgViewsList.size();
        for (int i = 0; i < length; i++) {
            ImageView imageView = imgViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                drawable.setCallback(null);
            }
        }
    }
}
