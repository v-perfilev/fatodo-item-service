package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ItemResource.ENDPOINT)
@RequiredArgsConstructor
public class ItemResource {

    static final String ENDPOINT = "/items";

    private final ItemService itemService;
    private final ItemMapper itemMapper;

}
