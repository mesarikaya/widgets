package com.mes.widgets.bootstrap.sql;

import com.mes.widgets.domains.sql.Widget;
import com.mes.widgets.repositories.WidgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Created by mesar on 6/14/2020
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"h2db", "mysqldb"})
public class Bootstrap implements CommandLineRunner {

    private final WidgetRepository widgetRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Widget> widgets = new ArrayList<>();
        Widget widget1 = Widget.builder()
                .height(50)
                .width(60)
                .x(0)
                .y(0)
                .zIndex(1)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        Widget widget2 = Widget.builder()
                .height(50)
                .width(60)
                .x(50)
                .y(50)
                .zIndex(-1)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        Widget widget3 = Widget.builder()
                .height(50)
                .width(60)
                .x(10)
                .y(10)
                .zIndex(0)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        Widget widget4 = Widget.builder()
                .height(60)
                .width(60)
                .x(20)
                .y(20)
                .zIndex(-3)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();
        widgets.addAll(List.of(widget1, widget2, widget3, widget4));

        for (int i=0; i<10; i++){
            Random random = new Random();
            Widget widget = Widget.builder()
                    .height(random.nextInt(50)+1)
                    .width(random.nextInt(50)+1)
                    .x(random.nextInt(50))
                    .y(random.nextInt(50))
                    .zIndex(i+5)
                    .lastModificationDate(Timestamp.from(Instant.now()))
                    .build();
            widgets.add(widget);
        }

        widgetRepository.saveAll(widgets);
        log.info("WidgetList is" + widgetRepository.findAll());
    }
}
