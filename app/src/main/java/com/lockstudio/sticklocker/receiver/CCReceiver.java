package com.lockstudio.sticklocker.receiver;//package com.lockstudio.sticklocker.receiver;
//Gavan注释了cocospush
//import android.content.Context;
//import android.content.Intent;
//import android.text.TextUtils;
//
//import com.cocos.CCPushRecevier;
//import com.lockstudio.sticklocker.service.CCService;
//
//import java.util.List;
//
//public class CCReceiver extends CCPushRecevier {
//
//    @Override
//    public void onSetTags(Context context, int i, List<String> list, List<String> list1) {
//
//    }
//
//    @Override
//    public void onDelTags(Context context, int i, List<String> list, List<String> list1) {
//
//    }
//
//    @Override
//    public void onStartPush(Context context, int i) {
//
//    }
//
//    @Override
//    public void onStopPush(Context context, int i) {
//
//    }
//
//    @Override
//    public void onSetAccount(Context context, int i, String s) {
//
//    }
//
//    @Override
//    public void onDelAccount(Context context, int i) {
//
//    }
//
//    @Override
//    public void onMessage(Context context, String s) {
//        if (TextUtils.isEmpty(s)) {
//            return;
//        }
//
//        Intent intent = new Intent(context, CCService.class);
//        intent.putExtra("MESSAGE", s);
//        context.startService(intent);
//    }
//
//    @Override
//    public void onLocalTimer(Context context, String s) {
//
//    }
//
//    @Override
//    public void onReceiveImMessage(Context context, String s, String s1) {
//
//    }
//
//    @Override
//    public void onSendImMessage(Context context, String s, int i, String s1) {
//
//    }
//}
