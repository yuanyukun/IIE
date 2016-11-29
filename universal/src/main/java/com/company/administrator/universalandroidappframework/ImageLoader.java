package com.company.administrator.universalandroidappframework;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2016/11/27.
 * 图片加载类
 */

public class ImageLoader {
    private static ImageLoader mInstance;

    /**
     * 图片的核心对象
     */
    private LruCache<String,Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    private static  final  int DEAFAULT_THREAD_COUNT = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    private Handler mUIHandler;

    private Semaphore mPoolThreadSemaphore = new Semaphore(0);
    private Semaphore mThreadSemaphore ;

    public enum  Type{
        FIFO,LIFO;
    }
    private  ImageLoader(int threadCount,Type type) {
        init(threadCount,type);
    }

    /**
     * 初始化
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        //后台轮询线程
        mPoolThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池去取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mThreadSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mPoolThreadSemaphore.release();
                Looper.loop();
            }
        });
        mPoolThread.start();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMeomory = maxMemory / 8;
        mLruCache = new LruCache<String,Bitmap>(cacheMeomory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mTaskQueue = new LinkedList<>();
        mType = type;

        mThreadSemaphore = new Semaphore(threadCount);
    }

    private Runnable getTask() {
        if(mType == Type.FIFO){
            return mTaskQueue.removeFirst();
        }else if(mType == Type.LIFO){
            return  mTaskQueue.removeLast();
        }
        return  null;
    }

    public static ImageLoader getInstance(){
        if(mInstance == null){
            synchronized (ImageLoader.class){
                if(mInstance == null){
                    mInstance = new ImageLoader(DEAFAULT_THREAD_COUNT,Type.LIFO);
                }
            }
        }
        return  mInstance;
    }

    public void loadImage(final String path, final ImageView imageView){
        imageView.setTag(path);
        if(mUIHandler == null){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //获取的到图片，为ImageView设置图片
                    ImageHolder imageHolder = (ImageHolder) msg.obj;
                    Bitmap bm = imageHolder.bitmap;
                    ImageView imageView = imageHolder.imageView;
                    String path = imageHolder.path;
                    //防止图片设置错乱
                    if(imageView.getTag().toString().equals(path)){
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }
        Bitmap bm = getBitmapFromLruCache(path);
        if(bm != null){
            refreshBitmap(path, imageView, bm);
        }else{
            addTasks(new Runnable() {
                @Override
                public void run() {
                    //加载图片，图片压缩，
                    //1.获取图片的宽高
                    ImageSize imageSize = getImageViewSize(imageView);
                    //2.压缩图片
                    Bitmap bm = decodeSampleBitmapFromPath(path,imageSize.width,imageSize.height);
                    //3.把图片加载到缓存
                    addBitmapToLruCache(path,bm);

                    refreshBitmap(path,imageView,bm);

                    mThreadSemaphore.release();

                }
            });
        }

    }

    private void refreshBitmap(String path, ImageView imageView, Bitmap bm) {
        Message msg = Message.obtain();
        ImageHolder holder = new ImageHolder(bm,imageView,path);
        msg.obj = holder;
        mUIHandler.sendMessage(msg);
    }

    /**
     * 把图片加入缓存
     * @param path
     * @param bm
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {
        if(getBitmapFromLruCache(path) == null){
            if(bm != null){
                mLruCache.put(path,bm);
            }
        }
    }

    private Bitmap decodeSampleBitmapFromPath(String path, int width, int height) {
        //获取图片的宽和高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//获取到图片的宽和高不加载到内存
        BitmapFactory.decodeFile(path,options);
        options.inSampleSize = caculateInSampleSize(options,width,height);
        //使用获取到的inSampleSize再次解析图片,图片加载到内存
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        return bitmap;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if(width > reqWidth || height > reqHeight){
            int widthRadio = Math.round(width*1.0f/reqHeight);
            int heightRadio =  Math.round(height*1.0f/reqHeight);
            //得到的图片越小越好
            inSampleSize = Math.max(widthRadio,heightRadio);
        }

        return inSampleSize;
    }

    private ImageSize getImageViewSize(ImageView imageView) {
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ImageSize imageSize = new ImageSize();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        //获取imageView的实际宽度
        int width = imageView.getWidth();
        if(width <= 0){
            width = lp.width;//获取imageView 在layout中声明的宽度
        }
        if(width <= 0){
            width = imageView.getMaxWidth();//检查最大值
        }
        if(width <= 0){
            width = displayMetrics.widthPixels;
        }
        //获取imageView的实际高度
        int height = imageView.getWidth();
        if(height <= 0){
            width = lp.height;//获取imageView 在layout中声明的宽度
        }
        if(height <= 0){
            height = imageView.getMaxHeight();//检查最大值
        }
        if(height <= 0){
            height = displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    private synchronized void addTasks(Runnable runnable) {
        mTaskQueue.add(runnable);
        //if(mPoolThreadHandler == null) wait;
        try {
            if(mPoolThreadHandler == null) {
                mPoolThreadSemaphore.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    /**
     * 根据path获取缓存中的图片
      * @param path
     * @return
     */
    private Bitmap getBitmapFromLruCache(String path) {

        return mLruCache.get(path);
    }
    private class ImageHolder{

        public ImageHolder(Bitmap bitmap, ImageView imageView, String path) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.path = path;
        }

        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
    private class ImageSize{
        int width;
        int height;
    }

}
