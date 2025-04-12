package com.conexa.starwars.dto.register;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}
