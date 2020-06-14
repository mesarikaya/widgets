package com.mes.widgets.domains.inMemory;

import lombok.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by mesar on 6/14/2020
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
@Profile("in_memory")
public class Widget {

    @NotNull
    private UUID id;
    private int x;
    private int y;
    private int zIndex;
    @Positive
    private int width;
    @Positive
    private int height;
    private Timestamp lastModificationDate;
}
