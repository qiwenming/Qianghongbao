package com.qwm.qianghongbao;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/**
 * <b>Project:</b> YuantaiApplication<br>
 * <b>Create Date:</b> 2016/11/17<br>
 * <b>Author:</b> qiwenming<br>
 * <b>Description:</b> <br>
 * 抢红包
 * 别人的 http://www.jianshu.com/p/4cd8c109cdfb
 */
public class QianghongbaoService extends AccessibilityService {

    private static final String TAG = "QianghongbaoService";
    private final String mWechatPn = "com.tencent.mm";
    /** 红包消息的关键字*/
    private static final String HONGBAO_TEXT_KEY = "123";
//    private static final String HONGBAO_TEXT_KEY = "[微信红包]";

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "onAccessibilityEvent: ---->" + event);
        if (!mWechatPn.equals(event.getPackageName())) {//微信的报名
            return;
        }
        final int eventType = event.getEventType();

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://通知状态
                handleNotification2(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://window状态改变
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://window内容改变
                break;
        }
    }


    /**
     * 处理通知栏信息
     *
     * 如果是微信红包的提示信息,则模拟点击
     *
     * @param event
     */
    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                Log.i(TAG, "handleNotification: --->"+content);// 24k纯帅: 123
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                if (content.contains(HONGBAO_TEXT_KEY)) {
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void handleNotification2(AccessibilityEvent event){
        List<CharSequence> texts = event.getText();
        if(!texts.isEmpty()){
            for (CharSequence textcs : texts) {
                String text = textcs.toString();
                Log.i(TAG, "handleNotification2: -->"+text);
                if(text.contains(HONGBAO_TEXT_KEY)){
                    if(event.getParcelableData()!=null || event.getParcelableData() instanceof Notification){//数据部位空，而且是通知的数据
                        Notification notificaition = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notificaition.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }



}
