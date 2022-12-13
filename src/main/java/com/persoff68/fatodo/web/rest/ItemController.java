package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.vm.ItemArchivedVM;
import com.persoff68.fatodo.model.vm.ItemStatusVM;
import com.persoff68.fatodo.model.vm.ItemVM;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
@RequestMapping(ItemController.ENDPOINT)
@RequiredArgsConstructor
public class ItemController {
    static final String ENDPOINT = "/api/item";

    public static final int DEFAULT_ITEMS_LENGTH = 20;
    public static final int PREVIEW_ITEMS_LENGTH = 5;

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping(value = "/preview")
    public ResponseEntity<Map<UUID, PageableList<ItemDTO>>> getMapByGroupIds(
            @RequestParam("groupIds") List<UUID> groupIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Map<UUID, PageableList<Item>> pairMap = itemService.getMapByGroupIds(userId, groupIdList, PREVIEW_ITEMS_LENGTH);
        Map<UUID, PageableList<ItemDTO>> pageableListMap = pairMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().convert(itemMapper::pojoToDTO)));
        return ResponseEntity.ok(pageableListMap);
    }

    @GetMapping(value = "/{groupId}/group")
    public ResponseEntity<PageableList<ItemDTO>> getAllByGroupId(@PathVariable UUID groupId,
                                                                 @RequestParam(required = false) Integer offset,
                                                                 @RequestParam(required = false) Integer size) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_ITEMS_LENGTH);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        PageableList<Item> pageableList = itemService.getAllByGroupId(userId, groupId, pageRequest);
        PageableList<ItemDTO> dtoPageableList = pageableList.convert(itemMapper::pojoToDTO);
        return ResponseEntity.ok(dtoPageableList);
    }

    @GetMapping(value = "/{groupId}/group/archived")
    public ResponseEntity<PageableList<ItemDTO>> getAllArchivedByGroupId(@PathVariable UUID groupId,
                                                                         @RequestParam(required = false) Integer offset,
                                                                         @RequestParam(required = false) Integer size) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_ITEMS_LENGTH);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        PageableList<Item> pageableList = itemService.getAllArchivedByGroupId(userId, groupId, pageRequest);
        PageableList<ItemDTO> dtoPageableList = pageableList.convert(itemMapper::pojoToDTO);
        return ResponseEntity.ok(dtoPageableList);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<ItemDTO> getById(@PathVariable UUID itemId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Item item = itemService.getById(userId, itemId);
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @PostMapping
    public ResponseEntity<ItemDTO> create(@RequestBody @Valid ItemVM itemVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Item item = itemMapper.vmToPojo(itemVM);
        item = itemService.createFirstStep(userId, item, itemVM.getGroupId());
        item = itemService.createSecondStep(userId, item.getId(), itemVM.getReminders());
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDTO);
    }

    @PutMapping
    public ResponseEntity<ItemDTO> update(@RequestBody @Valid ItemVM itemVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Item item = itemMapper.vmToPojo(itemVM);
        item = itemService.update(userId, item, itemVM.getReminders(), itemVM.isDeleteReminders());
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @PutMapping(value = "/status")
    public ResponseEntity<ItemDTO> updateStatus(@RequestBody @Valid ItemStatusVM itemStatusVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        UUID itemId = itemStatusVM.getId();
        ItemStatus status = ItemStatus.valueOf(itemStatusVM.getStatus());
        Item item = itemService.updateStatus(userId, itemId, status);
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @PutMapping(value = "/archived")
    public ResponseEntity<ItemDTO> updateArchived(@RequestBody @Valid ItemArchivedVM itemArchivedVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        UUID itemId = itemArchivedVM.getId();
        boolean archived = itemArchivedVM.isArchived();
        Item item = itemService.updateArchived(userId, itemId, archived);
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        return ResponseEntity.ok(itemDTO);
    }

    @DeleteMapping(value = "/{itemId}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID itemId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        itemService.delete(userId, itemId);
        return ResponseEntity.ok().build();
    }

}
