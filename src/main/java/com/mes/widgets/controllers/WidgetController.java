package com.mes.widgets.controllers;

import com.mes.widgets.services.WidgetService;
import com.mes.widgets.web.model.WidgetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mesar on 6/14/2020
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1")
@RestController
public class WidgetController {

    private final WidgetService widgetService;

    @GetMapping(path = "widget/{id}", produces = {"application/json"})
    public ResponseEntity<WidgetDTO> getWidget(@NotNull @PathVariable(value = "id") UUID id){

        return new ResponseEntity<>(widgetService.getById(id), HttpStatus.OK);
    }

    @GetMapping(path = "widgets", produces = {"application/json"})
    public ResponseEntity<Map<UUID, WidgetDTO>> getWidgets(){

        return new ResponseEntity<>(widgetService.getWidgetList(), HttpStatus.OK);
    }

    @GetMapping(path = "widgets/paged", produces = {"application/json"})
    public ResponseEntity<Map<UUID, WidgetDTO>> getWidgetsWithPagination(@RequestParam(value = "pageNumber") Integer pageNumber,
                                                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){

        return new ResponseEntity<>(widgetService.getWidgetListWithPagination(pageNumber, pageSize), HttpStatus.OK);
    }

    @DeleteMapping(path = "widget/{id}", produces = {"application/json"})
    public ResponseEntity deleteWidget(@PathVariable("id") UUID id){

        widgetService.deleteById(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "widget", produces = {"application/json"})
    public ResponseEntity saveNewWidget(@Valid @RequestBody WidgetDTO widgetDTO){

        return new ResponseEntity(widgetService.create(widgetDTO), HttpStatus.CREATED);
    }

    @PutMapping(path = "widget/{id}", produces = {"application/json"})
    public ResponseEntity  updateWidget(@PathVariable("id")  UUID id, @Valid @RequestBody WidgetDTO widgetDTO){

        return new ResponseEntity(widgetService.updateById(id, widgetDTO), HttpStatus.CREATED);
    }

}
