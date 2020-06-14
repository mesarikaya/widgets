package com.mes.widgets.services.inMemory;

import com.mes.widgets.domains.inMemory.Widget;
import com.mes.widgets.domains.inMemory.WidgetList;
import com.mes.widgets.services.WidgetService;
import com.mes.widgets.web.Exceptions.NotFoundException;
import com.mes.widgets.web.model.WidgetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * Created by mesar on 6/14/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Profile("in_memory")
public class WidgetServiceImpl implements WidgetService {

    private final WidgetList widgetList;
    private final ModelMapper modelMapper;

    @Override
    public synchronized WidgetDTO create(WidgetDTO widgetDTO) {

        // Set a random id
        UUID uuid = UUID.randomUUID();
        Map<UUID, Widget> map = widgetList.getWidgetList();
        while(map.containsKey(uuid)){
            uuid = UUID.randomUUID();
        }
        widgetDTO.setId(uuid);

        widgetDTO.setLastModificationDate(Timestamp.from(Instant.now()));

        List<WidgetDTO> minimumElementList = new ArrayList<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Widget::getZIndex)))
                .limit(1)
                .forEachOrdered(widgetEntry -> minimumElementList.add(convertToWidgetDTO(widgetEntry.getValue())));

        // Set z indices
        int zIndex = 0; //default value in absence of any data
        if(!minimumElementList.isEmpty()){
            WidgetDTO minItem = minimumElementList.get(0);
            zIndex = minItem.getZIndex() - 1;
        }

        if(widgetDTO.getZIndex() != null){
            zIndex = widgetDTO.getZIndex();
        }else{
            widgetDTO.setZIndex(zIndex);
        }

        // Update z indices for th rest if needed
        updateZIndices(zIndex);

        // Convert DTO to widget object
        Widget widget = convertToWidget(widgetDTO);

        // Add to the in memory map
        widgetList.getWidgetList().put(uuid, widget);

        return widgetDTO;
    }

    private void updateZIndices(int zIndex) {
        Map<UUID, Widget> map = widgetList.getWidgetList();

        Iterator<Map.Entry<UUID, Widget>> entries = map.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<UUID, Widget> entry = entries.next();
            int entryZIndex = entry.getValue().getZIndex();
            if(entryZIndex>=zIndex){
                entry.getValue().setZIndex(entryZIndex+1);
                System.out.println("New value" + entry.getValue().getZIndex());
            }
        }
    }

    private Widget convertToWidget(WidgetDTO widgetDTO){
        Widget widget = modelMapper.map(widgetDTO, Widget.class);
        return widget;
    }

    @Override
    public WidgetDTO getById(UUID id) {

        Map<UUID, Widget> map = widgetList.getWidgetList();
        if(!map.containsKey(id)){
            throw new NotFoundException("No such widget exists!");
        }

        Widget widget = map.get(id);

        return convertToWidgetDTO(widget);
    }

    private WidgetDTO convertToWidgetDTO(Widget widget){
        WidgetDTO widgetDTO = modelMapper.map(widget, WidgetDTO.class);
        return widgetDTO;
    }

    @Override
    public synchronized WidgetDTO updateById(UUID id, WidgetDTO widgetDTO) {

        WidgetDTO existingWidgetDTO = this.getById(id);
        // Via enforcing @NotNull in the DTO with validation in the controller,
        // there is no need to check if the attributes are null, except zIndex
        existingWidgetDTO.setX(widgetDTO.getX());
        existingWidgetDTO.setY(widgetDTO.getY());
        existingWidgetDTO.setHeight(widgetDTO.getHeight());
        existingWidgetDTO.setWidth(widgetDTO.getWidth());
        if(widgetDTO.getZIndex() != null){
            existingWidgetDTO.setZIndex(widgetDTO.getZIndex());
            // Update z indices via checking the current entry
            updateZIndices(widgetDTO.getZIndex());
        }
        existingWidgetDTO.setLastModificationDate(Timestamp.from(Instant.now()));

        // Update the data
        Widget existingWidget = widgetList.getWidgetList().get(id);
        existingWidget = convertToWidget(existingWidgetDTO);
        widgetList.getWidgetList().put(existingWidget.getId(), existingWidget);

        return existingWidgetDTO;
    }

    @Override
    public void deleteById(UUID id) {

        Map<UUID, Widget> map = widgetList.getWidgetList();
        map.remove(id);
    }

    @Override
    public LinkedHashMap<UUID, WidgetDTO> getWidgetList() {

        Map<UUID, Widget> map = widgetList.getWidgetList();
        // To enable proper order in the output, LinkedHashMap is chosen
        LinkedHashMap<UUID, WidgetDTO> dtoMap = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Widget::getZIndex)))
                .forEachOrdered(widgetEntry -> dtoMap.put(widgetEntry.getKey(), convertToWidgetDTO(widgetEntry.getValue())));

        return dtoMap;
    }

    @Override
    public LinkedHashMap<UUID, WidgetDTO> getWidgetListWithPagination(Integer pageNumber, Integer pageSize) {

        // Set defaults for page number and page size
        if(pageNumber==null){
            pageNumber=0;
        }

        if(pageSize==null){
            pageSize = 10;
        }else if(pageSize>500){
            pageSize = 500;
        }

        Map<UUID, Widget> map = widgetList.getWidgetList();
        // To enable proper order in the output, LinkedHashMap is chosen
        LinkedHashMap<UUID, WidgetDTO> dtoMap = new LinkedHashMap<>();

        int start = pageSize*pageNumber;
        if (map.size()>start){
            map.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Widget::getZIndex)))
                    .skip(start) // skip the not necessary pages
                    .limit(pageSize)
                    .forEachOrdered(widgetEntry -> dtoMap.put(widgetEntry.getKey(), convertToWidgetDTO(widgetEntry.getValue())));
        }

        return dtoMap;
    }
}
