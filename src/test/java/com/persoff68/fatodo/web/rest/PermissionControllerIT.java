package com.persoff68.fatodo.web.rest;

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
//    private static final UUID GROUP_CAN_ADMIN_ID = UUID.randomUUID();
//    private static final UUID GROUP_CAN_NOTHING_ID = UUID.randomUUID();
//    private static final UUID ITEM_CAN_ADMIN_ID = UUID.randomUUID();
//    private static final UUID ITEM_CAN_NOTHING_ID = UUID.randomUUID();

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
    void testCanReadGroup_true() throws Exception {
        String url = ENDPOINT + "/group/read/" + group1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadGroup_false() throws Exception {
        String url = ENDPOINT + "/group/read/" + group2.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadGroup_notFound() throws Exception {
        String url = ENDPOINT + "/group/read/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanReadGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/read/" + group1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditGroup_true() throws Exception {
        String url = ENDPOINT + "/group/edit/" + group1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditGroup_false() throws Exception {
        String url = ENDPOINT + "/group/edit/" + group2.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditGroup_notFound() throws Exception {
        String url = ENDPOINT + "/group/edit/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanEditGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/edit/" + group1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminGroup_true() throws Exception {
        String url = ENDPOINT + "/group/admin/" + group1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminGroup_false() throws Exception {
        String url = ENDPOINT + "/group/admin/" + group2.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminGroup_notFound() throws Exception {
        String url = ENDPOINT + "/group/admin/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanAdminGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/admin/" + group1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_true() throws Exception {
        String url = ENDPOINT + "/item/read/" + item1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_false() throws Exception {
        String url = ENDPOINT + "/item/read/" + item2.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_notFound() throws Exception {
        String url = ENDPOINT + "/item/read/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanReadItem_unauthorized() throws Exception {
        String url = ENDPOINT + "/item/read/" + item1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditItem_true() throws Exception {
        String url = ENDPOINT + "/item/edit/" + item1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditItem_false() throws Exception {
        String url = ENDPOINT + "/item/edit/" + item2.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditItem_notFound() throws Exception {
        String url = ENDPOINT + "/item/edit/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanEditItem_unauthorized() throws Exception {
        String url = ENDPOINT + "/item/edit/" + item1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItem_true() throws Exception {
        String url = ENDPOINT + "/item/admin/" + item1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItem_false() throws Exception {
        String url = ENDPOINT + "/item/admin/" + item2.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItem_notFound() throws Exception {
        String url = ENDPOINT + "/item/admin/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanAdminItem_unauthorized() throws Exception {
        String url = ENDPOINT + "/item/admin/" + item1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminGroups_true() throws Exception {
        String url = ENDPOINT + "/groups/admin";
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
    void testCanAdminGroups_false() throws Exception {
        String url = ENDPOINT + "/groups/admin";
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
    void testCanAdminGroups_notFound() throws Exception {
        String url = ENDPOINT + "/groups/admin";
        List<UUID> groupIdList = List.of(UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanAdminGroups_unauthorized() throws Exception {
        String url = ENDPOINT + "/groups/admin";
        List<UUID> groupIdList = List.of(group1.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItems_true() throws Exception {
        String url = ENDPOINT + "/items/admin";
        List<UUID> itemIdList = List.of(item1.getId());
        String requestBody = objectMapper.writeValueAsString(itemIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItems_false() throws Exception {
        String url = ENDPOINT + "/items/admin";
        List<UUID> itemIdList = List.of(item2.getId());
        String requestBody = objectMapper.writeValueAsString(itemIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItems_notFound() throws Exception {
        String url = ENDPOINT + "/items/admin";
        List<UUID> itemIdList = List.of(UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(itemIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanAdminItems_unauthorized() throws Exception {
        String url = ENDPOINT + "/items/admin";
        List<UUID> itemIdList = List.of(item1.getId());
        String requestBody = objectMapper.writeValueAsString(itemIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

}
