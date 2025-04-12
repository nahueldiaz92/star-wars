package com.conexa.starwars.controller;

import com.conexa.starwars.annotations.DocumentedApiListResponse;
import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.PaginatedResult;
import com.conexa.starwars.dto.people.*;
import com.conexa.starwars.service.PeopleService;
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

    @Operation(summary = "Obtener personajes paginados")
    @DocumentedApiListResponse
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<ApiListResponse<People>> getAllPeopleApi(
            @Parameter(description = "Numero de pagina", example = "1")
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(peopleService.getAllPeople(page));
    }

    @Operation(summary = "Obtener detalles de un personaje por ID")
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PeopleDetail> getPeopleByIdApi(
            @Parameter(description = "ID del personaje", example = "1")
            @PathVariable String id) {
        return ResponseEntity.ok(peopleService.getPeopleById(id));
    }
    @Operation(summary = "Obtener detalles de personajes filtrando por nombre")
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<PaginatedResult<PeopleResult>> searchPeopleByNameApi(
            @Parameter(description = "Nombre para filtrar", example = "Luke")
            @RequestParam(required = false) String name,
            @Parameter(description = "Numero de pagina", example = "1")
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(peopleService.searchPeopleByName(name, page));
    }

    //Endpoints para las vistas

    @GetMapping
    public String getAllPeopleView(
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        ApiListResponse<People> response = peopleService.getAllPeople(page);

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
            PaginatedResult<PeopleResult> paginatedResult = peopleService.searchPeopleByName(name, page);
            model.addAttribute("results", paginatedResult.getItems());
            model.addAttribute("currentPage", paginatedResult.getCurrentPage());
            model.addAttribute("totalPages", paginatedResult.getTotalPages());
            model.addAttribute("searchPerformed", true);
        }
        return "people/search";
    }


}
