package com.persoff68.fatodo.web.rest;


import com.persoff68.fatodo.model.constant.ElementType;
import com.persoff68.fatodo.model.dto.TypeAndParentDTO;
import com.persoff68.fatodo.service.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
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

    @GetMapping(value = "/type-and-parent/{id}")
    public ResponseEntity<TypeAndParentDTO> getTypeAndParent(@PathVariable UUID id) {
        Pair<ElementType, UUID> typeAndParentId = checkService.getTypeAndParentId(id);
        TypeAndParentDTO dto = new TypeAndParentDTO(typeAndParentId.getFirst(), typeAndParentId.getSecond());
        return ResponseEntity.ok(dto);
    }

}
