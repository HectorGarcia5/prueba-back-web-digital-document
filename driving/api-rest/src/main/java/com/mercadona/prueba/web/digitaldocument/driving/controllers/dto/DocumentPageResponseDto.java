package com.mercadona.prueba.web.digitaldocument.driving.controllers.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DocumentPageResponseDto {

  List<DigitalDocumentResponseDto> content;
  long totalElements;
  int page;
  int size;
}
