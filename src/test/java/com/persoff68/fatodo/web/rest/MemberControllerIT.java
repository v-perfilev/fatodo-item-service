package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
public class MemberControllerIT {
    private static final String ENDPOINT = "/api/members";

    private static final String ADMIN_ID = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String READ_USER_ID = "809c285c-c288-4e70-a952-0490200576ce";
    private static final String NEW_USER_ID = "648719b8-e566-44e4-962b-894ca6d2d6d6";
    private static final String GROUP_ID = "35e7896d-5967-4917-abad-f8a5dc01f1ca";
    private static final String WRONG_GROUP_ID = "009cc132-8454-44ee-8ece-2498c4579dd2";
    private static final String ITEM_ID = "c6a427e3-1fc6-407d-83f4-2c143d9196d1";
    private static final String WRONG_ITEM_ID = "54649839-07ef-4b1f-875b-de8a05e25ba8";

    @Autowired
    WebApplicationContext context;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceClient userServiceClient;

    MockMvc mvc;


    @BeforeEach
    public void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        Member member1 = TestMember.defaultBuilder()
                .id(UUID.fromString(ADMIN_ID)).permission(Permission.ADMIN).build();
        Member member2 = TestMember.defaultBuilder()
                .id(UUID.fromString(READ_USER_ID)).build();

        Group group1 = TestGroup.defaultBuilder()
                .id(UUID.fromString(GROUP_ID))
                .members(List.of(member1, member2)).build();
        Group group2 = TestGroup.defaultBuilder()
                .id(UUID.fromString(WRONG_GROUP_ID))
                .members(List.of(member2)).build();

        groupRepository.save(group1);
        groupRepository.save(group2);

        Item item1 = TestItem.defaultBuilder()
                .id(UUID.fromString(ITEM_ID)).groupId(UUID.fromString(GROUP_ID)).build();
        Item item2 = TestItem.defaultBuilder()
                .id(UUID.fromString(WRONG_ITEM_ID)).groupId(UUID.fromString(WRONG_GROUP_ID)).build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        when(userServiceClient.doIdsExist(any())).thenReturn(true);
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetUserIdsByGroupId_ok() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/ids";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, UUID.class);
        List<UUID> userIdList = objectMapper.readValue(resultString, listType);
        assertThat(userIdList.size()).isEqualTo(2);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetUserIdsByGroupId_notFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetUserIdsByGroupId_badRequest_wrongPermission() throws Exception {
        String url = ENDPOINT + "/group/" + WRONG_GROUP_ID + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testGetUserIdsByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    void testGetUserIdsByItemId_ok() throws Exception {
        String url = ENDPOINT + "/item/" + ITEM_ID + "/ids";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, UUID.class);
        List<UUID> memberList = objectMapper.readValue(resultString, listType);
        assertThat(memberList.size()).isEqualTo(2);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    void testGetUserIdsByItemId_notFound() throws Exception {
        String url = ENDPOINT + "/item/" + UUID.randomUUID() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    void testGetUserIdsByItemId_badRequest_wrongPermission() throws Exception {
        String url = ENDPOINT + "/item/" + WRONG_ITEM_ID + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testGetUserIdsByItemId_unauthorized() throws Exception {
        String url = ENDPOINT + "/item/" + ITEM_ID + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testAddMembersToGroup_ok() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        Group group = groupRepository.findById(UUID.fromString(GROUP_ID)).orElseThrow();
        Condition<Member> userCondition = new Condition<>(
                m -> m.getId().equals(UUID.fromString(NEW_USER_ID)) && m.getPermission().equals(Permission.READ),
                "new user"
        );
        assertThat(group.getMembers()).haveExactly(1, userCondition);
    }

    @Test
    @WithCustomSecurityContext(id = READ_USER_ID)
    public void testAddMembersToGroup_badRequest_wrongRole() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testAddMembersToGroup_notFound_groupNotFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testAddMembersToGroup_notFound_userNotFound() throws Exception {
        when(userServiceClient.doIdsExist(any())).thenReturn(false);
        String url = ENDPOINT + "/group/" + GROUP_ID + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    public void testAddMembersToGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testRemoveMembersToGroup_ok() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(READ_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        Group group = groupRepository.findById(UUID.fromString(GROUP_ID)).orElseThrow();
        Condition<Member> userCondition = new Condition<>(
                m -> m.getId().equals(UUID.fromString(READ_USER_ID)),
                "read user"
        );
        assertThat(group.getMembers()).doNotHave(userCondition);
    }

    @Test
    @WithCustomSecurityContext(id = READ_USER_ID)
    public void testRemoveMembersToGroup_badRequest_wrongRole() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.randomUUID()));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testRemoveMembersToGroup_badRequest_invalidGroup() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(ADMIN_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testRemoveMembersToGroup_notFound_groupNotFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(READ_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    public void testRemoveMembersToGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + GROUP_ID + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(READ_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

}
