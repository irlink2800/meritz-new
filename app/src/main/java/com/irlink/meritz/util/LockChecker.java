//package com.irlink.meritz.util;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.irlink.meritz.App;
//import com.irlink.meritz.UnlockActivity;
//
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.disposables.Disposable;
//import io.reactivex.subjects.BehaviorSubject;
//import io.reactivex.subjects.Subject;
//
//public class LockChecker {
//
//    public static CompositeDisposable disposable = new CompositeDisposable();
//    static Disposable subscription;
//    public static Subject<Optional<Context>> subject = BehaviorSubject.create();
//
//    public static void resetTimer(Context ctx) {
//        disposable.clear();
//        if(subscription == null || subscription.isDisposed()) {
//            subscription = subject.subscribe(v -> v.ifPresent(context -> {
//                LogWrapper.addLog("resetTImer", "1");
//                Intent intent = new Intent(context, UnlockActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                context.startActivity(intent);
//                subject.onNext(Optional.empty());
//            }));
//        }
//        disposable.add(Observable.timer(App.getDeviceInfo().crpMchyScrnLckHr, TimeUnit.MINUTES)
//            .map(v -> ctx)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(v -> {
//                LogWrapper.addLog("resetTimer", "2");
//                subject.onNext(Optional.of(v));
//            }));
//    }
//
//    public static void stopTimer(){
//        LogWrapper.addLog("stopTimer", "1");
//        disposable.clear();
//        subject.onNext(Optional.empty());
//    }
//
//    public static void pauseTimer() {
//        LogWrapper.addLog("pauseTimer", "1");
//        if(subscription == null) return;
//        disposable.clear();
//        subscription.dispose();
//    }
//}
