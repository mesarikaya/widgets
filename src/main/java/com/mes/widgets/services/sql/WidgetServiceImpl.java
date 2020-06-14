package com.mes.widgets.services.sql;

import com.mes.widgets.domains.sql.Widget;
import com.mes.widgets.repositories.WidgetRepository;
import com.mes.widgets.services.WidgetService;
import com.mes.widgets.web.Exceptions.NotFoundException;
import com.mes.widgets.web.model.WidgetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * Created by mesar on 6/14/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Profile({"h2db", "mysqldb"})
public class WidgetServiceImpl implements WidgetService {

    private final WidgetRepository widgetRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public synchronized WidgetDTO create(WidgetDTO widgetDTO) {

        widgetDTO.setLastModificationDate(Timestamp.from(Instant.now()));
        List<WidgetDTO> minimumElementList = new ArrayList<>();
        widgetRepository.findAll(Sort.by(Sort.Direction.ASC, "zIndex"))
                .stream()
                .limit(1)
                .forEachOrdered(widgetEntry -> minimumElementList.add(convertToWidgetDTO(widgetEntry)));

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
        List<Widget> widgets = updateZIndices(zIndex);

        // Convert DTO to widget object
        Widget widget = convertToWidget(widgetDTO);
        widgets.add(widget);

        // Update the values
        widgetRepository.saveAll(widgets);

        return widgetDTO;
    }

    private List<Widget> updateZIndices(int zIndex) {
        List<Widget> widgets = new ArrayList<>();
        widgetRepository
                .findAll()
                .forEach(widget -> {
                    int widgetZIndex = widget.getZIndex();
                    if(widgetZIndex>=zIndex){
                        widget.setZIndex(widgetZIndex+1);
                        widgets.add(widget);
                    }
                });

        return widgets;
    }

    private Widget convertToWidget(WidgetDTO widgetDTO){
        Widget widget = modelMapper.map(widgetDTO, Widget.class);
        return widget;
    }

    @Override
    public WidgetDTO getById(UUID id) {
        return convertToWidgetDTO(
                widgetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such widget exists!")));
    }

    private WidgetDTO convertToWidgetDTO(Widget widget){
        WidgetDTO widgetDTO = modelMapper.map(widget, WidgetDTO.class);
        return widgetDTO;
    }

    @Override
    public synchronized WidgetDTO updateById(UUID id, WidgetDTO widgetDTO) {
        List<Widget> widgets = new ArrayList<>();
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
            widgets = updateZIndices(widgetDTO.getZIndex());
        }
        existingWidgetDTO.setLastModificationDate(Timestamp.from(Instant.now()));

        // Update the data
        Widget existingWidget = convertToWidget(existingWidgetDTO);
        widgets.add(existingWidget );
        widgetRepository.saveAll(widgets);

        return existingWidgetDTO;
    }

    @Override
    public void deleteById(UUID id) {
        widgetRepository.deleteById(id);
    }

    @Override
    public LinkedHashMap<UUID, WidgetDTO> getWidgetList() {

        LinkedHashMap<UUID, WidgetDTO> dtoMap = new LinkedHashMap<>();
        widgetRepository.findAll(Sort.by(Sort.Direction.ASC, "zIndex"))
                .stream()
                .forEachOrdered(widgetEntry -> dtoMap.put(widgetEntry.getId(), convertToWidgetDTO(widgetEntry)));

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

        List<Widget> list = widgetRepository.findAll(Sort.by(Sort.Direction.ASC, "zIndex"));

        LinkedHashMap<UUID, WidgetDTO> dtoMap = new LinkedHashMap<>();
        int start = pageSize*pageNumber;
        if (list.size()>start){
            list.stream()
                    .skip(start) // skip the not necessary pages
                    .limit(pageSize)
                    .forEachOrdered(widgetEntry -> dtoMap.put(widgetEntry.getId(), convertToWidgetDTO(widgetEntry)));
        }

        return dtoMap;
    }
}
