package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.mapper.GroupMapper;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.model.vm.GroupVM;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.GroupService;
import com.persoff68.fatodo.web.rest.exception.InvalidFormException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(GroupController.ENDPOINT)
@RequiredArgsConstructor
public class GroupController {

    static final String ENDPOINT = "/api/group";

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllForMember() {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Group> groupList = groupService.getAllByUserId(userId);
        List<GroupDTO> groupDTOList = groupList.stream()
                .map(groupMapper::pojoToDTO)
                .toList();
        return ResponseEntity.ok(groupDTOList);
    }

    @GetMapping(value = "/{memberId}/member")
    public ResponseEntity<List<GroupDTO>> getAllCommonWithMember(@PathVariable UUID memberId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Group> groupList = groupService.getAllCommonByUserIds(userId, memberId);
        List<GroupDTO> groupDTOList = groupList.stream()
                .map(groupMapper::pojoToDTO)
                .toList();
        return ResponseEntity.ok(groupDTOList);
    }

    @GetMapping(value = "/{groupId}")
    public ResponseEntity<GroupDTO> getById(@PathVariable UUID groupId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Group group = groupService.getById(userId, groupId);
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        return ResponseEntity.ok(groupDTO);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupDTO> create(@ModelAttribute @Valid GroupVM groupVM) {
        Group newGroup = groupMapper.vmToPojo(groupVM);
        byte[] imageContent = getBytesFromMultipartFile(groupVM.getImageContent());
        Group group = groupService.create(newGroup, imageContent);
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDTO);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupDTO> update(@ModelAttribute @Valid GroupVM groupVM) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        Group newGroup = groupMapper.vmToPojo(groupVM);
        byte[] imageContent = getBytesFromMultipartFile(groupVM.getImageContent());
        Group group = groupService.update(userId, newGroup, imageContent);
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        return ResponseEntity.ok(groupDTO);
    }

    @DeleteMapping(value = "/{groupId}")
    public ResponseEntity<Void> delete(@PathVariable UUID groupId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        groupService.delete(userId, groupId);
        return ResponseEntity.ok().build();
    }

    private byte[] getBytesFromMultipartFile(MultipartFile multipartFile) {
        try {
            return multipartFile != null && !multipartFile.isEmpty()
                    ? multipartFile.getBytes()
                    : null;
        } catch (IOException e) {
            throw new InvalidFormException();
        }
    }

}