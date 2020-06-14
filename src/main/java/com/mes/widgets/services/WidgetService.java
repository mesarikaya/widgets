package com.mes.widgets.services;

import com.mes.widgets.web.model.WidgetDTO;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by mesar on 6/14/2020
 */
public interface WidgetService {

    WidgetDTO getById(UUID id);
    LinkedHashMap<UUID, WidgetDTO> getWidgetList();
    WidgetDTO create(WidgetDTO widgetDTO);
    WidgetDTO updateById(UUID id, WidgetDTO widgetDTO);
    void deleteById(UUID id);
    LinkedHashMap<UUID, WidgetDTO> getWidgetListWithPagination(Integer pageNumber, Integer pageSize);
}
