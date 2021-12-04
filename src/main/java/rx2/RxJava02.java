package rx2;

import io.reactivex.Flowable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        rxJava02.flatMap();
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
        Flowable.range(1, 100).flatMap(data -> {
            if (data % 3 == 0) {
                return Flowable.just(data * 2);
            }
            else {
                return Flowable.empty();
            }},
                // originData : map으로 처리하기 이전의 데이터
                // newData : map으로 처리한 데이터
                (originData, newData) -> originData + " " + newData)
                .subscribe(new CustomSubscriber(true));
    }

}
