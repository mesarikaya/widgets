package com.mes.widgets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.widgets.domains.inMemory.Widget;
import com.mes.widgets.domains.inMemory.WidgetList;
import com.mes.widgets.services.WidgetService;
import com.mes.widgets.web.model.WidgetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("in_memory")
class WidgetControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WidgetService widgetService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WidgetList widgetList;
    Map<UUID, Widget> map;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
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
    void getInitCount(){
        assertEquals(104, map.size());
    }

    @Test
    void getWidget() throws Exception {
        UUID id = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        WidgetDTO validWidgetDto = modelMapper.map(
                map.get(id), WidgetDTO.class);

        mockMvc.perform(get("/api/v1/widget/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getWidgetWithNullId() throws Exception {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            UUID id = null;
            WidgetDTO validWidgetDto = null;

            mockMvc.perform(get("/api/v1/widget/{id}", null)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        });
    }


    @Test
    void getWidgets() throws Exception {
        UUID id = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        WidgetDTO validWidgetDto = modelMapper.map(
                map.get(id), WidgetDTO.class);
        LinkedHashMap<UUID, WidgetDTO> widgetDTOMap = new LinkedHashMap<>();
        widgetDTOMap.put(validWidgetDto.getId(), validWidgetDto);

        mockMvc.perform(get("/api/v1/widgets", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteWidget() throws Exception {
        UUID id = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        mockMvc.perform(delete("/api/v1/widget/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void saveNewWidget() throws Exception {
        WidgetDTO validWidgetDto = WidgetDTO.builder()
                .height(60)
                .width(20)
                .x(20)
                .y(20)
                .zIndex(3)
                .build();

        mockMvc.perform(post("/api/v1/widget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validWidgetDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void saveNewWidgetWithNullValues() throws Exception {
        AssertionError exception = assertThrows(AssertionError.class, () -> {
            WidgetDTO validWidgetDto = WidgetDTO.builder()
                            .zIndex(3)
                            .build();

            mockMvc.perform(post("/api/v1/widget")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validWidgetDto)))
                    .andExpect(status().isCreated());
        });
    }

    @Test
    void updateWidget() throws Exception {
        UUID id = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
        WidgetDTO validWidgetDto = WidgetDTO.builder()
                .height(60)
                .width(20)
                .x(20)
                .y(20)
                .zIndex(3)
                .build();

        mockMvc.perform(put("/api/v1/widget/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validWidgetDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateWidgetWithNullValues() throws Exception {
        AssertionError exception = assertThrows(AssertionError.class, () -> {
            UUID id = UUID.fromString("23400000-8cf0-11bd-b23e-10b96e4ef00d");
            WidgetDTO validWidgetDto = WidgetDTO.builder()
                    .height(60)
                    .width(20)
                    .zIndex(3)
                    .build();

            mockMvc.perform(put("/api/v1/widget/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validWidgetDto)))
                    .andExpect(status().isCreated());
        });
    }
}