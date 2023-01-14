package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestItemArchivedVM;
import com.persoff68.fatodo.builder.TestItemStatusVM;
import com.persoff68.fatodo.builder.TestItemVM;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.vm.ItemArchivedVM;
import com.persoff68.fatodo.model.vm.ItemStatusVM;
import com.persoff68.fatodo.model.vm.ItemVM;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.client.PermissionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class ItemControllerIT {
    private static final String ENDPOINT = "/api/item";

    @Autowired
    MockMvc mvc;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ConfigurationRepository configurationRepository;
    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    PermissionService permissionService;

    @MockBean
    CommentServiceClient commentServiceClient;
    @MockBean
    NotificationServiceClient notificationServiceClient;
    @MockBean
    EventServiceClient eventServiceClient;
    @MockBean
    WsServiceClient wsServiceClient;

    Group group1;
    Group group2;
    Item item1;

    @BeforeEach
    @Transactional
    void setup() {
        group1 = TestGroup.defaultBuilder().build().toParent();
        Member member1 = TestMember.defaultBuilder().group(group1).build().toParent();
        Member member2 = TestMember.defaultBuilder().group(group1).build().toParent();
        item1 = TestItem.defaultBuilder().group(group1).build().toParent();
        Item item2 = TestItem.defaultBuilder().group(group1).build().toParent();
        Item item3 = TestItem.defaultBuilder().group(group1).isArchived(true).build().toParent();
        Item item4 = TestItem.defaultBuilder().group(group1).isArchived(true).build().toParent();
        Item item5 = TestItem.defaultBuilder().group(group1).isDeleted(true).build().toParent();
        group1.setMembers(List.of(member1, member2));
        group1.setItems(List.of(item1, item2, item3, item4, item5));
        group1 = groupRepository.save(group1);
        item1 = group1.getItems().get(0);

        group2 = TestGroup.defaultBuilder().build().toParent();
        Member member3 = TestMember.defaultBuilder().group(group2).build().toParent();
        Member member4 = TestMember.defaultBuilder().group(group2).build().toParent();
        group2.setMembers(List.of(member3, member4));
        group2 = groupRepository.save(group2);
    }

    @AfterEach
    void cleanup() {
        configurationRepository.deleteAll();
        itemRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext
    void testGetMapByGroupIds_ok() throws Exception {
        doReturn(true).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String params = String.join(",", group1.getId().toString(), group2.getId().toString());
        String url = ENDPOINT + "/preview?groupIds=" + params;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        TypeReference<Map<UUID, PageableList<ItemDTO>>> typeRef = new TypeReference<>() {
        };
        Map<UUID, PageableList<ItemDTO>> resultMap = objectMapper.readValue(resultString, typeRef);
        assertThat(resultMap).hasSize(2);
        PageableList<ItemDTO> pageableList1 = resultMap.get(group1.getId());
        assertThat(pageableList1.getCount()).isEqualTo(2);
        assertThat(pageableList1.getData()).hasSize(2);
        PageableList<ItemDTO> pageableList2 = resultMap.get(group2.getId());
        assertThat(pageableList2.getCount()).isZero();
        assertThat(pageableList2.getData()).isEmpty();
    }

    @Test
    @WithCustomSecurityContext
    void testGetMapByGroupIds_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String params = String.join(",", group1.getId().toString(), group2.getId().toString());
        String url = ENDPOINT + "/preview?groupIds=" + params;
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext
    void testGetMapByGroupIds_notFound() throws Exception {
        String params = String.join(",", group1.getId().toString(), UUID.randomUUID().toString());
        String url = ENDPOINT + "/preview?groupIds=" + params;
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testGetMapByGroupIds_unauthorized() throws Exception {
        String params = String.join(",", group1.getId().toString(), group2.getId().toString());
        String url = ENDPOINT + "/preview?groupIds=" + params;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testGetAllByGroupId_ok() throws Exception {
        doReturn(true).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + group1.getId() + "/group";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByGroupId_ok_pageable() throws Exception {
        doReturn(true).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + group1.getId() + "/group?offset=1&size=10";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByGroupId_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + group1.getId() + "/group";
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + group1.getId() + "/group";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testGetAllArchivedByGroupId_ok() throws Exception {
        doReturn(true).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + group1.getId() + "/group/archived";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllArchivedByGroupId_ok_pageable() throws Exception {
        doReturn(true).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + group1.getId() + "/group/archived?offset=1&size=10";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllArchivedByGroupId_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasGroupsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + group1.getId() + "/group/archived";
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllArchivedByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + group1.getId() + "/group/archived";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testGetById_ok() throws Exception {
        doReturn(true).when(permissionService).hasItemsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + item1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(item1.getId());
    }

    @Test
    @WithCustomSecurityContext
    void testGetById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        String url = ENDPOINT + "/" + id;
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testGetById_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasItemsPermission(any(), eq(Permission.READ), any());
        String url = ENDPOINT + "/" + item1.getId();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetById_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + item1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testCreate_created() throws Exception {
        doReturn(true).when(permissionService).hasGroupsPermission(any(), eq(Permission.EDIT), any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(vm.getTitle());
        assertThat(resultDTO.getDescription()).isEqualTo(vm.getDescription());
        assertThat(resultDTO.getRemindersCount()).isEqualTo(1);
        assertThat(resultDTO.getGroupId()).isEqualTo(vm.getGroupId());
    }

    @Test
    @WithCustomSecurityContext
    void testCreate_badRequest_invalidModel() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext
    void testCreate_badRequest_invalid() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().id(null).title(null).groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext
    void testCreate_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasGroupsPermission(any(), eq(Permission.EDIT), any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testCreate_unauthorized() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testUpdate_ok() throws Exception {
        doReturn(true).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        ItemVM vm = TestItemVM.defaultBuilder()
                .id(item1.getId()).groupId(group1.getId()).done(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(vm.getTitle());
        assertThat(resultDTO.isDone()).isEqualTo(true);
        assertThat(resultDTO.getDescription()).isEqualTo(vm.getDescription());
        assertThat(resultDTO.getGroupId()).isEqualTo(vm.getGroupId());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdate_notFound() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdate_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        ItemVM vm = TestItemVM.defaultBuilder().id(item1.getId()).groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdate_badRequest_invalid() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder()
                .id(item1.getId()).title(null).groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testUpdate_unauthorized() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().id(item1.getId()).groupId(group1.getId()).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testUpdateStatus_ok() throws Exception {
        doReturn(true).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().id(item1.getId()).done(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(item1.getId());
        assertThat(resultDTO.isDone()).isEqualTo(true);
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateStatus_notFound() throws Exception {
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().done(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateStatus_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().id(item1.getId()).done(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testUpdateStatus_unauthorized() throws Exception {
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().id(item1.getId()).done(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testUpdateArchived_ok() throws Exception {
        doReturn(true).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().id(item1.getId()).archived(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(item1.getId());
        assertThat(resultDTO.isArchived()).isTrue();
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateArchived_notFound() throws Exception {
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().archived(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateArchived_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().id(item1.getId()).archived(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testUpdateArchived_unauthorized() throws Exception {
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().id(item1.getId()).archived(true).build().toParent();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testDelete_ok() throws Exception {
        doReturn(true).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        String url = ENDPOINT + "/" + item1.getId();
        mvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithCustomSecurityContext
    void testDelete_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        String url = ENDPOINT + "/" + id;
        mvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testDelete_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasItemsPermission(any(), eq(Permission.EDIT), any());
        String url = ENDPOINT + "/" + item1.getId();
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testDelete_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + item1.getId();
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

}
