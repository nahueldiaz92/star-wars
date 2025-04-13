package com.conexa.starwars.annotations.api;

import com.conexa.starwars.dto.ErrorResponse;
import com.conexa.starwars.dto.people.PeopleSearchResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Recursos encontrados",
                content = @Content(schema = @Schema(implementation = PeopleSearchResponse.class))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Parametros incorrectos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Nombre no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DocumentedPaginatedResultResponse {
}
