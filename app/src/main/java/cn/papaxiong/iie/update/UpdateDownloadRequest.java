package cn.papaxiong.iie.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/11/16.
 * 真正的去下载和
 */

public class UpdateDownloadRequest implements Runnable {
    private String downloadUrl;
    private String localPath;
    private UpdateDownloadListener downloadListener;
    private Boolean isDownloading = false;
    private long currentLength;
    private DownloadResponseHandler downloadHandler;


    public UpdateDownloadRequest(String downloadUrl, String localPath,
                                 UpdateDownloadListener downloadListener) {
        this.downloadUrl = downloadUrl;
        this.localPath = localPath;
        this.downloadListener = downloadListener;
        this.isDownloading = true;
        this.downloadHandler = new DownloadResponseHandler();
    }

    @Override
    public void run() {
        try{
            makeRequest();
        } catch (InterruptedIOException e){
        } catch (IOException e){
        }
    }

    private void makeRequest() throws IOException,InterruptedIOException{
        if(!Thread.currentThread().isInterrupted()){
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Connection","Keep-Alive");
                connection.connect();
                currentLength = connection.getContentLength();
                if( !Thread.currentThread().isInterrupted()){
                    downloadHandler.sendResponseMessage(connection.getInputStream());
                }
            }catch (IOException e){
                throw e;
            }
        }
    }

    private String getTwoPointFloatStr(float value){
        DecimalFormat fnum = new DecimalFormat("0.00");
        return fnum.format(value);
    }
    public enum FailureCode{
        UnknownHost,Socket,SocketTimeout,ConnectTimeout,IO,
        HttpResponse,JSON,Interrupted
    }


    public class DownloadResponseHandler{
        protected static final int SUCCESS_MESSAGE = 0;
        protected static final int FAILUREE_MESSAGE = 1;
        protected static final int START_MESSAGE = 2;
        protected static final int FINISH_MESSAGE = 3;
        protected static final int NETWORK_OFF = 4;
        protected static final int PROGRESS_CHANGE = 5;

        private int mCompleteSize = 0;
        private int progress = 0;
        private Handler handler;

        public DownloadResponseHandler() {
            this.handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    handleSelfMessage(msg);
                }
            };
        }

        /**
         * 用来发送不同消息对象
         */
        protected void sendFinishMessage(){
            sendMessage(obtainMessage(FINISH_MESSAGE,null));
        }
        protected void sendProgressChangeMessage(int progress){
            sendMessage(obtainMessage(PROGRESS_CHANGE,new Object[]{progress}));
        }
        protected void sendFailureMessage(FailureCode failureCode){
            sendMessage(obtainMessage(FAILUREE_MESSAGE
            ,new Object[]{failureCode}));
        }
        protected  void sendMessage(Message msg){
            if(handler != null){
                handler.sendMessage(msg);
            }else {
                handleSelfMessage( msg);
            }

        }

        protected Message obtainMessage(int responseMessage,Object responses){
            Message msg = null;
            if(handler != null){
                msg = handler.obtainMessage(responseMessage,responses);
            }else{
                msg = Message.obtain();
                msg.what = responseMessage;
                msg.obj = responses;
            }

            return msg;
        }
        private void handleSelfMessage(Message msg) {
            Object[] response;
            switch (msg.what){
                case FAILUREE_MESSAGE:
                    response = (Object[]) msg.obj;
                    handleFailureMessage((FailureCode) response[0]);
                    break;
                case PROGRESS_CHANGE:
                    response = (Object[]) msg.obj;
                    handleProgressChangeMessage(((Integer)response[0]).intValue());
                    break;
                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }
        }

        private void onFinish() {
            downloadListener.onFinished(mCompleteSize,"");
        }

        private void handleProgressChangeMessage(int progress) {
            downloadListener.onProgressChange(progress,downloadUrl);
        }

        private void handleFailureMessage(FailureCode failureCode) {
            downloadListener.onFailure();

        }

        private void onFailure(FailureCode failureCode) {
            onFailure(failureCode);
        }

        public void sendResponseMessage(InputStream inputStream) {
            RandomAccessFile randomAccessFile = null;
            mCompleteSize = 0;
            try {
                byte[] buffer = new byte[1024];
                int length = -1;
                int limit = 0;
                randomAccessFile = new RandomAccessFile(localPath,"rwd");
                while ((length = inputStream.read(buffer)) != -1){
                    if(isDownloading) {
                        randomAccessFile.write(buffer,0,length);
                        mCompleteSize += length;
                        if(mCompleteSize < currentLength) {
                            progress = (int) Float.parseFloat(getTwoPointFloatStr(
                                    ((float) mCompleteSize) / currentLength))*100;
                            if (limit / 30 == 0 && progress <= 100) {
                                sendProgressChangeMessage(progress);
                            }
                            limit++;
                        }
                    }
                }
                sendFinishMessage();
            }catch (IOException e){
                sendFailureMessage(FailureCode.IO);
            }finally {
                try {

                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                }catch (IOException e){
                    sendFailureMessage(FailureCode.IO);
                }
            }
        }
    }
}
