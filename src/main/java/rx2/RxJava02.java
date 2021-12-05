package rx2;

import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RxJava02 {

    public static void main(String[] args) {
        RxJava02 rxJava02 = new RxJava02();
        //rxJava02.fromArray_fromIterable();
        //rxJava02.fromCallable();
        //rxJava02.range();
        //rxJava02.interval();
        //rxJava02.timer();
        //rxJava02.defer();
        //rxJava02.map();
        //rxJava02.flatMap();
        //rxJava02.concatMap();
        //rxJava02.concatMapEager();
        //rxJava02.merge();
        //rxJava02.retry();
        //rxJava02.onErrorReturn();
        //rxJava02.toList();
        //rxJava02.toMap();
        rxJava02.toMultiMap();
    }

    public void fromArray_fromIterable() {
        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Flowable.fromArray(numbers).subscribe(new CustomSubscriber(true));

        List<Integer> numberList = Arrays.asList(numbers);
        Flowable.fromIterable(numberList).subscribe(new CustomSubscriber(true));
    }

    /**
     * Done 만 출력하고 끝난다.
     */
    public void fromCallable() {
        Flowable.fromCallable(() -> "Done").subscribe(new CustomSubscriber(true));
    }

    /**
     * @summary 100 부터 199까지 출력한다.
     */
    public void range() {
        Flowable.range(100, 100).subscribe(new CustomSubscriber(true));
    }

    /**
     *
     */
    public void interval() {
        Flowable.interval(1, TimeUnit.SECONDS).subscribe(new CustomSubscriber(true)); // main thread가 아닌 RxComputationThreadPool-* 쓰레드를 탄다.
        ThreadUtil.sleep(5, false); // main thread 를 5초 sleep
    }

    public void timer() {
        Flowable.timer(10L, TimeUnit.SECONDS).subscribe(new CustomSubscriber(true)); // RxComputationThreadPool-1
        ThreadUtil.sleep(20, true); // main thread sleep
    }

    /**
     * defer : 구독했을 당시의 데이터를 갖고 온다.
     */
    public void defer() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        // defer는 구독하는 시점에 publisher 데이터가 생성 된다.
        Flowable<Integer> flowable = Flowable.defer(() -> Flowable.just(list.size(), list.size() + 1, list.size() + 2));
        flowable.subscribe(data -> System.out.println("1: " + data));

        list.remove(0);
        ThreadUtil.sleep(1, false);
        flowable.subscribe(data -> System.out.println("2: " + data)); // 2, 3, 4로 출력되게 된다.
    }

    public void map() {
        // Flowable.just("a", "b", "c", "d").map(String::toUpperCase).subscribe(new CustomSubscriber(true));
        Flowable.range(1, 100).map(data -> {
            if (data % 3 == 0) {
                return data;
            }
            else {
                // null을 return 할 수 없다.
                return -1;
            }
        }).subscribe(new CustomSubscriber(true));
    }

    public void flatMap() {
        Flowable.range(1, 100)
                .flatMap(data -> {
                    if (data % 3 == 0) {
                        return Flowable.just(data * 2);
                    }
                    else {
                        return Flowable.empty();
                    }},
                    // originData : map으로 처리하기 이전의 데이터
                    // newData : map으로 처리한 데이터
                    (originData, newData) -> originData + " " + newData
                )
                .subscribe(new CustomSubscriber(true));

        Flowable<Integer> integerFlowable = Flowable.range(1, 1000)
                // 멀티쓰레드 일 때 순서를 보장하지 않는다.
                .flatMap(data -> {
                            if (data % 3 == 0) {
                                return Flowable.just(data).delay(1, TimeUnit.SECONDS);
                            } else {
                                return Flowable.empty();
                            }
                        }
                )
                .doOnNext(data ->
                        System.out.println(ThreadUtil.getThreadName() + " publisher ================> " + data)
                );

        integerFlowable.subscribe(data -> System.out.println(" [[ subscriber: " + data));
        ThreadUtil.sleep(10, true);
    }

    public void concatMap() {
        Flowable<Integer> integerFlowable = Flowable.range(1, 1000)
                // 멀티쓰레드 일 때 또한 순서를 보장한다.
                // 하지만 엄청나게 느리다.
                .concatMap(data -> {
                            if (data % 3 == 0) {
                                return Flowable.just(data).delay(1, TimeUnit.SECONDS);
                            } else {
                                return Flowable.empty();
                            }
                        }
                )
                .doOnNext(data ->
                        System.out.println(ThreadUtil.getThreadName() + " publisher ================> " + data)
                );

        integerFlowable.subscribe(data -> System.out.println(" [[ subscriber: " + data));
        ThreadUtil.sleep(10, true);
    }

    public void concatMapEager() {
        Flowable<Integer> integerFlowable = Flowable.range(1, 1000)
                // 멀티쓰레드 일 때 또한 순서를 보장한다.
                // 또한 빠르다.
                // BUT!!! OOM이 발생할 수 있다.
                .concatMapEager(data -> {
                            if (data % 3 == 0) {
                                return Flowable.just(data).delay(1, TimeUnit.SECONDS);
                            } else {
                                return Flowable.empty();
                            }
                        }
                )
                .doOnNext(data ->
                        System.out.println(ThreadUtil.getThreadName() + " publisher ================> " + data)
                );

        integerFlowable.subscribe(data -> System.out.println(" [[ subscriber: " + data));
        ThreadUtil.sleep(10, true);
    }

    public void merge() {
        Flowable<Integer> source1 = Flowable.range(1, 100);
        Flowable<Integer> source2 = Flowable.range(101, 200);

        // 그냥 merge 하는 것임.
        Flowable.merge(source1, source2).subscribe(new CustomSubscriber(true));
        ThreadUtil.sleep(4, true);
    }

    public void retry() {
        Flowable<String> flowable = Flowable.just("1", "2", "삼", "4").map(data -> String.valueOf(Integer.parseInt(data))).retry(3);
        /*
        아래와 같이 출력된다.
        main 1
        main 2
        // retry 3번 시작
        main 1
        main 2
        main 1
        main 2
        main 1
        main 2
        onError
         */
        flowable.subscribe(new CustomSubscriber(true));
    }

    public void onErrorReturn() {
        var flowable = Flowable.just("1", "2", "삼", "4").map(data -> String.valueOf(Integer.parseInt(data))).onErrorReturn(data -> "-1");
        /*
        main 1
        main 2
        // error 발생, -1 리턴하고 끝난다.
        main -1
        onComplete
         */
        flowable.subscribe(new CustomSubscriber(true));
    }

    public void toList() {
        Single<List<Integer>> listSingle = Flowable.just(1, 2, 3, 4, 5).toList();

        // [1, 2, 3, 4, 5] 요렇게 list 로 한번에(single list) data를 전달한다.
        // WARN list가 한번에 전달되므로 OOM 주의할 것
        listSingle.subscribe(System.out::println);
    }

    public void toMap() {
        String[] strings = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        AtomicInteger i = new AtomicInteger(1);
        Single<Map<Integer, String>> mapSingle = Flowable.fromArray(strings).toMap(data -> i.getAndIncrement());

        // 출력
        // {1=A, 2=B, 3=C, 4=D, 5=E, 6=F, 7=G, 8=H, 9=I, 10=J, 11=K, 12=L, 13=M, 14=N, 15=O, 16=P, 17=Q, 18=R, 19=S, 20=T, 21=U, 22=V, 23=W, 24=X, 25=Y, 26=Z}
        // OOM 주의
        mapSingle.subscribe(System.out::println);
    }

    public void toMultiMap() {
        Single<Map<String, Collection<Integer>>> mapSingle = Flowable.range(1, 100).toMultimap(data -> {
            if (data % 3 == 0) {
                return "3의 배수";
            } else {
                return "3의 배수 아님";
            }
        });

        mapSingle.subscribe(System.out::println);
    }

}
