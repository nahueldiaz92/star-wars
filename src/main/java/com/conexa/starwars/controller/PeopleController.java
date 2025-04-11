package com.conexa.starwars.controller;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.people.People;
import com.conexa.starwars.dto.people.PeopleDetail;
import com.conexa.starwars.service.PeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;
    private final int defaultPageSize = 10;


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

    // Endpoint API JSON
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<ApiListResponse<People>> getAllPeopleApi(
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(peopleService.getAllPeople(page));
    }

    // Endpoint API JSON
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PeopleDetail> getPeopleByIdApi(@PathVariable String id) {
        return ResponseEntity.ok(peopleService.getPeopleById(id));
    }
}
