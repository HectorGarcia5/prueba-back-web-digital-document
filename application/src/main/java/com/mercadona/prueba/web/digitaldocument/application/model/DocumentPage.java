package com.mercadona.prueba.web.digitaldocument.application.model;

import java.util.List;

public record DocumentPage(
    List<DigitalDocumentView> content,
    long totalElements,
    int page,
    int size
) {}
