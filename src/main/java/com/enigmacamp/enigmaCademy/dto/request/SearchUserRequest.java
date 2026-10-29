package com.enigmacamp.enigmaCademy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRequest {
    // filtering fields
    private String name;
    private String username;
    private String email;
    private String userRole;
    private String createdAt;

    // pagination dan sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
