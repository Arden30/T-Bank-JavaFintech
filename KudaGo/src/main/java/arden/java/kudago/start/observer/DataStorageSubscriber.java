package arden.java.kudago.start.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataStorageSubscriber implements Subscriber {
    @Override
    public void update(String message) {
        log.info("All {} were saved in DB", message);
    }
}
