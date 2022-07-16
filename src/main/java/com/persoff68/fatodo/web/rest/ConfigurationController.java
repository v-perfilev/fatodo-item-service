package com.persoff68.fatodo.web.rest;


import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ConfigurationController.ENDPOINT)
@RequiredArgsConstructor
public class ConfigurationController {

    static final String ENDPOINT = "/api/configuration";

    private final ConfigurationService configurationService;

    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> setOrder(@RequestBody List<UUID> groupIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        configurationService.setOrder(userId, groupIdList);
        return ResponseEntity.ok().build();
    }

}
