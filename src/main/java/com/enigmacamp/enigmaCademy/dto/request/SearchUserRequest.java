package com.enigmacamp.enigmaCademy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchUserRequest {
    // for filtering
    private String name;
    private String email;
    private String userRole;
    private String createdAt;

    // for pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortOrder;
}
