package com.mes.widgets.domains.inMemory;

import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mesar on 6/14/2020
 */
@Data
@Component
@Profile("in_memory")
public class WidgetList {

    private Map<UUID, Widget> widgetList;

    public WidgetList() {
        this.widgetList = new HashMap<>();
    }

    public String toString() {
        return "WidgetList(widgetList=" + this.getWidgetList() + ")";
    }
}
