package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestGroupUser;
import com.persoff68.fatodo.model.Configuration;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
public class ConfigurationControllerIT {
    private static final String ENDPOINT = "/api/configuration";

    private static final UUID USER_ID = UUID.fromString("d2eb0f4f-1736-4361-889b-b6d833dd9815");
    private static final UUID GROUP_1_ID = UUID.fromString("12886ad8-f1a2-487c-a5f1-ff71d63a3b52");
    private static final UUID GROUP_2_ID = UUID.fromString("605db3e3-9320-4ec9-999e-85da23c31e29");
    private static final UUID GROUP_3_ID = UUID.fromString("5ad38dae-1952-4c80-a214-49655323a096");

    @Autowired
    WebApplicationContext context;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ConfigurationRepository configurationRepository;
    @Autowired
    ObjectMapper objectMapper;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group1 = TestGroup.defaultBuilder().id(GROUP_1_ID).users(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().id(GROUP_2_ID).users(List.of(groupUser)).build();
        Group group3 = TestGroup.defaultBuilder().id(GROUP_3_ID).users(List.of(groupUser)).build();

        groupRepository.deleteAll();
        configurationRepository.deleteAll();
        groupRepository.save(group1);
        groupRepository.save(group2);
        groupRepository.save(group3);
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testSetOrder_ok() throws Exception {
        List<UUID> groupIdList = List.of(GROUP_3_ID, GROUP_1_ID, GROUP_2_ID);
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        String url = ENDPOINT + "/order";
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());

        String groupUrl = "/api/groups";
        ResultActions resultActions = mvc.perform(get(groupUrl))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, GroupDTO.class);
        List<GroupDTO> resultDTOList = objectMapper.readValue(resultString, listType);

        assertThat(resultDTOList.size()).isEqualTo(3);
        assertThat(resultDTOList.get(0).getId()).isEqualTo(GROUP_3_ID);
        assertThat(resultDTOList.get(1).getId()).isEqualTo(GROUP_1_ID);
        assertThat(resultDTOList.get(2).getId()).isEqualTo(GROUP_2_ID);

        List<Configuration> configurationList = configurationRepository.findAll();
        assertThat(configurationList.size()).isEqualTo(1);
        assertThat(configurationList.get(0).getUserId()).isEqualTo(USER_ID);
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testSetOrder_ok_notFullList() throws Exception {
        List<UUID> groupIdList = List.of(GROUP_3_ID, GROUP_2_ID);
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        String url = ENDPOINT + "/order";
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());

        String groupUrl = "/api/groups";
        ResultActions resultActions = mvc.perform(get(groupUrl))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, GroupDTO.class);
        List<GroupDTO> resultDTOList = objectMapper.readValue(resultString, listType);

        assertThat(resultDTOList.size()).isEqualTo(3);
        assertThat(resultDTOList.get(0).getId()).isEqualTo(GROUP_3_ID);
        assertThat(resultDTOList.get(1).getId()).isEqualTo(GROUP_2_ID);
        assertThat(resultDTOList.get(2).getId()).isEqualTo(GROUP_1_ID);
    }

    @Test
    @WithAnonymousUser
    void testSetOrder_unauthorized() throws Exception {
        List<UUID> groupIdList = List.of(GROUP_3_ID, GROUP_1_ID, GROUP_2_ID);
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        String url = ENDPOINT + "/order";
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

}
