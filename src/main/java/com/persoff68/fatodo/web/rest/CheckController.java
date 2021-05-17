package com.persoff68.fatodo.web.rest;


import com.persoff68.fatodo.service.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(CheckController.ENDPOINT)
@RequiredArgsConstructor
public class CheckController {

    static final String ENDPOINT = "/api/check";

    private final CheckService checkService;

    @GetMapping(value = "/is-group/{id}")
    public ResponseEntity<Boolean> isGroup(@PathVariable UUID id) {
        boolean isGroup = checkService.isGroup(id);
        return ResponseEntity.ok(isGroup);
    }

    @GetMapping(value = "/is-item/{id}")
    public ResponseEntity<Boolean> isItem(@PathVariable UUID id) {
        boolean isItem = checkService.isItem(id);
        return ResponseEntity.ok(isItem);
    }

}
