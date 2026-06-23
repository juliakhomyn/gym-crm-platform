package com.gym.crm.gateway.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FallbackResponse {
    Integer errorCode;
    String message;
}
