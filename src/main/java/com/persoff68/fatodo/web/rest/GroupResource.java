package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.mapper.GroupMapper;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.GroupService;
import com.persoff68.fatodo.web.rest.exception.InvalidFormException;
import com.persoff68.fatodo.model.vm.GroupVM;
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
@RequestMapping(GroupResource.ENDPOINT)
@RequiredArgsConstructor
public class GroupResource {

    static final String ENDPOINT = "/api/groups";

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupDTO>> getAllForMember() {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Group> groupList = groupService.getAllByUserId(userId);
        List<GroupDTO> groupDTOList = groupList.stream()
                .map(groupMapper::pojoToDTO)
                .toList();
        return ResponseEntity.ok(groupDTOList);
    }

    @GetMapping(value = "/{memberId}/member", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupDTO>> getAllCommonWithMember(@PathVariable UUID memberId) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Group> groupList = groupService.getAllCommonByUserIds(userId, memberId);
        List<GroupDTO> groupDTOList = groupList.stream()
                .map(groupMapper::pojoToDTO)
                .toList();
        return ResponseEntity.ok(groupDTOList);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupDTO> getById(@PathVariable UUID id) {
        Group group = groupService.getById(id);
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        return ResponseEntity.ok(groupDTO);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupDTO> create(@ModelAttribute @Valid GroupVM groupVM) {
        Group newGroup = groupMapper.vmToPojo(groupVM);
        byte[] imageContent = getBytesFromMultipartFile(groupVM.getImageContent());
        Group group = groupService.create(newGroup, imageContent);
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDTO);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupDTO> update(@ModelAttribute @Valid GroupVM groupVM) {
        Group newGroup = groupMapper.vmToPojo(groupVM);
        byte[] imageContent = getBytesFromMultipartFile(groupVM.getImageContent());
        Group group = groupService.update(newGroup, imageContent);
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        return ResponseEntity.ok(groupDTO);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        groupService.delete(id);
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
