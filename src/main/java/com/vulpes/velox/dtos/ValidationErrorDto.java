package com.vulpes.velox.dtos;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorDto {
  public String status;
  public List<String> message = new ArrayList<>();
}
