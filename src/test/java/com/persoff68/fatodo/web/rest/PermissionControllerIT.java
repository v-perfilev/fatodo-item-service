package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestGroupUser;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
public class PermissionControllerIT {
    private static final String ENDPOINT = "/api/permissions";

    private static final UUID USER_ID = UUID.fromString("d2eb0f4f-1736-4361-889b-b6d833dd9815");
    private static final UUID GROUP_CAN_EDIT_ID = UUID.randomUUID();
    private static final UUID GROUP_CAN_NOTHING_ID = UUID.randomUUID();
    private static final UUID ITEM_CAN_EDIT_ID = UUID.randomUUID();
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

        Group.User groupUser1 = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group1 = TestGroup.defaultBuilder().id(GROUP_CAN_EDIT_ID).users(List.of(groupUser1)).build();
        groupRepository.save(group1);

        Group.User groupUser2 = TestGroupUser.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().id(GROUP_CAN_NOTHING_ID).users(List.of(groupUser2)).build();
        groupRepository.save(group2);

        Item item1 = TestItem.defaultBuilder().id(ITEM_CAN_EDIT_ID).groupId(GROUP_CAN_EDIT_ID).build();
        itemRepository.save(item1);

        Item item2 = TestItem.defaultBuilder().id(ITEM_CAN_NOTHING_ID).groupId(GROUP_CAN_NOTHING_ID).build();
        itemRepository.save(item2);
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadGroup_true() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_CAN_EDIT_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadGroup_false() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_CAN_NOTHING_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadGroup_notFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanReadGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_CAN_EDIT_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_true() throws Exception {
        String url = ENDPOINT + "/item/" + ITEM_CAN_EDIT_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isTrue();
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_false() throws Exception {
        String url = ENDPOINT + "/item/" + ITEM_CAN_NOTHING_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(resultString);
        assertThat(result).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadItem_notFound() throws Exception {
        String url = ENDPOINT + "/item/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testCanReadItem_unauthorized() throws Exception {
        String url = ENDPOINT + "/item/" + ITEM_CAN_EDIT_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
