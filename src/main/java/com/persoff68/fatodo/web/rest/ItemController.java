package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.mapper.ItemMapper;
import com.persoff68.fatodo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ItemController.ENDPOINT)
@RequiredArgsConstructor
public class ItemController {

    static final String ENDPOINT = "/api/item";

    private final ItemService itemService;
    private final ItemMapper itemMapper;


    @GetMapping(value = "/all-by-group-id/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemDTO>> getByGroupId(@PathVariable String groupId) {
        List<Item> itemList = itemService.getAllByGroupId(groupId);
        List<ItemDTO> itemDTOList = itemList.stream().map(itemMapper::itemToItemDTO).collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOList);
    }

}
