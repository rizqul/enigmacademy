package com.enigmacamp.enigmaCademy.dto.request;

import com.enigmacamp.enigmaCademy.utils.validator.ValidEmailUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ValidEmailUser // custom validation
public class UserRequest {
    @NotBlank(message = "name cannot be null or a trail of whitespace")
    private String name;

    @NotBlank
    @Email(message = "email format is not valid")
    private String email;

    private String userRole;

//    another example of create annotation validation
//    @Pattern(regexp = "^08[0-9]{0,13}")
//    private String phoneNumber;

//    @Min(17)
//    private Integer age;
}
