package arden.java.kudago.start;

import arden.java.kudago.client.CategoryRestTemplate;
import arden.java.kudago.client.LocationRestTemplate;
import arden.java.kudago.config.ThreadsConfig;
import arden.java.kudago.dto.response.places.CategoryDto;
import arden.java.kudago.dto.response.places.LocationDto;
import arden.java.kudago.exception.GeneralException;
import arden.java.kudago.repository.StorageRepository;
import arden.java.kudago.start.command.FillCategoriesCommand;
import arden.java.kudago.start.command.FillLocationCommand;
import arden.java.kudago.start.observer.DataStorageSubscriber;
import arden.java.kudago.start.observer.Publisher;
import configuration.annotation.logtimexec.LogTimeExec;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
@LogTimeExec
public class StartupLogic implements ApplicationContextAware {
    private final CategoryRestTemplate categoryRestTemplate;
    private final LocationRestTemplate locationRestTemplate;
    private final FillCategoriesCommand fillCategoriesCommand;
    private final FillLocationCommand fillLocationCommand;
    @Qualifier("fixedThreadPool")
    private final ExecutorService fixedThreadPool;
    @Qualifier("scheduledThreadPool")
    private final ScheduledExecutorService scheduledExecutorService;
    private final ThreadsConfig threadsConfig;
    private ApplicationContext applicationContext;
    private final Publisher publisher;
    private final DataStorageSubscriber dataStorageSubscriber;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        log.info("Starting up, preparing to initialize DB with data");
        publisher.subscribe(dataStorageSubscriber);

        scheduledExecutorService.scheduleAtFixedRate(this::fillDB, threadsConfig.delayInSeconds().toSeconds(), threadsConfig.periodInSeconds().toSeconds(), TimeUnit.SECONDS);
    }

    public void fillDB() {
        StartupLogic startupLogic = applicationContext.getBean(StartupLogic.class);

        var tasks = List.<Callable<String>>of(
                startupLogic::fillDBWithLocations,
                startupLogic::fillDBWithCategories
        );

        try {
            fixedThreadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("Interrupted while filling DB, closing", e);
            Thread.currentThread().interrupt();
        }
    }

    public String fillDBWithCategories() {
        Optional<List<CategoryDto>> request = categoryRestTemplate.getAllCategories();
        if (request.isPresent()) {
            List<CategoryDto> categories = request.get();
            fillCategoriesCommand.execute(categories);
            publisher.notifySubscribers("categories");

            return categories.toString();
        } else {
            log.error("Problems with saving categories to DB");
            throw new GeneralException("Categories were not found");
        }
    }

    public String fillDBWithLocations() {
        Optional<List<LocationDto>> request = locationRestTemplate.getLocations();
        if (request.isPresent()) {
            List<LocationDto> locationResponse = request.get();
            fillLocationCommand.execute(locationResponse);
            publisher.notifySubscribers("locations");

            return locationResponse.toString();
        } else {
            log.error("Problems with saving locations to DB");
            throw new GeneralException("No locations found");
        }
    }

    @PreDestroy
    public void shutdownExecutors() {
        scheduledExecutorService.shutdown();
        fixedThreadPool.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
            if (!fixedThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                fixedThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Shutdown interrupted, closing", e);
        }
    }

}
