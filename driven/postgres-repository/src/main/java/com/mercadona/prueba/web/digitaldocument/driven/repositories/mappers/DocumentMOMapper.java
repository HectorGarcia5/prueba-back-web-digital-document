package com.mercadona.prueba.web.digitaldocument.driven.repositories.mappers;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.driven.repositories.models.DigitalDocumentQueryMO;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper from {@link DigitalDocumentQueryMO} to {@link DigitalDocumentView}.
 */
@Mapper(componentModel = "spring")
public interface DocumentMOMapper {

  /**
   * Maps a database MO to the application domain view.
   *
   * @param mo the JPA managed object
   * @return the domain view
   */
  DigitalDocumentView toView(DigitalDocumentQueryMO mo);
}
