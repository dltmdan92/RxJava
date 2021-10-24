package lambda;

@FunctionalInterface
interface Mathematics {
    int cal(int x, int y);
}

// @FunctionalInterface 이걸 셋팅함으로써 컴파일 타임 때 람다식을 위한 interface를 check할 수 있다.
@FunctionalInterface
interface Trace1<T> {
    void follow(T t);
}

@FunctionalInterface
interface Trace2 {
    void hello();
}

public class Lambda001 {
    public static void main(String[] args) {
        Trace1<String> trace1 = (x) -> System.out.println(x + " " +System.currentTimeMillis());
        Trace2 trace2 = () -> System.out.println("hello");

        trace1.follow("PSC");
        trace2.hello();

        Mathematics calulate = (x, y) -> x + y;
        System.out.println(cal(10, 20, calulate));
        System.out.println(cal(10, 20, (x, y) -> x + y));
        System.out.println(cal(10, 20, Integer::sum));
    }

    public static int cal(int x, int y, Mathematics mathematics) {
        return mathematics.cal(x, y);
    }
}
