package rx2;

import io.reactivex.*;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * RxJava 2.0 : reactive stream을 구현
 *
 * reactive stream : 데이터 스트림을 비동기로 처리
 *
 * 데이터를 가져와서 처리하는 것이 아니고 데이터를 받은 시점에 반응해서 처리
 *
 * 데이터를 생산하는 publisher는 열심히 생산, 데이터를 소비하는 subcriber는 열심히 소비 하면 된다.
 *
 * Micro Service와 어울림
 */
public class RxJava01 {
    public static void main(String[] args) {
        RxJava01 rxJava01 = new RxJava01();
        //rxJava01.basicConcept_001();
        //rxJava01.coldHot_002();
        //rxJava01.disposable_003();
        //rxJava01.newSubscriber_004();
        //rxJava01.singleMaybe_005();
        rxJava01.scheduler_006();
    }

    public void basicConcept_001() {

        // Flowable : 생산자(publisher), 소비자(subscriber) 역압력 가능
        Flowable<Integer> integerFlowable = Flowable.just(1, 2, 3, 4, 5, 6);
        integerFlowable.subscribe(data -> System.out.println(data));
        integerFlowable.subscribe(data -> System.out.println("2: " + data),
                error -> error.printStackTrace(),
                () -> System.out.println("완료")
                );

        // 역압력 불가
        Observable<Integer> integerObservable = Observable.just(1, 2, 3, 4, 5, 6);
        integerObservable.subscribe(data -> System.out.println(data));
    }

    public void coldHot_002() {
        boolean isCold = true;

        if (isCold) {
            // COLD
            /*
            3 초 sleep main
            1: 0 - thread : RxComputationThreadPool-1
            1: 1 - thread : RxComputationThreadPool-1
            1: 2 - thread : RxComputationThreadPool-1
            3 초 sleep main
            1: 3 - thread : RxComputationThreadPool-1
            2: 0 - thread : RxComputationThreadPool-2
            1: 4 - thread : RxComputationThreadPool-1
            2: 1 - thread : RxComputationThreadPool-2
            1: 5 - thread : RxComputationThreadPool-1
            2: 2 - thread : RxComputationThreadPool-2
             */
            Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS);// 1초마다 한번씩 데이터 publish하는 publisher

            flowable.subscribe(data -> System.out.println("1: " + data + " - thread : " + Thread.currentThread().getName()));
            ThreadUtil.sleep(3, true);

            flowable.subscribe(data -> System.out.println("2: " + data + " - thread : " + Thread.currentThread().getName()));
            ThreadUtil.sleep(3, true);
        } else {
            // HOT
            /*
            3 초 sleep main
            1: 0 - thread : RxComputationThreadPool-1
            1: 1 - thread : RxComputationThreadPool-1
            1: 2 - thread : RxComputationThreadPool-1
            3 초 sleep main
            1: 3 - thread : RxComputationThreadPool-1
            2: 3 - thread : RxComputationThreadPool-2
            1: 4 - thread : RxComputationThreadPool-1
            2: 4 - thread : RxComputationThreadPool-2
            1: 5 - thread : RxComputationThreadPool-1
            2: 5 - thread : RxComputationThreadPool-2
             */
            ConnectableFlowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS).publish();
            flowable.connect();

            flowable.subscribe(data -> System.out.println("1: " + data + " - thread : " + Thread.currentThread().getName()));
            ThreadUtil.sleep(3, true);

            flowable.subscribe(data -> System.out.println("2: " + data + " - thread : " + Thread.currentThread().getName()));
            ThreadUtil.sleep(3, true);
        }
    }

    public void disposable_003() {
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS);
        Disposable disposable = flowable.subscribe(data -> System.out.println(data));
        ThreadUtil.sleep(5, true);
        disposable.dispose(); // 구독 해지
        ThreadUtil.sleep(5, true);

        Disposable disposable1 = flowable.subscribe(data -> System.out.println("1 : " + data));
        ThreadUtil.sleep(5, true);
        Disposable disposable2 = flowable.subscribe(data -> System.out.println("2 : " + data));
        ThreadUtil.sleep(5, true);
        Disposable disposable3 = flowable.subscribe(data -> System.out.println("3 : " + data));
        ThreadUtil.sleep(5, true);

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(disposable1);
        compositeDisposable.add(disposable2);
        compositeDisposable.add(disposable3);

        // 한꺼번에 구독해지
        compositeDisposable.dispose();
        ThreadUtil.sleep(3, true);
    }

    public void newSubscriber_004() {

        // just에서는 main thread로 도는듯!
        Flowable.just(1,2,3,4,5)
                .doOnNext(data -> System.out.println(Thread.currentThread().getName() + " data : " + data))
                .subscribe(data -> System.out.println(data));

        Flowable.just(1,2,3,4,5).subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext : " + integer);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

    public void singleMaybe_005() {
        // RxJava
        Single<Integer> single = Single.just(1);
        Maybe<Integer> maybe = Maybe.empty();
        Maybe<Integer> maybe1 = Maybe.just(1);

        // webflux
        Mono<Integer> mono = Mono.empty();
        Mono<Integer> mono1 = Mono.just(1);

        // spring reactor
        Flux<Integer> flux = Flux.just(1,2,3);

        mono1.subscribe(data -> System.out.println(data));
        flux.doOnNext(data -> System.out.println("--------------------------->"+data)).subscribe(data -> System.out.println(">>>>>>"+data));

        ThreadUtil.sleep(5, false);
    }

    public void scheduler_006() {
        Flowable.just(1,2,3,4,5)
                .doOnNext(data -> System.out.println(ThreadUtil.getThreadName())) // publish
                .subscribe(data -> System.out.println(data)) // subscribe
        ;

        //Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 5, 6, 7)
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                // 역압력 전략
                //.onBackpressureDrop() // 단순 오버 플로우 된 것 버리기
                .onBackpressureBuffer(
                        3, () -> System.out.println("[ERROR] buffer Over"), BackpressureOverflowStrategy.DROP_LATEST
                ) // Consumer 쪽에서 소비 가능 분 이상일 때 (역압력 대상) --> 대상 item들을 buffer한다.

                // publisher쪽의 Scheduler를 정의하는 방법
                .subscribeOn(Schedulers.computation())
                //.subscribeOn(Schedulers.io())
                //.subscribeOn(Schedulers.single())
                .doOnNext(data -> System.out.println(ThreadUtil.getThreadName()));


        flowable

                // subscriber 쪽의 Scheduler를 정의하는 방법
                .observeOn(Schedulers.computation(), false, 3)
                .subscribe(data -> {
                    ThreadUtil.sleep(2, false);
                    System.out.println(data);
                });

        ThreadUtil.sleep(10, false);
    }
}
