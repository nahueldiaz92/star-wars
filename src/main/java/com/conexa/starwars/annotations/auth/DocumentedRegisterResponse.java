package com.conexa.starwars.annotations.auth;

import com.conexa.starwars.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Exito"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Formato no valido",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "409",
                description = "El email ya se encuentra en uso",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DocumentedRegisterResponse {
}
