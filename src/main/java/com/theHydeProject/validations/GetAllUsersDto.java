package com.theHydeProject.validations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GetAllUsersDto {

  @NotNull
  @Pattern(regexp = "^/d+$")
  private int page;

  @NotNull
  @Pattern(regexp = "^/d+$")
  private int limit;

}
