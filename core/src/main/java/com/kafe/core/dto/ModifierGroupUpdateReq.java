package com.kafe.core.dto;

import jakarta.validation.constraints.*;

public class ModifierGroupUpdateReq {
  @NotBlank @Size(max=120) public String name;
  public boolean required = false;
  @Min(1) public int maxSelect = 1;
}
