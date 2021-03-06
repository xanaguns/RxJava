package com.bluebird.rxjavatest;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.reactivestreams.Publisher;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;


public class Main {
    public static void reactiveProgramming() {
        PublishSubject<Integer> items = PublishSubject.create();
        items.onNext(1);
        items.onNext(2);
        items.onNext(3);
        items.onNext(4);

        items.filter(item -> item % 2 == 0)
                .subscribe(s -> System.out.print(s + " "));

        items.onNext(5);
        items.onNext(6);
        items.onNext(7);
        items.onNext(8);
        System.out.println();
    }

    public static void test01_create() {
        Observable<String> source = Observable.create(emitter -> {
            emitter.onNext("Hello");
            emitter.onNext("Yena");
            emitter.onComplete();
        });
        source.subscribe(s -> System.out.print(s + " "));
        System.out.println();

        Observable<String> source2 = Observable.create(emitter -> {
            emitter.onNext("Hello");
            emitter.onError(new Throwable());
            emitter.onNext("Yena");
        });
        source2.subscribe(s -> System.out.print(s + " "),
                throwable -> System.out.println("Good bye")
        );
    }

    public static void test02_just() {
        Observable<String> source = Observable.just("Hello", "Yena");
        source.subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test03_fromArray() {
        String[] itemArray = new String[]{"Morning", "Afternoon", "Evening"};
        Observable source = Observable.fromArray(itemArray);
        source.subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test04_fromIterable() {
        ArrayList itemList = new ArrayList<String>();
        itemList.add("Morning");
        itemList.add("Afternoon");
        itemList.add("Evening");
        Observable source = Observable.fromIterable(itemList);
        source.subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test05_fromFuture() {
        Future<String> future = Executors.newSingleThreadExecutor()
                .submit(() -> {
                    Thread.sleep(2000);
                    return "This is the future";
                });
        Observable source = Observable.fromFuture(future);
        source.subscribe(System.out::println); //??????????????? ?????????
    }

    public static void test06_fromPublisher() {
        Publisher<String> publisher = subscriber -> {
            subscriber.onNext("Morning");
            subscriber.onNext("Afternoon");
            subscriber.onNext("Evening");
            subscriber.onComplete();
        };
        Observable<String> source = Observable.fromPublisher(publisher);
        source.subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test07_fromCallable() {
        Callable<String> callable = () -> "RxJava is cool";
        Observable source = Observable.fromCallable(callable);
        source.subscribe(System.out::println);
    }

    public static void test08_Single() {
        Single.create(emitter -> emitter.onSuccess("Hello"))
                .subscribe(System.out::println);
    }

    public static void test09_Completable() {
        Completable.create(emitter -> {
            System.out.print("OK ");
            emitter.onComplete();
        }).subscribe(() -> System.out.println("Completed"));
    }

    public static void test10_ColdObservable() {
        Observable src = Observable.interval(1, TimeUnit.SECONDS);
        Disposable disp1 = src.subscribe(value -> System.out.print("  First: " + value));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Disposable disp2 = src.subscribe(value -> System.out.print("  Second: " + value));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        disp1.dispose();
        disp2.dispose();
        System.out.println();
    }

    public static void test11_ConnectableObservable() {
        ConnectableObservable src =
                Observable.interval(1, TimeUnit.SECONDS)
                        .publish();
        src.connect();
        src.subscribe(value -> System.out.print("  First: " + value));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        src.subscribe(value -> System.out.print("  Second: " + value));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public static void test12_autoConnect() {
        Observable<Long> src =
                Observable.interval(1, TimeUnit.SECONDS)
                        .publish()
                        .autoConnect(2);

        src.subscribe(value -> System.out.print("  First: " + value));
        src.subscribe(value -> System.out.print("  Second: " + value));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public static void test13_Disposable() {
        Observable source = Observable.interval(1, TimeUnit.SECONDS);
        Disposable disposable = source.subscribe(s -> System.out.print(s + " "));

        new Thread(() -> {
            try {
                Thread.sleep(3500);
            } catch(Exception e) {
                e.printStackTrace();
            }
            disposable.dispose();
        }).start();
    }

    public static void test14_CompositeDisposable() {
        Observable source = Observable.interval(1, TimeUnit.SECONDS);
        Disposable disposable1 = source.subscribe(s -> System.out.print(s + " "));
        Disposable disposable2 = source.subscribe(s -> System.out.print(s + " "));
        Disposable disposable3 = source.subscribe(s -> System.out.print(s + " "));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // add(Disposable) or addAll(Disposable...)
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable1);
        compositeDisposable.addAll(disposable2, disposable3);
        compositeDisposable.dispose();
        System.out.println();
    }

    public static void test15_defer() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREA);
        Observable<String> justSrc = Observable.just(
                sdf.format(System.currentTimeMillis())
        );

        Observable<String> deferSrc = Observable.defer(() ->
                Observable.just(sdf.format(System.currentTimeMillis()))
        );

        System.out.println("????????????1 : " + sdf.format(System.currentTimeMillis()));

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("????????????2 : " + sdf.format(System.currentTimeMillis()));
        justSrc.subscribe(time ->
                System.out.println("justSrc : " + time)
        );

        deferSrc.subscribe(time ->
                System.out.println("deferSrc : " + time)
        );
    }

    public static void test16_empty_never() {
        Observable.empty()
                .doOnTerminate(() -> System.out.println("empty ??????"))
                .subscribe();

        Observable.never()
                .doOnTerminate(() -> System.out.println("never ??????"))
                .subscribe();
    }

    public static void test17_interval() {
        Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(s -> System.out.print(s + " "));
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        disposable.dispose();
        System.out.println();
    }

    public static void test18_range() {
        Observable.range(3, 5) //range(start, count)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test19_timer() {
        Observable src = Observable.timer(2, TimeUnit.SECONDS);
        System.out.println("??????");
        src.subscribe(item -> //?????? ??? 3??? ?????? ???????????? ????????????.
                System.out.println("?????? --- item : " + item + ", Class: " + item.getClass().getSimpleName())
        );
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void test20_map() {
        Observable<Integer> intSrc = Observable.just(1, 2, 3);
        Observable<String> stringSrc = intSrc.map(i -> String.valueOf(i * 10));
        stringSrc.subscribe(item ->
                System.out.print("  item : " + item + ", Class: " +
                        item.getClass().getSimpleName())
        );
        System.out.println();
    }

    public static void test21_flatmap() {
        Observable<String> src = Observable.just("a", "b", "c");
        src.flatMap(str -> Observable.just(str + "1", str + "2"))
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test22_buffer() {
        Observable.range(0, 7)
                .buffer(3) //count
                .subscribe(integers -> {
                    System.out.print(" buffer ????????? ??????:");
                    for (Integer i : integers) {
                        System.out.print(" #" + i);
                    }
                });
        System.out.println();
    }

    public static void test23_scan() {
        Observable.just("a", "b", "c", "d", "e")
                .scan((x, y) -> x + y)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test24_debounce() {
        Observable.create(emitter -> {
            emitter.onNext("1");
            Thread.sleep(100);
            emitter.onNext("2");
            emitter.onNext("3");
            emitter.onNext("4");
            emitter.onNext("5");
            Thread.sleep(100);
            emitter.onNext("6");
            Thread.sleep(100);
        })
                .debounce(10, TimeUnit.MILLISECONDS)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test25_distinct() {
        Observable.just("A", "B", "B", "A", "C")
                .distinct()
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test26_elementAt() {
        Observable.just("A", "B", "B", "A", "C")
                .elementAt(3)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test27_filter() {
        Observable.just(12, 35, 43, 39, 10, 18, 42) //942?????? ?????? ????????????
                .filter(x -> x > 30)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test28_sample() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .sample(300, TimeUnit.MILLISECONDS)
                .subscribe(s -> System.out.print(s + " "));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public static void test29_skip() {
        Observable.just(1, 2, 3, 4, 5)
                .skip(2)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test30_take() {
        Observable.just(1, 2, 3, 4, 5)
                .take(2)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test31_all() {
        Observable.just(1, 2, 3, 4, 5)
                .all(i -> i > 0)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test32_combineLatest() {
        Observable<Integer> intSource = Observable.create((emitter -> {
            new Thread(() -> {
                for (int i = 1; i <= 5; i++) {
                    emitter.onNext(i);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {
                    }
                }
            }).start();
        }));

        Observable<String> strSource = Observable.create(emitter -> {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    emitter.onNext("A");
                    Thread.sleep(700);
                    emitter.onNext("B");
                    Thread.sleep(100);
                    emitter.onNext("C");
                    Thread.sleep(700);
                    emitter.onNext("D");
                } catch (Exception e) {
                }
            }).start();
        });

        Observable.combineLatest(intSource, strSource, (num, str) -> num + str)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test33_zip() {
        Observable<Integer> intSource = Observable.create((emitter -> {
            new Thread(() -> {
                for (int i = 1; i <= 5; i++) {
                    emitter.onNext(i);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {
                    }
                }
            }).start();
        }));


        Observable<String> strSource = Observable.create(emitter -> {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    emitter.onNext("A");
                    Thread.sleep(700);
                    emitter.onNext("B");
                    Thread.sleep(100);
                    emitter.onNext("C");
                    Thread.sleep(700);
                    emitter.onNext("D");
                } catch (Exception e) {
                }
            }).start();
        });

        Observable.zip(intSource, strSource, (num, str) -> num + str)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test34_merge() {
        Observable<Integer> src1 = Observable.create(emitter ->
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        emitter.onNext(1);
                        Thread.sleep(100);
                        emitter.onNext(2);
                        Thread.sleep(100);
                        emitter.onNext(3);
                    } catch (Exception e) {
                    }
                }).start()
        );

        Observable<Integer> src2 = Observable.create(emitter ->
                new Thread(() -> {
                    try {
                        Thread.sleep(250);
                        emitter.onNext(100);
                        Thread.sleep(250);
                        emitter.onNext(200);
                        Thread.sleep(250);
                        emitter.onNext(300);
                    } catch (Exception e) {
                    }
                }).start()
        );


        Observable.merge(src1, src2)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test35_introErrorDebugging() {
        Observable.just("1", "2", "a", "3")
                .map(i -> Integer.parseInt(i))
                .subscribe(s -> System.out.print(s + " "),
                        throwable -> System.out.println("?????? ?????? ?????? ???????????? ??????")
                );
    }

    public static void test36_onErrorReturn() {
        Observable.just("1", "2", "a", "3")
                .map(i -> Integer.parseInt(i))
                .onErrorReturn(throwable -> -1)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test37_onErrorResumeNext() {
        Observable.just("1", "2", "a", "3")
                .map(i -> Integer.parseInt(i))
                .onErrorResumeNext(throwable ->
                        Observable.just(100, 200, 300))
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test38_retry() {
        Observable.just("1", "2", "a", "3")
                .map(i -> Integer.parseInt(i))
                .retry(2)
                .onErrorReturn(throwable -> -1)
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test39_doOnEach() {
        Observable.just(1, 2, 3)
                .doOnEach(notification -> {
                    System.out.print(" value = " + notification.getValue());
                    System.out.print(", isOnNext = " + notification.isOnNext());
                    System.out.print(", isOnComplete = " + notification.isOnComplete());
                    System.out.print(", isOnError = " + notification.isOnError());
                })
                .subscribe(s -> System.out.println(" : " + s));
        System.out.println();
    }

    public static void test40_doOnNext() {
        Observable.just(1, 2, 3)
                .doOnNext(it -> System.out.print("????????? ?????? " + it))
                .subscribe(s -> System.out.print(" : " + s + ", "));
        System.out.println();
    }

    public static void test41_doOnSubscribe() {
        Observable.just(1, 2, 3)
                .doOnSubscribe(disposable -> {
                    if (disposable.isDisposed()) {
                        System.out.println("disposed ???");
                    } else {
                        System.out.print("?????? ?????? ");
                    }
                })
                .subscribe(s -> System.out.print(s + " "));
        System.out.println();
    }

    public static void test42_doOnComplete() {
        Observable.just("1", "2", "3")
                .doOnComplete(() -> System.out.println("Complete"))
                .subscribe(s -> System.out.print(s + " "));
    }

    public static void test43_doOnError() {
        Observable.just("1", "2", "a", "3")
                .map(i -> Integer.parseInt(i))
                .doOnError(throwable -> System.out.println(throwable.toString()))
                .subscribe(s -> System.out.print(s + " "));
    }


    public static void main(String[] args) {
    // https://blog.yena.io/studynote/
    // 1. ??????
        reactiveProgramming();

    // 2. [Android] RxJava Observable ????????????
        // ??? Observable ????????????
        test01_create();
        test02_just();

        // ??? ????????? Observable??? ????????????
        test03_fromArray();
        test04_fromIterable();
        //test05_fromFuture();
        test06_fromPublisher();
        test07_fromCallable();

        // ??? ????????? Observable??? ??????
        test08_Single();
        test09_Completable();

    // 3. [Android] RxJava Cold Observable, Hot Observable
        // ??? Cold Observable
        //test10_ColdObservable();

        // ??? Hot Observable
        //  - publish ???????????? connect ?????????
        //test11_ConnectableObservable();
        //  - autoConnect ?????????
        //test12_autoConnect();

    // 4. [Android] RxJava Disposable
        // ??? Disposable
        //test13_Disposable();

        // ??? CompositeDisposable
        //test14_CompositeDisposable();

    // 5. [Android] RxJava Observable ??????, ?????? ?????????
        // ??? Observable??? ???????????? ?????????
        //  - defer ?????????
        //test15_defer();
        //  - empty, never ?????????
        test16_empty_never();
        //  - interval ?????????
        //test17_interval();
        //  - range ?????????
        test18_range();
        //  - timer ?????????
        //test19_timer();

        // ??? Observable??? ???????????? ?????????
        //  - map ?????????
        //test20_map();
        //  - flatMap ?????????
        //test21_flatmap();
        //  - buffer ?????????
        //test22_buffer();
        //  - scan ?????????
        //test23_scan();

    // 6. [Android] RxJava Observable ??????, ?????? ?????????
        // ??? Observable??? ??????????????? ?????????
        //  - debounce ?????????
        test24_debounce();
        //  - distinct ?????????
        test25_distinct();
        //  - elementAt ?????????
        test26_elementAt();
        //  - filter ?????????
        test27_filter();
        //  - sample ?????????
        //test28_sample();
        //  - skip ?????????
        test29_skip();
        //  - take ?????????
        test30_take();
        //  - all ?????????
        test31_all();

        // ??? Observable??? ???????????? ?????????
        //  - combineLatest ?????????
        //test32_combineLatest();
        //  - zip ?????????
        //test33_zip();
        //  - merge ?????????
        //test34_merge();

    // 7. [Android] RxJava ?????? ????????? ??? ?????????
        // ??? ????????? ????????? ?????????
        test35_introErrorDebugging();
        //  - onErrorReturn ?????????
        test36_onErrorReturn();
        //  - onErrorResumeNext ?????????
        test37_onErrorResumeNext();
        //  - retry ?????????
        test38_retry();

        // ??? ???????????? ?????? doOn- ?????????
        //  - doOnEach ?????????
        test39_doOnEach();
        //  - doOnNext ?????????
        test40_doOnNext();
        //  - doOnSubscribe ?????????
        test41_doOnSubscribe();
        //  - doOnComplete ?????????
        test42_doOnComplete();
        //  - doOnError ?????????
        test43_doOnError();
    }
}
