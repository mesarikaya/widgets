package com.mes.widgets.bootstrap.inMemory;

import com.mes.widgets.domains.inMemory.Widget;
import com.mes.widgets.domains.inMemory.WidgetList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by mesar on 6/14/2020
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile("in_memory")
public class Bootstrap implements CommandLineRunner {

    private final WidgetList widgetList;

    @Override
    public void run(String... args) throws Exception {
        log.info("Running run method");
        Map<UUID, Widget> map = widgetList.getWidgetList();
        UUID uuid1 = UUID.fromString("12300000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget1 = Widget.builder()
                .id(uuid1)
                .height(50)
                .width(60)
                .x(0)
                .y(0)
                .zIndex(1)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        UUID uuid2 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget2 = Widget.builder()
                .id(uuid2)
                .height(50)
                .width(60)
                .x(50)
                .y(50)
                .zIndex(-1)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        UUID uuid3 = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget3 = Widget.builder()
                .id(uuid3)
                .height(50)
                .width(60)
                .x(10)
                .y(10)
                .zIndex(0)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        UUID uuid4 = UUID.fromString("54320000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget4 = Widget.builder()
                .id(uuid4)
                .height(60)
                .width(60)
                .x(20)
                .y(20)
                .zIndex(3)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        for (int i=0; i<100; i++){
            Random random = new Random();
            UUID uuid = UUID.randomUUID();
            Widget widget = Widget.builder()
                    .id(uuid)
                    .height(random.nextInt(50)+1)
                    .width(random.nextInt(50)+1)
                    .x(random.nextInt(50))
                    .y(random.nextInt(50))
                    .zIndex(i+5)
                    .lastModificationDate(Timestamp.from(Instant.now()))
                    .build();
            map.put(uuid, widget);
        }

        map.put(uuid1, widget1);
        map.put(uuid2, widget2);
        map.put(uuid3, widget3);
        map.put(uuid4, widget4);
        log.info("Activated!!!!");
        log.info("WidgetList is" + widgetList.getWidgetList());
    }
}
