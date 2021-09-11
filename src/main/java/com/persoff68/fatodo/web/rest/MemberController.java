package com.persoff68.fatodo.web.rest;


import com.persoff68.fatodo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(MemberController.ENDPOINT)
@RequiredArgsConstructor
public class MemberController {

    static final String ENDPOINT = "/api/members";

    private final MemberService memberService;

    @GetMapping(value = "/group/{id}/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> getUserIdsByGroupId(@PathVariable UUID id) {
        List<UUID> userIdList = memberService.getUserIdsByGroupId(id);
        return ResponseEntity.ok(userIdList);
    }

    @GetMapping(value = "/item/{id}/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> getUserIdsByItemId(@PathVariable UUID id) {
        List<UUID> userIdList = memberService.getUserIdsByItemId(id);
        return ResponseEntity.ok(userIdList);
    }

}
