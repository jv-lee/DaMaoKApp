package com.yuan7.lockscreen.helper.rx;

import android.support.annotation.NonNull;
import java.util.Vector;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


/**
 * Created by Administrator on 2016/11/29.
 */

public class RxBus {

    private static volatile RxBus mInstance;

    private Vector<Subject> mSubjects = new Vector<>();

    private RxBus() {
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    public synchronized <T> Observable<T> register(@NonNull Object object) {
        Subject<T> subject = PublishSubject.create();
        mSubjects.add(subject);
        return subject;
    }

    public synchronized void unregister(Object object) {
        mSubjects.remove(object);
    }

    public void post(@NonNull EventBase content) {
        synchronized (this) {
            for (Subject subject : mSubjects) {
                if (subject != null) {
                    subject.onNext(content);
                }
            }
        }
    }

}
