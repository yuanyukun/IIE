package com.horem.parachute.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.horem.parachute.common.CustomApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by kenny on 2015/09/06.
 */
public class BitmapUtils {

    public static final int QUALITY = 100;
    /**
     * 图片的最大边长
     */
    public static final int MAX_IMG_SIZE;
    /**
     * 图片比例
     */
    private static final float IMG_RADIO = 1.778f;

    static {
        Context context = CustomApplication.getInstance();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        Log.i("手机屏幕尺寸：","width ==>"+screenWidth+" height==> "+screenHeight);

        int targetSize = Math.max(screenWidth, screenHeight);
        if (targetSize >= 1280) {
            targetSize = 1280;
        } else if (targetSize >= 800) {
            targetSize = 960;
        } else {
            targetSize = 640;
        }

        MAX_IMG_SIZE = targetSize;
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap = null;
             int w = drawable.getIntrinsicWidth();
             int h = drawable.getIntrinsicHeight();
             System.out.println("Drawable转Bitmap");
             Bitmap.Config config =
                         drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                             : Bitmap.Config.RGB_565;
              bitmap = Bitmap.createBitmap(w,h,config);
              //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
              Canvas canvas = new Canvas(bitmap);
              drawable.setBounds(0, 0, w, h);
              drawable.draw(canvas);
        return bitmap;
  }



    public static Bitmap ResizeBitmap(Context context,Bitmap srcBitmap){

        float scaleRate = 1.0f;
        int minLength;
        Bitmap resizeBitmap = null;
//        float scaleHeight = 0.5f;
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        Log.i("获取照相机图片原始尺寸：","width ==>"+srcWidth+" height==> "+srcHeight);
        int maxLength = Math.max(srcWidth,srcHeight);

        if(maxLength >1920){
            scaleRate = 1920f/maxLength;
            resizeBitmap = ChangeBitmapSize(scaleRate,srcBitmap);

            minLength = Math.min(resizeBitmap.getWidth(),resizeBitmap.getHeight());
            if(minLength > 1080){
                scaleRate = 1080f/minLength;
                Bitmap newBmp2 = ChangeBitmapSize(scaleRate,resizeBitmap);

                return newBmp2;
            }else{
                return resizeBitmap;
            }
        }

        if(scaleRate == 1.0f){
            minLength = Math.min(srcWidth,srcHeight);
            if(minLength > 1080){
                scaleRate = 1080f/minLength;
                resizeBitmap = ChangeBitmapSize(scaleRate,srcBitmap);
                return resizeBitmap;
            }else{
                return srcBitmap;
            }
        }
        return null;
    }
    public static Bitmap ChangeBitmapSize(float ScaleRatio,Bitmap srcBitmap){
        Matrix matrix = new Matrix();
        matrix.postScale(ScaleRatio,ScaleRatio);
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        Bitmap newBmp = Bitmap.createBitmap(srcBitmap,0,0,
                width,height,matrix,true);
//        if(!srcBitmap.isRecycled()){
//            srcBitmap.recycle();
//            srcBitmap = null;
//        }
        return newBmp;
    }
    /**
     * 保存处理过的图片
     * @param context
     * @param uri
     * @param limitSize
     * @return
     */
    public static File saveResizeBitmap2File(Context context, Uri uri, int limitSize) {
        return saveRotateBitmap2File(context, uri, limitSize, 0);
    }

    /**
     * 旋转图片方正保存
     * @param context
     * @param uri
     * @param limitSize
     * @param degree
     * @return
     */
    public static File saveRotateBitmap2File(Context context, Uri uri, int limitSize, int degree) {

        Bitmap bmp = tryLoadImageInSafeSize(context, uri, limitSize);
        if(bmp == null) {
            return null;
        }

        if (degree > 0) {
            bmp = rotateBitmap(bmp, degree);
            if(bmp == null) {
                return null;
            }
        }

        Bitmap newBitmap = scaleBitmap2Limit(bmp, limitSize);
        if (newBitmap == null) {
            return null;
        }

        File outFile = createTmpImage(context);

        int height = newBitmap.getHeight();
        int width = newBitmap.getWidth();
        boolean isOk = saveBitmap2File(newBitmap, outFile);

        if (!bmp.isRecycled()) {
            bmp.recycle();
        }

        if (!newBitmap.isRecycled()) {
            newBitmap.recycle();
        }

        if (isOk) {
            StringBuilder builder = new StringBuilder()
                    .append(outFile.getAbsolutePath())
                    .append("_")
                    .append(width).append("x").append(height);

            String newFilePath = builder.toString();  //在原来的文件路径后面补全图片的长宽信息, 格式：_960*500
            File newFile = new File(newFilePath);

            if (outFile.renameTo(newFile)) {
                return newFile;
            }
            return outFile;
        }
        return null;
    }

    /**
     * 将bitmap压缩保存到文件，压缩质量
     * @param srcBitmap
     * @param file
     * @return
     */
    public static boolean saveBitmap2File(Bitmap srcBitmap, File file) {
        return saveBitmap2File(srcBitmap, file, QUALITY, true);
    }

    /**
     * 保存Bitmap对象为到文件
     * @param srcBitmap
     * @param file
     * @param quality 0.5
     * @param recycleOrig
     * @return
     */
    public static boolean saveBitmap2File(Bitmap srcBitmap, File file, int quality, boolean recycleOrig) {
        if (srcBitmap == null || file == null) {
            return false;
        }

        if (file.exists()) {
            file.delete();
        }
        //默认设置成50
        if (quality < 0) {
            quality = 0;

        } else if (quality > 100) {
            quality = 100;
        }

        boolean saveOk = true;
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();

        } catch (Exception e) {
            saveOk = false;
            e.printStackTrace();
        } finally {
            IOUtils.safeClose(bos);
        }

//        if (recycleOrig && !srcBitmap.isRecycled()) {
//            srcBitmap.recycle();
//        }

        return saveOk;
    }

    /**
     * The rule is to scale the bitmap to fit the width of the rect, if the height
     * exceeds that of the rect, crop the scaled bitmap at the center
     *压缩图片是图片适合给出矩形的宽度，如果压缩后的图片高度大于矩形的高度，从图片的中间裁剪以适应矩形
     * @param bitmap the original bitmap
     * @param rect the target rect that the scaled bitmap will fit
     * @param recycleOrig if true, recycle bitmap when done
     * @return the scaled bitmap or the original bitmap if OutOfMemoryError occurs
     */
    public static Bitmap scaleBitmapToFitRect(Bitmap bitmap, Rect rect, boolean recycleOrig) {
        try {
            Bitmap newBitmap1 = null;

            if (rect.width() != bitmap.getWidth()) {
                float scale = (float)rect.width() / bitmap.getWidth();
                int height = (int)(bitmap.getHeight() * scale);
                newBitmap1 = Bitmap.createScaledBitmap(bitmap, rect.width(), height, true);

                if (recycleOrig && !bitmap.isRecycled())
                    bitmap.recycle();
            }

            if (newBitmap1 != null)
                bitmap = newBitmap1;

            // 按宽度拉伸后，高度超出screenHeight，所以要把超出的部分截取掉
            if (bitmap.getHeight() > rect.height()) {
                int startY = (bitmap.getHeight() - rect.height()) / 2;
                Bitmap newBitmap2 = Bitmap.createBitmap(bitmap, 0, startY, rect.width(), rect.height());

                if (newBitmap1 != null && !newBitmap1.isRecycled())
                    newBitmap1.recycle();

                else if (recycleOrig && !bitmap.isRecycled())
                    bitmap.recycle();

                return newBitmap2;
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 在程序对应的外部存储器的image文件夹下创建临时文件
     * @param context
     * @return
     */
    public static File createTmpImage(Context context) {
        String cacheDir = SdcardUtils.createTmpSubFolder(context, "伞来了").getAbsolutePath();
        File file = new File(cacheDir + File.separator + System.currentTimeMillis() + System.nanoTime()+".jpg");
        return file;
    }
    public static String getCurDataStr(){

        String tmpImage =  System.currentTimeMillis() + System.nanoTime()+".jpg";

        return tmpImage;
    }
    /**
     * 获取文件目录下所有文件的总大小
     * @param file
     * @return
     */
    public static double getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

    /**
     * 对Bitmap对象按其最大的边长进行缩放max1920
     * @param srcBitmap
     * @param maxSize
     * @return 如果没有任何缩放，则返回原来的Bitmap对象<br/>
     *         如果出现OOM错误，返回null
     */
    public static Bitmap scaleBitmap2Limit(Bitmap srcBitmap, final int maxSize) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();
        float factor = 1;
        if (width > height) {
            if (width > maxSize) {
                factor = (float) maxSize / width;
                width = maxSize;
                height = (int) (height * factor);
            }
        } else {
            if (height > maxSize) {
                factor = (float) maxSize / height;
                height = maxSize;
                width = (int) (width * factor);
            }
        }

        if (factor == 1) {
            return srcBitmap;
        }

        Bitmap targetBitmap = null;
        try {
            targetBitmap = Bitmap.createBitmap(width, height, srcBitmap.getConfig());
            Canvas canvas = new Canvas(targetBitmap);
            canvas.scale(factor, factor);
            canvas.drawBitmap(srcBitmap, 0, 0, null);
        } catch (OutOfMemoryError e) {
            //do nothing
        }

        return targetBitmap;
    }

    /**
     * 旋转Bitmap
     * @param src
     * @param degree
     * @return 如果出现OOM错误，返回null
     */
    public static Bitmap rotateBitmap(Bitmap src, float degree) {

        return rotateBitmap(src, degree, true);
    }

    /**
     * 旋转图片
     * @param src
     * @param degree
     * @param recycle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap src, float degree, boolean recycle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
            if (recycle && src != null) {
                src.recycle();
                System.gc();
            }
        } catch (OutOfMemoryError e) {
            //do nothing
            while(bitmap == null) {
                System.gc();
                System.runFinalization();
                bitmap = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
            }
        }

        return bitmap;
    }

    /**
     * 采用限制图片大小的方式来加载图片
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap tryLoadImageInSafeSize(Context context, Uri uri, int limitSize) {
        InputStream is = null;
        Bitmap bitmap = null;

        try {
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options opts = loadBitmapOptions(context, uri);
            if (opts != null) {
                int limitHeight =  Math.round(limitSize / IMG_RADIO);
                opts.inSampleSize = calculateInSampleSize(opts, limitSize, limitHeight);
                bitmap = BitmapFactory.decodeStream(is, null, opts);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } catch (OutOfMemoryError e) {
            //do nothing
        } finally {
            IOUtils.safeClose(is);
        }

        return bitmap;
    }

    /**
     * 计算inSampleSize 采样率
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.取两者最小的比率
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 读取指定Uri Bitmap的属性
     * @param context
     * @param uri
     * @return
     */
    public static BitmapFactory.Options loadBitmapOptions(Context context, Uri uri) {
        BitmapFactory.Options opts = null;
        InputStream is = null;

        try {
            is = context.getContentResolver().openInputStream(uri);
            opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opts);
            opts.inJustDecodeBounds = false;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            IOUtils.safeClose(is);
        }

        return opts;
    }


    /**
     * 自适应图片的ImageView
     * @param context
     * @param image
     * @param source
     */
    public static void setImageViewMathParent(ImageView image, Bitmap source) {
        //获得屏幕宽度和图片宽度的比例
        float scalew = (float)ScreenBean.getScreenWidth()
                / (float) source.getWidth();
        //获得ImageView的参数类
        ViewGroup.LayoutParams vgl=image.getLayoutParams();
        //设置ImageView的宽度为屏幕的宽度
        vgl.width=ScreenBean.getScreenWidth();
        //设置ImageView的高度
        vgl.height=(int) (source.getHeight()*scalew);
        //设置图片充满ImageView控件
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        //等比例缩放
        image.setAdjustViewBounds(true);
        image.setLayoutParams(vgl);
        image.setImageBitmap(source);

        if (source != null && source.isRecycled()) {
            source.recycle();
        }

    }
}
