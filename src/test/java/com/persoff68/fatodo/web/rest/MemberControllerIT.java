package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.builder.TestMemberVM;
import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.model.vm.MemberVM;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class MemberControllerIT {
    private static final String ENDPOINT = "/api/members";

    private static final String ADMIN_USER_ID = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String READ_USER_ID = "809c285c-c288-4e70-a952-0490200576ce";
    private static final String NEW_USER_ID = "648719b8-e566-44e4-962b-894ca6d2d6d6";

    @Autowired
    MockMvc mvc;

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ContactServiceClient contactServiceClient;

    Group group1;
    Group group2;
    Item item1;
    Item item2;

    @BeforeEach
    void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();


        group1 = TestGroup.defaultBuilder().build().toParent();
        group2 = TestGroup.defaultBuilder().build().toParent();

        Member member1 = TestMember.defaultBuilder()
                .group(group1).userId(UUID.fromString(ADMIN_USER_ID)).permission(Permission.ADMIN).build().toParent();
        Member member2 = TestMember.defaultBuilder()
                .group(group1).userId(UUID.fromString(READ_USER_ID)).build().toParent();
        Member member3 = TestMember.defaultBuilder()
                .group(group2).permission(Permission.ADMIN).build().toParent();

        item1 = TestItem.defaultBuilder()
                .group(group1).build().toParent();
        item2 = TestItem.defaultBuilder()
                .group(group2).build().toParent();

        group1.setMembers(List.of(member1, member2));
        group2.setMembers(List.of(member3));

        group1.setItems(List.of(item1));
        group2.setItems(List.of(item2));

        group1 = groupRepository.save(group1);
        item1 = group1.getItems().get(0);
        group2 = groupRepository.save(group2);
        item2 = group2.getItems().get(0);

        when(contactServiceClient.areUsersInContactList(any())).thenReturn(true);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testGetUserIdsByGroupId_ok() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/ids";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, UUID.class);
        List<UUID> userIdList = objectMapper.readValue(resultString, listType);
        assertThat(userIdList).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testGetUserIdsByGroupId_notFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testGetUserIdsByGroupId_forbidden() throws Exception {
        String url = ENDPOINT + "/group/" + group2.getId() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetUserIdsByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testGetUserIdsByItemId_ok() throws Exception {
        String url = ENDPOINT + "/item/" + item1.getId() + "/ids";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, UUID.class);
        List<UUID> memberList = objectMapper.readValue(resultString, listType);
        assertThat(memberList).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testGetUserIdsByItemId_notFound() throws Exception {
        String url = ENDPOINT + "/item/" + UUID.randomUUID() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testGetUserIdsByItemId_forbidden() throws Exception {
        String url = ENDPOINT + "/item/" + item2.getId() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetUserIdsByItemId_unauthorized() throws Exception {
        String url = ENDPOINT + "/item/" + item1.getId() + "/ids";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testAddMembersToGroup_ok() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        Group group = groupRepository.findById(group1.getId()).orElseThrow();
        Condition<Member> userCondition = new Condition<>(
                m -> m.getUserId().equals(UUID.fromString(NEW_USER_ID)) && m.getPermission().equals(Permission.READ),
                "new user"
        );
        assertThat(group.getMembers()).haveExactly(1, userCondition);
    }

    @Test
    @WithCustomSecurityContext(id = READ_USER_ID)
    void testAddMembersToGroup_forbidden() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testAddMembersToGroup_notFound_groupNotFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testAddMembersToGroup_usersNotAllowed() throws Exception {
        when(contactServiceClient.areUsersInContactList(any())).thenReturn(false);
        String url = ENDPOINT + "/group/" + group1.getId() + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testAddMembersToGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/add";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(NEW_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testRemoveMembersToGroup_ok() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(READ_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        Group group = groupRepository.findById(group1.getId()).orElseThrow();
        Condition<Member> userCondition = new Condition<>(
                m -> m.getUserId().equals(UUID.fromString(READ_USER_ID)),
                "read user"
        );
        assertThat(group.getMembers()).doNotHave(userCondition);
    }

    @Test
    @WithCustomSecurityContext(id = READ_USER_ID)
    void testRemoveMembersToGroup_forbidden() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.randomUUID()));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testRemoveMembersToGroup_badRequest_invalidGroup() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(ADMIN_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testRemoveMembersToGroup_notFound_groupNotFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(READ_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testRemoveMembersToGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/remove";
        String requestBody = objectMapper.writeValueAsString(Collections.singletonList(UUID.fromString(READ_USER_ID)));
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testEditGroupMember_ok() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/edit";
        MemberVM memberVM = TestMemberVM.defaultBuilder()
                .userId(UUID.fromString(READ_USER_ID)).permission(Permission.EDIT).build().toParent();
        String requestBody = objectMapper.writeValueAsString(memberVM);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        Group group = groupRepository.findById(group1.getId()).orElseThrow();
        Condition<Member> userCondition = new Condition<>(
                m -> m.getUserId().equals(UUID.fromString(READ_USER_ID)) && m.getPermission().equals(Permission.EDIT),
                "edit user"
        );
        assertThat(group.getMembers()).haveExactly(1, userCondition);
    }

    @Test
    @WithCustomSecurityContext(id = READ_USER_ID)
    void testEditGroupMember_forbidden() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/edit";
        MemberVM memberVM = TestMemberVM.defaultBuilder()
                .userId(UUID.fromString(ADMIN_USER_ID)).permission(Permission.EDIT).build().toParent();
        String requestBody = objectMapper.writeValueAsString(memberVM);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testEditGroupMember_notFound_groupNotFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/edit";
        MemberVM memberVM = TestMemberVM.defaultBuilder()
                .userId(UUID.fromString(READ_USER_ID)).permission(Permission.EDIT).build().toParent();
        String requestBody = objectMapper.writeValueAsString(memberVM);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testEditGroupMember_notFound_userNotFound() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/edit";
        MemberVM memberVM = TestMemberVM.defaultBuilder()
                .userId(UUID.randomUUID()).permission(Permission.EDIT).build().toParent();
        String requestBody = objectMapper.writeValueAsString(memberVM);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testEditGroupMember_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/edit";
        MemberVM memberVM = TestMemberVM.defaultBuilder()
                .userId(UUID.fromString(READ_USER_ID)).permission(Permission.EDIT).build().toParent();
        String requestBody = objectMapper.writeValueAsString(memberVM);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(id = READ_USER_ID)
    void testLeaveGroup_ok() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/leave";
        mvc.perform(get(url))
                .andExpect(status().isOk());
        Group group = groupRepository.findById(group1.getId()).orElseThrow();
        Condition<Member> userCondition = new Condition<>(
                m -> m.getUserId().equals(UUID.fromString(READ_USER_ID)),
                "read user"
        );
        assertThat(group.getMembers()).doNotHave(userCondition);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testLeaveGroup_badRequest_invalidGroup() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/leave";
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = NEW_USER_ID)
    void testLeaveGroup_forbidden() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/leave";
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_USER_ID)
    void testLeaveGroup_notFound() throws Exception {
        String url = ENDPOINT + "/group/" + UUID.randomUUID() + "/leave";
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testLeaveGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/group/" + group1.getId() + "/leave";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
