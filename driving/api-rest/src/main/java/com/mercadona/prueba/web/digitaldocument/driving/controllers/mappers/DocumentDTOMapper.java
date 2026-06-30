package com.mercadona.prueba.web.digitaldocument.driving.controllers.mappers;

import com.mercadona.prueba.web.digitaldocument.application.model.DigitalDocumentView;
import com.mercadona.prueba.web.digitaldocument.application.model.DocumentPage;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DigitalDocumentResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DocumentPageResponseDto;
import com.mercadona.prueba.web.digitaldocument.driving.controllers.dto.DocumentStatusResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentDTOMapper {

  DigitalDocumentResponseDto toResponseDto(DigitalDocumentView view);

  default DocumentStatusResponseDto toStatusDto(DigitalDocumentView view) {
    return DocumentStatusResponseDto.builder()
        .documentId(view.id())
        .status(view.status())
        .failedStep(view.failedStep())
        .updatedAt(view.updatedAt())
        .build();
  }

  default DocumentPageResponseDto toPageDto(DocumentPage page) {
    return DocumentPageResponseDto.builder()
        .content(page.content().stream().map(this::toResponseDto).toList())
        .totalElements(page.totalElements())
        .page(page.page())
        .size(page.size())
        .build();
  }
}
