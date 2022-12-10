package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.ItemInfoDTO;
import com.persoff68.fatodo.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(SystemController.ENDPOINT)
@RequiredArgsConstructor
public class SystemController {
    static final String ENDPOINT = "/api/system";

    private final SystemService systemService;
    private final ItemMapper itemMapper;

    @GetMapping(value = "/item")
    public ResponseEntity<List<ItemInfoDTO>> getAllItemInfoByIds(@RequestParam("ids") List<UUID> itemIdList) {
        List<Item> itemList = systemService.getAllItemsByIds(itemIdList);
        List<ItemInfoDTO> dtoList = itemList.stream()
                .map(itemMapper::pojoToInfoDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteAccountPermanently(@PathVariable UUID userId) {
        systemService.deleteAccountPermanently(userId);
        return ResponseEntity.ok().build();
    }

}
