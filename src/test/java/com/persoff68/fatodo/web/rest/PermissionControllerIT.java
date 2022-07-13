package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class PermissionControllerIT {
    private static final String ENDPOINT = "/api/permissions";

    private static final UUID USER_ID = UUID.fromString("d2eb0f4f-1736-4361-889b-b6d833dd9815");

    @Autowired
    MockMvc mvc;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    Group group1;
    Group group2;
    Item item1;
    Item item2;

    @BeforeEach
    @Transactional
    void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        group1 = TestGroup.defaultBuilder().build().toParent();
        Member member1 = TestMember.defaultBuilder()
                .group(group1).userId(USER_ID).permission(Permission.ADMIN).build().toParent();
        item1 = TestItem.defaultBuilder().group(group1).build().toParent();
        group1.setMembers(List.of(member1));
        group1.setItems(List.of(item1));
        group1 = groupRepository.save(group1);
        item1 = group1.getItems().get(0);

        group2 = TestGroup.defaultBuilder().build().toParent();
        Member member2 = TestMember.defaultBuilder().group(group2).build().toParent();
        item2 = TestItem.defaultBuilder().group(group2).build().toParent();
        group2.setMembers(List.of(member2));
        group2.setItems(List.of(item2));
        group2 = groupRepository.save(group2);
        item2 = group2.getItems().get(0);
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testGetGroupIdsForMember_ok() throws Exception {
        String url = ENDPOINT + "/groups";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionLikeType(List.class, UUID.class);
        List<UUID> groupIdList = objectMapper.readValue(resultString, javaType);
        assertThat(groupIdList).hasSize(1).contains(group1.getId());
    }

    @Test
    @WithAnonymousUser
    void testGetGroupIdsForMember_unauthorized() throws Exception {
        String url = ENDPOINT + "/groups";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasGroupsPermission_admin() throws Exception {
        String url = ENDPOINT + "/groups/ADMIN";
        List<UUID> groupIdList = List.of(group1.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasGroupsPermission_edit() throws Exception {
        String url = ENDPOINT + "/groups/EDIT";
        List<UUID> groupIdList = List.of(group1.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasGroupsPermission_read() throws Exception {
        String url = ENDPOINT + "/groups/READ";
        List<UUID> groupIdList = List.of(group1.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasGroupsPermission_noPermissions() throws Exception {
        String url = ENDPOINT + "/groups/ADMIN";
        List<UUID> groupIdList = List.of(group2.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasGroupsPermission_notFound() throws Exception {
        String url = ENDPOINT + "/groups/ADMIN";
        List<UUID> groupIdList = List.of(group1.getId(), UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testHasGroupsPermission_unauthorized() throws Exception {
        String url = ENDPOINT + "/groups/ADMIN";
        List<UUID> groupIdList = List.of(group1.getId(), UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasItemsPermission_admin() throws Exception {
        String url = ENDPOINT + "/items/ADMIN";
        List<UUID> groupIdList = List.of(item1.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasItemsPermission_edit() throws Exception {
        String url = ENDPOINT + "/items/EDIT";
        List<UUID> groupIdList = List.of(item1.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasItemsPermission_read() throws Exception {
        String url = ENDPOINT + "/items/READ";
        List<UUID> groupIdList = List.of(item1.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasItemsPermission_noPermissions() throws Exception {
        String url = ENDPOINT + "/items/ADMIN";
        List<UUID> groupIdList = List.of(item2.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testHasItemsPermission_notFound() throws Exception {
        String url = ENDPOINT + "/items/ADMIN";
        List<UUID> groupIdList = List.of(item1.getId(), UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testHasItemsPermission_unauthorized() throws Exception {
        String url = ENDPOINT + "/items/ADMIN";
        List<UUID> groupIdList = List.of(group1.getId(), UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

}
