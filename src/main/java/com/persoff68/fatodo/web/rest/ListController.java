package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(ListController.ENDPOINT)
@RequiredArgsConstructor
public class ListController {
    static final String ENDPOINT = "/api/list";

    public static final int DEFAULT_ITEMS_LENGTH = 20;

    private final ListService listService;
    private final ItemMapper itemMapper;

    @GetMapping
    public ResponseEntity<PageableList<ItemDTO>> getAll(@RequestParam(required = false) Integer offset,
                                                        @RequestParam(required = false) Integer size) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        offset = Optional.ofNullable(offset).orElse(0);
        size = Optional.ofNullable(size).orElse(DEFAULT_ITEMS_LENGTH);
        Pageable pageRequest = OffsetPageRequest.of(offset, size);
        PageableList<Item> pageableList = listService.getAllPageable(userId, pageRequest);
        PageableList<ItemDTO> dtoPageableList = pageableList.convert(itemMapper::pojoToDTO);
        return ResponseEntity.ok(dtoPageableList);
    }

}
