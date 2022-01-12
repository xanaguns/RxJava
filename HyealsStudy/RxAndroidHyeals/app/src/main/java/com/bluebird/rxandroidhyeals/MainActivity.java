package com.bluebird.rxandroidhyeals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<String> observable = Observable.create(emitter -> {
                    emitter.onNext(Thread.currentThread().getName() + "\n: RxJava Observer Test");
                    emitter.onComplete();
                }
        );

        //observable.subscribe(observer); // main스레드에서 실행
        observable.subscribeOn(Schedulers.io()).subscribe(observer); // io스레드에서 실행
    }

    DisposableObserver<String> observer = new DisposableObserver<String>() {
        @Override
        public void onNext(@NonNull String s) {
            TextView rxjava_text = findViewById(R.id.textView);
            rxjava_text.setText(s);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d("TEST", "Observer Error...");
        }

        @Override
        public void onComplete() {
            Log.d("TEST", "Observer Complete!");
        }
    };
}