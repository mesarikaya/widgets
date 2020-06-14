package com.mes.widgets.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.widgets.domains.inMemory.Widget;
import com.mes.widgets.domains.inMemory.WidgetList;
import com.mes.widgets.services.inMemory.WidgetServiceImpl;
import com.mes.widgets.web.Exceptions.NotFoundException;
import com.mes.widgets.web.model.WidgetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@WebMvcTest(WidgetServiceImpl.class)
@ActiveProfiles("in_memory")
class WidgetServiceImplTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    WidgetList widgetList;
    Map<UUID, Widget> map;
    WidgetServiceImpl widgetServiceImpl;

    @BeforeEach
    public void setUp(WebApplicationContext context) {

        MockitoAnnotations.initMocks(this);
        widgetServiceImpl = new WidgetServiceImpl(widgetList, modelMapper);
        map = widgetList.getWidgetList();
        UUID uuid1 = UUID.fromString("12300000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget1 = Widget.builder()
                .id(uuid1)
                .height(50)
                .x(0)
                .y(0)
                .zIndex(1)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        UUID uuid2 = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget2 = Widget.builder()
                .id(uuid2)
                .height(50)
                .x(50)
                .y(50)
                .zIndex(-1)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        UUID uuid3 = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget3 = Widget.builder()
                .id(uuid3)
                .height(50)
                .x(10)
                .y(10)
                .zIndex(0)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        UUID uuid4 = UUID.fromString("54320000-8cf0-11bd-b23e-10b96e4ef00d");
        Widget widget4 = Widget.builder()
                .id(uuid4)
                .height(60)
                .x(20)
                .y(20)
                .zIndex(3)
                .lastModificationDate(Timestamp.from(Instant.now()))
                .build();

        map.put(uuid1, widget1);
        map.put(uuid2, widget2);
        map.put(uuid3, widget3);
        map.put(uuid4, widget4);
    }


    @Test
    void getById(){
        UUID id = UUID.fromString("54320000-8cf0-11bd-b23e-10b96e4ef00d");
        WidgetDTO widgetDto = modelMapper.map(map.get(id), WidgetDTO.class);
        given(widgetList.getWidgetList()).willReturn(map);
        WidgetDTO retrievedWidgetDto = widgetServiceImpl.getById(id);

        assertEquals(widgetDto, retrievedWidgetDto);
    }

    @Test
    void getWidgetList(){
        given(widgetList.getWidgetList()).willReturn(map);
        LinkedHashMap<UUID, WidgetDTO> retrievedWidgetsDto = widgetServiceImpl.getWidgetList();

        List<Integer> expectedZIndices = List.of(-1, 0, 1, 3);

        int count = 0;
        Iterator<Map.Entry<UUID, WidgetDTO>> iterator = retrievedWidgetsDto.entrySet().iterator();
        while(iterator.hasNext()){
            WidgetDTO widgetDTO = iterator.next().getValue();
            assertEquals(expectedZIndices.get(count), widgetDTO.getZIndex());
            count++;
        }

        assertEquals(4, retrievedWidgetsDto.size());
    }

    @Test
    void create(){
        WidgetDTO widgetDto = WidgetDTO.builder()
                .height(10)
                .width(20)
                .x(3)
                .y(4)
                .zIndex(3)
                .build();
        given(widgetList.getWidgetList()).willReturn(map);
        WidgetDTO retrievedWidgetDto = widgetServiceImpl.create(widgetDto);

        assertNotNull(retrievedWidgetDto);
        assertEquals(5, map.size());

        // Check if the z indices are correct and the last item is as expected
        given(widgetList.getWidgetList()).willReturn(map);
        LinkedHashMap<UUID, WidgetDTO> retrievedWidgetsDto = widgetServiceImpl.getWidgetList();
        List<Integer> expectedZIndices = List.of(-1, 0, 1, 3, 4);

        int count = 0;
        Iterator<Map.Entry<UUID, WidgetDTO>> iterator = retrievedWidgetsDto.entrySet().iterator();
        while(iterator.hasNext()){
            WidgetDTO widgetDTO = iterator.next().getValue();
            assertEquals(expectedZIndices.get(count), widgetDTO.getZIndex());
            count++;

            if(!iterator.hasNext()){
                UUID id = UUID.fromString("54320000-8cf0-11bd-b23e-10b96e4ef00d");
                WidgetDTO expectedLastWidgetDto = modelMapper.map(map.get(id), WidgetDTO.class);
                assertEquals(widgetDTO, expectedLastWidgetDto );
            }
        }
    }

    @Test
    void updateById(){
        WidgetDTO widgetDto = WidgetDTO.builder()
                .height(10)
                .width(20)
                .x(3)
                .y(4)
                .zIndex(3)
                .build();
        UUID id = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        given(widgetList.getWidgetList()).willReturn(map);
        WidgetDTO retrievedWidgetDto = widgetServiceImpl.updateById(id, widgetDto);

        assertNotNull(retrievedWidgetDto);
        assertEquals(4, map.size());

        // Check if the z indices are correct and the last item is as expected
        LinkedHashMap<UUID, WidgetDTO> retrievedWidgetsDto = widgetServiceImpl.getWidgetList();
        List<Integer> expectedZIndices = List.of(-1, 1, 3, 4);

        int count = 0;
        Iterator<Map.Entry<UUID, WidgetDTO>> iterator = retrievedWidgetsDto.entrySet().iterator();
        while(iterator.hasNext()){
            WidgetDTO widgetDTO = iterator.next().getValue();
            assertEquals(expectedZIndices.get(count), widgetDTO.getZIndex());
            count++;

            if(!iterator.hasNext()){
                UUID lastItemId = UUID.fromString("54320000-8cf0-11bd-b23e-10b96e4ef00d");
                WidgetDTO expectedLastWidgetDto = modelMapper.map(map.get(lastItemId), WidgetDTO.class);
                assertEquals(widgetDTO, expectedLastWidgetDto );
            }
        }
    }

    @Test
    void updateByIdWithoutZIndex(){
        WidgetDTO widgetDto = WidgetDTO.builder()
                .height(10)
                .width(20)
                .x(3)
                .y(4)
                .build();
        UUID id = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        given(widgetList.getWidgetList()).willReturn(map);
        WidgetDTO retrievedWidgetDto = widgetServiceImpl.updateById(id, widgetDto);

        assertNotNull(retrievedWidgetDto);
        assertEquals(4, map.size());

        // Check if the z indices are correct and the last item is as expected
        LinkedHashMap<UUID, WidgetDTO> retrievedWidgetsDto = widgetServiceImpl.getWidgetList();
        List<Integer> expectedZIndices = List.of(-1,0, 1, 3);

        int count = 0;
        Iterator<Map.Entry<UUID, WidgetDTO>> iterator = retrievedWidgetsDto.entrySet().iterator();
        while(iterator.hasNext()){
            WidgetDTO widgetDTO = iterator.next().getValue();
            assertEquals(expectedZIndices.get(count), widgetDTO.getZIndex());
            count++;

            if(!iterator.hasNext()){
                UUID lastItemId = UUID.fromString("54320000-8cf0-11bd-b23e-10b96e4ef00d");
                WidgetDTO expectedLastWidgetDto = modelMapper.map(map.get(lastItemId), WidgetDTO.class);
                assertEquals(widgetDTO, expectedLastWidgetDto );
            }
        }
    }


    @Test()
    void deleteById(){
        UUID id = UUID.fromString("54320000-8cf0-11bd-b23e-10b96e4ef00d");
        WidgetDTO widgetToDelete = modelMapper.map(map.get(id), WidgetDTO.class);

        given(widgetList.getWidgetList()).willReturn(map);
        widgetServiceImpl.deleteById(id);

        assertEquals(3, map.size());
        assertThrows(NotFoundException.class, () -> widgetServiceImpl.getById(id));
    }
}