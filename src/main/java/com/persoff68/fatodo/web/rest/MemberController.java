package com.persoff68.fatodo.web.rest;


import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.MemberService;
import com.persoff68.fatodo.web.rest.vm.MemberVM;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@RestController
@RequestMapping(MemberController.ENDPOINT)
@RequiredArgsConstructor
public class MemberController {
    static final String ENDPOINT = "/api/members";

    private final MemberService memberService;

    @GetMapping(value = "/group/{groupId}/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> getUserIdsByGroupId(@PathVariable UUID groupId) {
        List<UUID> userIdList = memberService.getUserIdsByGroupId(groupId);
        return ResponseEntity.ok(userIdList);
    }

    @GetMapping(value = "/item/{itemId}/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> getUserIdsByItemId(@PathVariable UUID itemId) {
        List<UUID> userIdList = memberService.getUserIdsByItemId(itemId);
        return ResponseEntity.ok(userIdList);
    }

    @PostMapping(value = "/group/{groupId}/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addMembersToGroup(@PathVariable UUID groupId,
                                                  @RequestBody List<UUID> userIdList) {
        memberService.addMembersToGroup(groupId, userIdList);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/group/{groupId}/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeMembersFromGroup(@PathVariable UUID groupId,
                                                       @RequestBody List<UUID> userIdList) {
        memberService.removeMembersFromGroup(groupId, userIdList);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/group/{groupId}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editGroupMember(@PathVariable UUID groupId,
                                                @RequestBody @Valid MemberVM memberVM) {
        memberService.editGroupMember(groupId, memberVM.getUserId(), memberVM.getPermission());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/group/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable UUID groupId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberService.leaveGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

}
