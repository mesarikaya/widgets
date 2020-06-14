package com.mes.widgets.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by mesar on 6/14/2020
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetDTO {

    @JsonProperty("id")
    private UUID id;
    @NotNull
    private Integer x;
    @NotNull
    private Integer y;
    @JsonProperty("zIndex") // Somehow jackson could not handle the request without this
    private Integer zIndex;
    @NotNull
    @Positive
    private Integer width;
    @NotNull
    @Positive
    private Integer height;
    private Timestamp lastModificationDate;
}
