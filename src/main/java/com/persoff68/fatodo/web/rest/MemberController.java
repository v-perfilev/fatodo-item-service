package com.persoff68.fatodo.web.rest;


import com.persoff68.fatodo.model.vm.MemberVM;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.MemberService;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@RestController
@RequestMapping(MemberController.ENDPOINT)
@RequiredArgsConstructor
public class MemberController {
    static final String ENDPOINT = "/api/member";

    private final MemberService memberService;

    @GetMapping(value = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> getUserIdsByGroupId(@PathVariable UUID groupId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> userIdList = memberService.getUserIdsByGroupId(userId, groupId);
        return ResponseEntity.ok(userIdList);
    }

    @GetMapping(value = "/{itemId}/item", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> getUserIdsByItemId(@PathVariable UUID itemId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> userIdList = memberService.getUserIdsByItemId(userId, itemId);
        return ResponseEntity.ok(userIdList);
    }

    @PostMapping(value = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addMembersToGroup(@PathVariable UUID groupId,
                                                  @RequestBody List<UUID> userIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberService.addMembersToGroup(userId, groupId, userIdList);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{groupId}")
    public ResponseEntity<Void> removeMembersFromGroup(@PathVariable UUID groupId,
                                                       @RequestParam("ids") List<UUID> userIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberService.removeMembersFromGroup(userId, groupId, userIdList);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editGroupMember(@PathVariable UUID groupId,
                                                @RequestBody @Valid MemberVM memberVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberService.editGroupMember(userId, groupId, memberVM.getUserId(), memberVM.getPermission());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable UUID groupId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        memberService.leaveGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

}
