package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ItemResource.ENDPOINT)
@RequiredArgsConstructor
public class ItemResource {

    static final String ENDPOINT = "/items";

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemDTO>> getAllByGroupIds(@RequestBody List<String> groupIds) {
        List<Item> itemList = itemService.getAllByGroupIds(groupIds);
        List<ItemDTO> itemDTOList = itemList.stream().map(itemMapper::itemToItemDTO).collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOList);
    }

    @GetMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> getById(@PathVariable String id) {
        Item item = itemService.getById(id);
        ItemDTO itemDTO = itemMapper.itemToItemDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> create(@Valid @RequestBody ItemDTO itemDTO) {
        Item item = itemMapper.itemDTOToItem(itemDTO);
        item = itemService.create(item);
        itemDTO = itemMapper.itemToItemDTO(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> update(@Valid @RequestBody ItemDTO itemDTO) {
        Item item = itemMapper.itemDTOToItem(itemDTO);
        item = itemService.update(item);
        itemDTO = itemMapper.itemToItemDTO(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDTO);
    }

    @PostMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        itemService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
