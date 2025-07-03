package ir.nofitech.counter.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CounterService {
    private final AtomicLong counter = new AtomicLong(1);

    /**
     * Thread-safe incrementing counter
     * @return next value in sequence
     */
    public long getNext() {
        return counter.getAndIncrement();
    }
}
