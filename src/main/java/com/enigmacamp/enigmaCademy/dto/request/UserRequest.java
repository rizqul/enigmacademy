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

    @NotBlank
    @Email(message = "email tidak sesuai aturan", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    @Size(max = 40, groups = ValidationGroups.OnCreate.class)
    private String email;

    @NotBlank
    @ValidUserRole(groups = {ValidationGroups.OnCreate.class})
    private String userRole;

//    @Min(17)
//    @Max(500)
//    private Integer age;

//    @Pattern(
//            regexp = "^08[0-9]{10,12}"
//    )
//    private String phoneNumber;
}
