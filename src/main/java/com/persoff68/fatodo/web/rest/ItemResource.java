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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ItemResource.ENDPOINT)
@RequiredArgsConstructor
public class ItemResource {

    static final String ENDPOINT = "/api/items";

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping(value = "/{groupId}/group-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemDTO>> getAllByGroupId(@PathVariable UUID groupId) {
        List<Item> itemList = itemService.getAllByGroupId(groupId);
        List<ItemDTO> itemDTOList = itemList.stream().map(itemMapper::pojoToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOList);
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> getById(@PathVariable UUID id) {
        Item item = itemService.getById(id);
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> create(@RequestBody @Valid ItemVM itemVM) {
        Item item = itemMapper.vmToPojo(itemVM);
        item = itemService.create(item, itemVM.getReminders());
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDTO);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDTO> update(@RequestBody @Valid ItemVM itemVM) {
        Item item = itemMapper.vmToPojo(itemVM);
        item = itemService.update(item, itemVM.getReminders(), itemVM.isDeleteReminders());
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        itemService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{groupId}/group-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAllByGroupId(@PathVariable UUID groupId) {
        itemService.deleteAllByGroupId(groupId);
        return ResponseEntity.ok().build();
    }


}
