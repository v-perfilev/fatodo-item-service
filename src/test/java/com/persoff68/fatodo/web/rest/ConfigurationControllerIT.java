package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Configuration;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class ConfigurationControllerIT {
    private static final String ENDPOINT = "/api/configuration";

    private static final String USER_ID = "d2eb0f4f-1736-4361-889b-b6d833dd9815";

    @Autowired
    MockMvc mvc;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ConfigurationRepository configurationRepository;
    @Autowired
    ObjectMapper objectMapper;

    Group group1;
    Group group2;
    Group group3;

    @BeforeEach
    void setup() {
        group1 = TestGroup.defaultBuilder().build().toParent();
        Member member1 = TestMember.defaultBuilder()
                .group(group1)
                .userId(UUID.fromString(USER_ID)).permission(Permission.ADMIN)
                .build().toParent();
        group1.setMembers(List.of(member1));

        group2 = TestGroup.defaultBuilder().build().toParent();
        Member member2 = TestMember.defaultBuilder()
                .group(group2)
                .userId(UUID.fromString(USER_ID)).permission(Permission.ADMIN)
                .build().toParent();
        group2.setMembers(List.of(member2));

        group3 = TestGroup.defaultBuilder().build().toParent();
        Member member3 = TestMember.defaultBuilder()
                .group(group3)
                .userId(UUID.fromString(USER_ID)).permission(Permission.ADMIN)
                .build().toParent();
        group3.setMembers(List.of(member3));

        group1 = groupRepository.save(group1);
        group2 = groupRepository.save(group2);
        group3 = groupRepository.save(group3);
    }

    @AfterEach
    void cleanup() {
        configurationRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID)
    void testSetOrder_ok() throws Exception {
        List<UUID> groupIdList = List.of(group3.getId(), group1.getId(), group2.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        String url = ENDPOINT + "/order";
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());

        String groupUrl = "/api/group";
        ResultActions resultActions = mvc.perform(get(groupUrl))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, GroupDTO.class);
        PageableList<GroupDTO> resultDTOList = objectMapper.readValue(resultString, type);

        assertThat(resultDTOList.getCount()).isEqualTo(3);
        assertThat(resultDTOList.getData().get(0).getId()).isEqualTo(group3.getId());
        assertThat(resultDTOList.getData().get(1).getId()).isEqualTo(group1.getId());
        assertThat(resultDTOList.getData().get(2).getId()).isEqualTo(group2.getId());

        List<Configuration> configurationList = configurationRepository.findAll();
        assertThat(configurationList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID)
    void testSetOrder_ok_notFullList() throws Exception {
        List<UUID> groupIdList = List.of(group3.getId(), group2.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        String url = ENDPOINT + "/order";
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());

        String groupUrl = "/api/group";
        ResultActions resultActions = mvc.perform(get(groupUrl))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, GroupDTO.class);
        PageableList<GroupDTO> resultDTOList = objectMapper.readValue(resultString, type);

        assertThat(resultDTOList.getCount()).isEqualTo(3);
        assertThat(resultDTOList.getData().get(0).getId()).isEqualTo(group3.getId());
        assertThat(resultDTOList.getData().get(1).getId()).isEqualTo(group2.getId());
        assertThat(resultDTOList.getData().get(2).getId()).isEqualTo(group1.getId());
    }

    @Test
    @WithAnonymousUser
    void testSetOrder_unauthorized() throws Exception {
        List<UUID> groupIdList = List.of(group3.getId(), group1.getId(), group2.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        String url = ENDPOINT + "/order";
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

}
