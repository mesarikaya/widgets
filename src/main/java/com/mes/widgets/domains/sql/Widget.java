package com.mes.widgets.domains.sql;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.context.annotation.Profile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Profile({"h2db", "mysqldb"})
@Entity
public class Widget {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type="org.hibernate.type.UUIDCharType")
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
