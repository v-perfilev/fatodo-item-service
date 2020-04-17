package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ItemController.ENDPOINT)
@RequiredArgsConstructor
public class ItemController {

    static final String ENDPOINT = "/api/item";

    private final ItemService itemService;

    @GetMapping("/count/group/{groupId}")
    public ResponseEntity<Integer> getItemsCountByGroupId(@PathVariable String groupId) {
        Integer count = itemService.getCountByGroupId(groupId);
        return ResponseEntity.ok(count);
    }

}
