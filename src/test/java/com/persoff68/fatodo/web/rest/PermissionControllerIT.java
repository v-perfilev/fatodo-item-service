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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
public class PermissionControllerIT {
    private static final String ENDPOINT = "/api/permissions";

    private static final UUID USER_ID = UUID.fromString("d2eb0f4f-1736-4361-889b-b6d833dd9815");
    private static final UUID GROUP_CAN_ADMIN_ID = UUID.randomUUID();
    private static final UUID GROUP_CAN_NOTHING_ID = UUID.randomUUID();
    private static final UUID ITEM_CAN_ADMIN_ID = UUID.randomUUID();
    private static final UUID ITEM_CAN_NOTHING_ID = UUID.randomUUID();

    @Autowired
    MockMvc mvc;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        Member member1 = TestMember.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group1 = TestGroup.defaultBuilder().id(GROUP_CAN_ADMIN_ID).members(List.of(member1)).build();
        groupRepository.save(group1);

        Member member2 = TestMember.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().id(GROUP_CAN_NOTHING_ID).members(List.of(member2)).build();
        groupRepository.save(group2);

        Item item1 = TestItem.defaultBuilder().id(ITEM_CAN_ADMIN_ID).groupId(GROUP_CAN_ADMIN_ID).build();
        itemRepository.save(item1);

        Item item2 = TestItem.defaultBuilder().id(ITEM_CAN_NOTHING_ID).groupId(GROUP_CAN_NOTHING_ID).build();
        itemRepository.save(item2);

    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadGroup_true() throws Exception {
        String url = ENDPOINT + "/group/read/" + GROUP_CAN_ADMIN_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadGroup_false() throws Exception {
        String url = ENDPOINT + "/group/read/" + GROUP_CAN_NOTHING_ID;
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
        String url = ENDPOINT + "/group/read/" + GROUP_CAN_ADMIN_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditGroup_true() throws Exception {
        String url = ENDPOINT + "/group/edit/" + GROUP_CAN_ADMIN_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditGroup_false() throws Exception {
        String url = ENDPOINT + "/group/edit/" + GROUP_CAN_NOTHING_ID;
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
        String url = ENDPOINT + "/group/edit/" + GROUP_CAN_ADMIN_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminGroup_true() throws Exception {
        String url = ENDPOINT + "/group/admin/" + GROUP_CAN_ADMIN_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminGroup_false() throws Exception {
        String url = ENDPOINT + "/group/admin/" + GROUP_CAN_NOTHING_ID;
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
        String url = ENDPOINT + "/group/admin/" + GROUP_CAN_ADMIN_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_true() throws Exception {
        String url = ENDPOINT + "/item/read/" + ITEM_CAN_ADMIN_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_false() throws Exception {
        String url = ENDPOINT + "/item/read/" + ITEM_CAN_NOTHING_ID;
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
        String url = ENDPOINT + "/item/read/" + ITEM_CAN_ADMIN_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditItem_true() throws Exception {
        String url = ENDPOINT + "/item/edit/" + ITEM_CAN_ADMIN_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditItem_false() throws Exception {
        String url = ENDPOINT + "/item/edit/" + ITEM_CAN_NOTHING_ID;
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
        String url = ENDPOINT + "/item/edit/" + ITEM_CAN_ADMIN_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItem_true() throws Exception {
        String url = ENDPOINT + "/item/admin/" + ITEM_CAN_ADMIN_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItem_false() throws Exception {
        String url = ENDPOINT + "/item/admin/" + ITEM_CAN_NOTHING_ID;
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
        String url = ENDPOINT + "/item/admin/" + ITEM_CAN_ADMIN_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminGroups_true() throws Exception {
        String url = ENDPOINT + "/groups/admin";
        List<UUID> groupIdList = List.of(GROUP_CAN_ADMIN_ID);
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
        List<UUID> groupIdList = List.of(GROUP_CAN_NOTHING_ID);
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
        List<UUID> groupIdList = List.of(GROUP_CAN_ADMIN_ID);
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminItems_true() throws Exception {
        String url = ENDPOINT + "/items/admin";
        List<UUID> itemIdList = List.of(ITEM_CAN_ADMIN_ID);
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
        List<UUID> itemIdList = List.of(ITEM_CAN_NOTHING_ID);
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
        List<UUID> itemIdList = List.of(ITEM_CAN_ADMIN_ID);
        String requestBody = objectMapper.writeValueAsString(itemIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

}
