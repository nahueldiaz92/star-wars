package com.conexa.starwars.annotations.api;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.ErrorResponse;
import com.conexa.starwars.dto.people.PeopleDetailResponse;
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
                description = "Recurso encontrado",
                content = @Content(schema = @Schema(implementation = PeopleDetailResponse.class))
        ),
        @ApiResponse(
                responseCode = "404",
                description = "ID no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Error interno",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
})
public @interface DocumentedDetailResponse {
}
