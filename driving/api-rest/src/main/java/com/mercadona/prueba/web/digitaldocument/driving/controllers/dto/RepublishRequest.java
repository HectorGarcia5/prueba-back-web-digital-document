package com.mercadona.prueba.web.digitaldocument.driving.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class RepublishRequest {

  @NotNull
  private UUID documentId;

  @NotBlank
  private String employeeId;

  @NotBlank
  private String managedGroupId;
}
