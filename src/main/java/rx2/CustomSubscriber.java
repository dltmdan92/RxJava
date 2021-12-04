package rx2;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class CustomSubscriber implements Subscriber {

    private boolean debug;

    public CustomSubscriber() {
    }

    public CustomSubscriber(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        if (isDebug()) {
            System.out.println(ThreadUtil.getThreadName() + " " + o);
        }
        else {
            System.out.println(o);
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onError");
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }

    private boolean isDebug() {
        return debug;
    }
}
