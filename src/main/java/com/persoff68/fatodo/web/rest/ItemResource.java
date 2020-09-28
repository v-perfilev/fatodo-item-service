package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.mapper.ItemMapper;
import com.persoff68.fatodo.service.ItemService;
import com.persoff68.fatodo.web.rest.vm.ItemVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(ItemResource.ENDPOINT)
@RequiredArgsConstructor
public class ItemResource {

    static final String ENDPOINT = "/api/items";

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> getById(@PathVariable String id) {
        Item item = itemService.getById(id);
        ItemDTO itemDTO = itemMapper.itemToItemDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> create(@Valid @RequestBody ItemVM itemVM) {
        Item item = itemMapper.itemVMToItem(itemVM);
        item = itemService.create(item);
        ItemDTO itemDTO = itemMapper.itemToItemDTO(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> update(@Valid @RequestBody ItemVM itemVM) {
        Item item = itemMapper.itemVMToItem(itemVM);
        item = itemService.update(item);
        ItemDTO itemDTO = itemMapper.itemToItemDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        itemService.delete(id);
        return ResponseEntity.ok().build();
    }

}
