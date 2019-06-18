package com.tonytang.demo.rxretry;

import com.google.common.truth.Truth;
import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //If you change MAX to 20, it will success in most times within 10 retried times.
    private static final int MAX = 20;
    private int retriedCount = 0;
    private Random random;

    @Before
    public void setup() {
        retriedCount = 0;
        random = new Random(MAX);
    }

    @Test
    public void no_retry() {
        Completable completable = completeWithoutRetry();
        completable.test().assertTerminated();
        Truth.assertThat(retriedCount).isEqualTo(1);
        Truth.assertThat(retriedCount).isLessThan(MAX);
    }

    @Test
    public void retry() {
        Completable completable = completeWithRetry();
        TestObserver<Void> result = completable.test();
        result.assertTerminated();
        Truth.assertThat(retriedCount).isLessThan(MAX);
        System.out.println(
            "retriedCount:" + retriedCount + ", succeeded:" + hasNoError(result));
    }

    private boolean hasNoError(TestObserver<Void> result) {
        List<Object> errorList = result.getEvents().get(1);
        return errorList.size() == 0;
    }

    private Completable completeWithoutRetry() {
        return Completable.fromAction(this::doYourWork);
    }

    private Completable completeWithRetry() {
        return Completable.fromAction(this::doYourWork).retry(20);
    }

    private void doYourWork() {
        retriedCount++;
        if (random.nextInt() % MAX == 0) {
            System.out.println("Completed Success.");
        } else {
            throw new RuntimeException("Please retry it again");
        }
    }
}