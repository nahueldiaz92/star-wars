package com.conexa.starwars.controller;

import com.conexa.starwars.annotations.DocumentedListResponse;
import com.conexa.starwars.annotations.DocumentedDetailResponse;
import com.conexa.starwars.annotations.DocumentedPaginatedResultResponse;
import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.PaginatedResult;
import com.conexa.starwars.dto.people.*;
import com.conexa.starwars.service.PeopleService;
import com.conexa.starwars.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/people")
@RequiredArgsConstructor
@Tag(name = "People", description = "Operaciones con personajes de Star Wars")
public class PeopleController {
    private final PeopleService peopleService;

    @Operation(summary = "Obtener todos los personajes paginados")
    @DocumentedListResponse
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<ApiListResponse<People>> getAllPeople(
            @Parameter(description = "Numero de pagina", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Numero de items por pagina", example = "3")
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(peopleService.getAllPeople(page, pageSize));
    }

    @Operation(summary = "Obtener detalles de un personaje por ID")
    @DocumentedDetailResponse
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PeopleDetail> getPeopleById(
            @Parameter(description = "ID del personaje", example = "1")
            @PathVariable String id) {
        return ResponseEntity.ok(peopleService.getPeopleById(id));
    }
    @Operation(summary = "Obtener detalles de personajes filtrando por nombre")
    @DocumentedPaginatedResultResponse
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<PaginatedResult<PeopleResult>> searchPeopleByName(
            @Parameter(description = "Nombre para filtrar", example = "Luke")
            @RequestParam String name,
            @Parameter(description = "Numero de pagina", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Numero de items por pagina", example = "3")
            @RequestParam(defaultValue = "3") int pageSize) {
        return ResponseEntity.ok(peopleService.searchPeopleByName(name, page, pageSize));
    }

    //Endpoints para las vistas

    @GetMapping
    public String getAllPeopleView(
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        ApiListResponse<People> response = peopleService.getAllPeople(page, Constants.DEFAULT_PAGE_SIZE);

        model.addAttribute("peopleList", response.getResults());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", response.getTotal_pages());
        model.addAttribute("hasPrevious", response.getPrevious() != null);
        model.addAttribute("hasNext", response.getNext() != null);

        return "people/list";
    }


    @GetMapping("/{id}")
    public String getPeopleDetailView(@PathVariable String id, Model model) {
        PeopleDetail person = peopleService.getPeopleById(id);
        model.addAttribute("person", person);
        return "people/detail";
    }

    @GetMapping("/search")
    public String searchPeopleView(Model model, @RequestParam(required = false) String name,
                                   @RequestParam(defaultValue = "1") int page) {
        if (name != null && !name.isEmpty()) {
            PaginatedResult<PeopleResult> paginatedResult = peopleService.searchPeopleByName(name, page, Constants.DEFAULT_SEARCH_PAGE_SIZE);
            model.addAttribute("results", paginatedResult.getItems());
            model.addAttribute("currentPage", paginatedResult.getCurrentPage());
            model.addAttribute("totalPages", paginatedResult.getTotalPages());
            model.addAttribute("searchPerformed", true);
        }
        return "people/search";
    }


}
