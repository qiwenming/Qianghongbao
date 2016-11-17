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
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

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
//    private static final String HONGBAO_TEXT_KEY = "123";
    private static final String HONGBAO_TEXT_KEY = "[微信红包]";
    private static final String HONGBAO_GET_KEY = "领取红包";

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "onAccessibilityEvent: ---->" +  event.getClassName()+"---->"+event.eventTypeToString(event.getEventType()));
        if (!mWechatPn.equals(event.getPackageName())) {//微信的报名
            return;
        }
        final int eventType = event.getEventType();
        mcount = 0;
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://通知状态
                handleNotification2(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://window状态改变
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://window内容改变
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {//聊天界面
                    getPacket1();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {//红包界面
                    openPacket1();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {//红包详情
                    close();
                }

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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getPacket1(){//获取钱包，打开钱包界面
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
         recursion(rootNode,0);
//        Log.i(TAG, "getPacket1: "+node.getText());
//        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//        AccessibilityNodeInfo parentNode = node.getParent();
//        while (parentNode!=null){
//            Log.i(TAG, "getPacket1: while -->"+parentNode.getText());
//            if(parentNode.isClickable()){
//                parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                break;
//            }
//            parentNode = parentNode.getParent();
//        }
    }

   int mcount = 0;
    /**
     * 递归获取满足的控件
     * @param rootNode
     * @return
     */
    private void recursion(AccessibilityNodeInfo rootNode,int type) {
//        Log.i(TAG, "recursion: ------>"+getSpace(type)+rootNode.getText()+"--->"+rootNode.getChildCount());
        if(rootNode.getChildCount()==0){
            if(rootNode.getText()!=null && rootNode.getText().toString().equals(HONGBAO_GET_KEY)){//获取到抢取红包的控件
//                return rootNode;
                rootNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                AccessibilityNodeInfo parentNode = rootNode.getParent();
                while (parentNode!=null){
                    Log.i(TAG, "getPacket1: while -->"+parentNode.getText());
                    if(parentNode.isClickable()){
                        mcount++;
                        if(mcount>2) return;
                        parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parentNode = parentNode.getParent();
                }
            }
        }else{
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                if (rootNode.getChild(i) != null) {
                    recursion(rootNode.getChild(i),type+1);
                }
            }
        }
//        return rootNode;
    }

    private String getSpace(int type) {
        StringBuilder sb = new StringBuilder("|-");
        for (int i=1;i<=type;i++){
            sb.append("--");
        }
        return sb.toString();
    }

    /**
     * 打开钱包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPacket1(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo!=null){
            List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("@id/b9m");
            nodeInfo.recycle();
            for (AccessibilityNodeInfo accessibilityNodeInfo : nodeInfoList) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void close() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo!=null){
            List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("@id/ez");
            nodeInfo.recycle();
            for (AccessibilityNodeInfo accessibilityNodeInfo : nodeInfoList) {
                Log.i(TAG, "close: "+accessibilityNodeInfo.getText());
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
                while (parent != null) {
                    if (parent.isClickable()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }


    /**
     * 模拟点击,打开抢红包界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo node = recycle(rootNode);

        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        AccessibilityNodeInfo parent = node.getParent();
        while (parent != null) {
            if (parent.isClickable()) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            parent = parent.getParent();
        }
    }

    /**
     * 递归查找当前聊天窗口中的红包信息
     *
     * 聊天窗口中的红包都存在"领取红包"一词,因此可根据该词查找红包
     *
     * @param node
     */
    public AccessibilityNodeInfo recycle(AccessibilityNodeInfo node) {
        Log.i(TAG, "recycle: --->"+node.getText());
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                if (HONGBAO_GET_KEY.equals(node.getText().toString())) {
                    return node;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    recycle(node.getChild(i));
                }
            }
        }
        return node;
    }

}
