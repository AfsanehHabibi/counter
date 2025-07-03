package ir.nofitech.counter.service;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class CounterServiceTest {

    private final CounterService counterService = new CounterService();

    @Test
    void whenSingleThreadCallsCounterGetSequentialNumbersStartingOne() {
        assertEquals(1, counterService.getNext());
        assertEquals(2, counterService.getNext());
        assertEquals(3, counterService.getNext());
    }

    @Test
    void whenMultiThreadsCallCounterGetSequentialNumbersStartingOne() throws InterruptedException {
        final int threadCount = 100;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final ConcurrentSkipListSet<Long> values = new ConcurrentSkipListSet<>();

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                values.add(counterService.getNext());
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify all values are unique and sequential
        assertEquals(threadCount, values.size());
        assertEquals(1, values.first());
        assertEquals(threadCount, values.last());
    }
}
