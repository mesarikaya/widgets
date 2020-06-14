package com.mes.widgets.repositories;

import com.mes.widgets.domains.sql.Widget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Created by mesar on 6/14/2020
 */
public interface WidgetRepository extends JpaRepository<Widget, UUID> {

}
