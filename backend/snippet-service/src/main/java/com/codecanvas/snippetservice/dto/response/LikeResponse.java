package com.codecanvas.snippetservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {

    private boolean success;
    private String message;
    private long likeCount;
    private boolean liked;
}