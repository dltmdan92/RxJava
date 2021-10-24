package generic;

import java.util.ArrayList;
import java.util.List;

class NonGenericType {
    private Object value;

    public NonGenericType(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}

class GenericType<T> {
    private T value;

    public GenericType(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}

class MultiType<K, V> {
    private K key;
    private V value;

    public MultiType(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}

class A {}
class B extends A {}
class C extends B {}

public class Generic001 {

    public static void test001() {
        NonGenericType nonGenericType1 = new NonGenericType("A");
        String value1 = (String) nonGenericType1.getValue();
        NonGenericType nonGenericType2 = new NonGenericType(1);
        int value2 = (int) nonGenericType2.getValue();
    }

    public static void test002() {
        GenericType<String> genericType1 = new GenericType<>("1");
        String value1 = genericType1.getValue(); // 제너릭을 통해 형변환 작업이 생략된다.
    }

    public static void test003() {
        MultiType<Integer, String> multiType = new MultiType<>(1, "1");
        int key = multiType.getKey();
        String value = multiType.getValue();
    }

    public static <K, V, R> List getA(MultiType<K, V> multiType, R r) {
        List<R> list = new ArrayList<>();
        list.add(r);
        return list;
    }

    public static void getB(GenericType<?> genericType) {}

    public static void getExtendWildCard(GenericType<? extends B> genericType) {}

    public static void getSuperWildCard(GenericType<? super B> genericType) {}

    public static void main(String[] args) {
        List list = getA(new MultiType<>(1, 1), "value");
        System.out.println(list);
        getB(new GenericType<>(1));

        GenericType<A> a = new GenericType<>(new A());
        GenericType<B> b = new GenericType<>(new B());
        GenericType<C> c = new GenericType<>(new C());

        // wildcard 이므로 다 들어갈 수 있다.
        getB(a); getB(b); getB(c);
        // ? extends B
        getExtendWildCard(b); getExtendWildCard(c);
        // ? super B
        getSuperWildCard(a); getSuperWildCard(b);
    }
}
