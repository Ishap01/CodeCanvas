package com.codecanvas.userservice.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    private String email;

    private String newPassword;

    private String confirmPassword;
}