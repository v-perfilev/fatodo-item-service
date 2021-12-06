package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.mapper.ItemMapper;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.service.ItemService;
import com.persoff68.fatodo.web.rest.vm.ItemVM;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ItemResource.ENDPOINT)
@RequiredArgsConstructor
public class ItemResource {
    static final String ENDPOINT = "/api/items";

    private static final int DEFAULT_SIZE = 10;

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping(value = "/preview/group-ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<UUID, PageableList<ItemDTO>>> getPreviewByGroupIds(@RequestBody List<UUID> groupIdList) {
        Map<UUID, PageableList<Item>> pairMap = itemService.getFirstPagesByGroupIds(groupIdList);
        Map<UUID, PageableList<ItemDTO>> pageableListMap = pairMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> mapItemListToDTOList(entry.getValue())));
        return ResponseEntity.ok(pageableListMap);
    }

    @GetMapping(value = "/{groupId}/group-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageableList<ItemDTO>> getAllByGroupId(@PathVariable UUID groupId,
                                                                 @RequestParam(required = false) Integer offset,
                                                                 @RequestParam(required = false) Integer size) {
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        PageableList<Item> pageableList = itemService.getAllByGroupId(groupId, pageRequest);
        PageableList<ItemDTO> dtoPageableList = mapItemListToDTOList(pageableList);
        return ResponseEntity.ok(dtoPageableList);
    }

    @GetMapping(value = "/archived/{groupId}/group-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageableList<ItemDTO>> getArchivedByGroupId(@PathVariable UUID groupId,
                                                                      @RequestParam(required = false) Integer offset,
                                                                      @RequestParam(required = false) Integer size) {
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        PageableList<Item> pageableList = itemService.getAllArchivedByGroupId(groupId, pageRequest);
        PageableList<ItemDTO> dtoPageableList = mapItemListToDTOList(pageableList);
        return ResponseEntity.ok(dtoPageableList);
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

    private PageableList<ItemDTO> mapItemListToDTOList(PageableList<Item> pageableList) {
        long count = pageableList.getCount();
        List<ItemDTO> dtoList = pageableList.getData().stream()
                .map(itemMapper::pojoToDTO)
                .collect(Collectors.toList());
        return PageableList.of(dtoList, count);
    }

}
