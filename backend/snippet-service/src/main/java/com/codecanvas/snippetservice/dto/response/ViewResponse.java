package com.codecanvas.snippetservice.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewResponse {

    private boolean success;
    private String message;
    private long viewCount;
    private boolean viewed;
}