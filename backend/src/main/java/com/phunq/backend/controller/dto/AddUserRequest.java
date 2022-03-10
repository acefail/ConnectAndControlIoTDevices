package com.phunq.backend.controller.dto;

import lombok.Data;

/**
 * @author phunq3107
 * @since 3/7/2022
 */

@Data
public class AddUserRequest {

  private String username;
  private String password;
}
