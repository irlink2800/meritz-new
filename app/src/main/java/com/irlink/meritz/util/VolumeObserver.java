//package com.irlink.meritz.util;
//
//import android.content.Context;
//import android.database.ContentObserver;
//import android.media.AudioManager;
//import android.os.Handler;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import com.irlink.meritz.App;
//import com.irlink.meritz.MainActivity;
//
///**
// * Created by posisun on 2015-10-15.
// */
//public class VolumeObserver extends ContentObserver {
//    Context context;
//
//    public VolumeObserver(final Context context, final Handler handler) {
//        super(handler);
//        this.context = context;
//    }
//
//    @Override
//    public boolean deliverSelfNotifications() {
//        return false;
//    }
//
//    @Override
//    public void onChange(final boolean selfChange) {
////        if(((MainActivity)this.context).socketService != null) {
//            final int currentVolume = App.getDeviceInfo().getAudioManager().getStreamVolume(AudioManager.STREAM_VOICE_CALL);
//            Map<String, Object> msg = new HashMap<>();
//            msg.put("type", "DevVolume");
//            msg.put("volume", currentVolume);
//            App.socketService.sendMsg(msg);
////        }
//    }
//}
