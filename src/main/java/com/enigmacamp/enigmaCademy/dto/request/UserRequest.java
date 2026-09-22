package com.enigmacamp.enigmaCademy.dto.request;

import com.enigmacamp.enigmaCademy.utils.validators.ValidLmsEmail;
import com.enigmacamp.enigmaCademy.utils.validators.ValidUserRole;
import com.enigmacamp.enigmaCademy.utils.validators.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidLmsEmail
public class UserRequest {
    @NotBlank(message = "nama tidak boleh kosong", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private String name;

    @NotBlank(message = "username tidak boleh kosong", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private String username;

    @NotBlank
    @Email(message = "email tidak sesuai aturan", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Size(max = 40, groups = ValidationGroups.OnCreate.class)
    private String email;

    @NotBlank(message = "Password Tidak Boleh Kosong", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private String password;

    @NotBlank
    @ValidUserRole(groups = {ValidationGroups.OnCreate.class})
    private String userRole;
}
