package com.persoff68.fatodo.web.rest;


import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.dto.MemberDTO;
import com.persoff68.fatodo.model.mapper.MemberMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(MemberController.ENDPOINT)
@RequiredArgsConstructor
public class MemberController {

    static final String ENDPOINT = "/api/members";

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @GetMapping(value = "/group/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDTO>> getMembersByGroupId(@PathVariable UUID id) {
        List<Member> memberList = memberService.getMembersByGroupId(id);
        List<MemberDTO> dtoList = memberList.stream()
                .map(memberMapper::pojoToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(value = "/item/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDTO>> getMembersByItemId(@PathVariable UUID id) {
        List<Member> memberList = memberService.getMembersByItemId(id);
        List<MemberDTO> dtoList = memberList.stream()
                .map(memberMapper::pojoToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

}
