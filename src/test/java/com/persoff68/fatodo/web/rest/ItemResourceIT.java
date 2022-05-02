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
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.PermissionService;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.web.rest.vm.ItemArchivedVM;
import com.persoff68.fatodo.web.rest.vm.ItemStatusVM;
import com.persoff68.fatodo.web.rest.vm.ItemVM;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class ItemResourceIT {
    private static final String ENDPOINT = "/api/items";

    private static final UUID GROUP_1_ID = UUID.randomUUID();
    private static final UUID GROUP_2_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();

    @Autowired
    MockMvc mvc;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    PermissionService permissionService;

    @MockBean
    CommentServiceClient commentServiceClient;
    @MockBean
    NotificationServiceClient notificationServiceClient;

    @BeforeEach
    void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        Member member1 = TestMember.defaultBuilder().build();
        Member member2 = TestMember.defaultBuilder().build();
        Group group1 = TestGroup.defaultBuilder().id(GROUP_1_ID).members(List.of(member1, member2)).build();
        Group group2 = TestGroup.defaultBuilder().id(GROUP_2_ID).members(List.of(member1, member2)).build();

        groupRepository.save(group1);
        groupRepository.save(group2);

        Item item1 = TestItem.defaultBuilder().id(ITEM_ID).groupId(GROUP_1_ID).build();
        Item item2 = TestItem.defaultBuilder().groupId(GROUP_1_ID).build();
        Item item3 = TestItem.defaultBuilder().groupId(GROUP_1_ID).archived(true).build();
        Item item4 = TestItem.defaultBuilder().groupId(GROUP_1_ID).archived(true).build();

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.save(item4);

        doNothing().when(commentServiceClient).deleteAllThreadsByTargetIds(any());
        doNothing().when(notificationServiceClient).setReminders(any(), any());
        doNothing().when(notificationServiceClient).deleteReminders(any());
    }

    @Test
    @WithCustomSecurityContext
    void testGetMapByGroupIds_ok() throws Exception {
        doReturn(true).when(permissionService).hasMultipleReadPermission(any());
        String url = ENDPOINT + "/preview/group-ids";
        List<UUID> groupIdList = List.of(GROUP_1_ID, GROUP_2_ID);
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        TypeReference<Map<UUID, PageableList<ItemDTO>>> typeRef = new TypeReference<>() {
        };
        Map<UUID, PageableList<ItemDTO>> resultMap = objectMapper.readValue(resultString, typeRef);
        assertThat(resultMap).hasSize(2);
        PageableList<ItemDTO> pageableList1 = resultMap.get(GROUP_1_ID);
        assertThat(pageableList1.getCount()).isEqualTo(2);
        assertThat(pageableList1.getData()).hasSize(2);
        ItemDTO itemDTO1 = pageableList1.getData().get(0);
        ItemDTO itemDTO2 = pageableList1.getData().get(1);
        assertThat(itemDTO1.getCreatedAt()).isAfter(itemDTO2.getCreatedAt());
        PageableList<ItemDTO> pageableList2 = resultMap.get(GROUP_2_ID);
        assertThat(pageableList2.getCount()).isZero();
        assertThat(pageableList2.getData()).isEmpty();
    }

    @Test
    @WithCustomSecurityContext
    void testGetMapByGroupIds_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasMultipleReadPermission(any());
        String url = ENDPOINT + "/preview/group-ids";
        List<UUID> groupIdList = List.of(GROUP_1_ID, GROUP_2_ID);
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext
    void testGetMapByGroupIds_notFound() throws Exception {
        doThrow(new ModelNotFoundException()).when(permissionService).hasMultipleReadPermission(any());
        String url = ENDPOINT + "/preview/group-ids";
        List<UUID> groupIdList = List.of(GROUP_1_ID, UUID.randomUUID());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testGetMapByGroupIds_unauthorized() throws Exception {
        String url = ENDPOINT + "/preview/group-ids";
        List<UUID> groupIdList = List.of(GROUP_1_ID, GROUP_2_ID);
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testGetAllByGroupId_ok() throws Exception {
        doReturn(true).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/" + GROUP_1_ID + "/group-id";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(2);
        ItemDTO itemDTO1 = resultPageableList.getData().get(0);
        ItemDTO itemDTO2 = resultPageableList.getData().get(1);
        assertThat(itemDTO1.getCreatedAt()).isAfter(itemDTO2.getCreatedAt());
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByGroupId_ok_pageable() throws Exception {
        doReturn(true).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/" + GROUP_1_ID + "/group-id?offset=1&size=10";
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
        doReturn(false).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/" + GROUP_1_ID + "/group-id";
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + GROUP_1_ID + "/group-id";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testGetAllArchivedByGroupId_ok() throws Exception {
        doReturn(true).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/archived/" + GROUP_1_ID + "/group-id";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(2);
        ItemDTO itemDTO1 = resultPageableList.getData().get(0);
        ItemDTO itemDTO2 = resultPageableList.getData().get(1);
        assertThat(itemDTO1.getCreatedAt()).isAfter(itemDTO2.getCreatedAt());
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllArchivedByGroupId_ok_pageable() throws Exception {
        doReturn(true).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/archived/" + GROUP_1_ID + "/group-id?offset=1&size=10";
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
        doReturn(false).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/archived/" + GROUP_1_ID + "/group-id";
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllArchivedByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/archived/" + GROUP_1_ID + "/group-id";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testGetById_ok() throws Exception {
        doReturn(true).when(permissionService).hasReadPermission(any());
        UUID id = ITEM_ID;
        String url = ENDPOINT + "/" + id;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(id);
    }

    @Test
    @WithCustomSecurityContext
    void testGetById_notFound() throws Exception {
        doReturn(false).when(permissionService).hasReadPermission(any());
        UUID id = UUID.randomUUID();
        String url = ENDPOINT + "/" + id;
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testGetById_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/" + ITEM_ID;
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetById_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + ITEM_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testCreate_created() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(vm.getTitle());
        assertThat(resultDTO.getDescription()).isEqualTo(vm.getDescription());
        assertThat(resultDTO.getGroupId()).isEqualTo(vm.getGroupId());
    }

    @Test
    @WithCustomSecurityContext
    void testCreate_badRequest_invalidModel() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext
    void testCreate_badRequest_invalid() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).title(null).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext
    void testCreate_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testCreate_unauthorized() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testUpdate_ok() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(vm.getTitle());
        assertThat(resultDTO.getDescription()).isEqualTo(vm.getDescription());
        assertThat(resultDTO.getGroupId()).isEqualTo(vm.getGroupId());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdate_notFound() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdate_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdate_badRequest_invalid() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).title(null).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testUpdate_unauthorized() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).groupId(GROUP_1_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testUpdateStatus_ok() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().id(ITEM_ID).status("COMPLETED").build();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(ITEM_ID);
        assertThat(resultDTO.getStatus()).isEqualTo(ItemStatus.COMPLETED);
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateStatus_notFound() throws Exception {
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().status("COMPLETED").build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateStatus_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().id(ITEM_ID).status("COMPLETED").build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testUpdateStatus_unauthorized() throws Exception {
        String url = ENDPOINT + "/status";
        ItemStatusVM vm = TestItemStatusVM.defaultBuilder().id(ITEM_ID).status("COMPLETED").build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testUpdateArchived_ok() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().id(ITEM_ID).archived(true).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        ResultActions resultActions = mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(ITEM_ID);
        assertThat(resultDTO.isArchived()).isTrue();
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateArchived_notFound() throws Exception {
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().archived(true).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    void testUpdateArchived_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().id(ITEM_ID).archived(true).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testUpdateArchived_unauthorized() throws Exception {
        String url = ENDPOINT + "/archived";
        ItemArchivedVM vm = TestItemArchivedVM.defaultBuilder().id(ITEM_ID).archived(true).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testDelete_ok() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        String url = ENDPOINT + "/" + ITEM_ID;
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
        doReturn(false).when(permissionService).hasEditPermission(any());
        String url = ENDPOINT + "/" + ITEM_ID;
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testDelete_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + ITEM_ID;
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testDeleteAllByGroupId_ok() throws Exception {
        doReturn(true).when(permissionService).hasAdminPermission(any());
        String url = ENDPOINT + "/" + GROUP_1_ID + "/group-id";
        mvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithCustomSecurityContext
    void testDeleteAllByGroupId_forbidden() throws Exception {
        doReturn(false).when(permissionService).hasAdminPermission(any());
        String url = ENDPOINT + "/" + GROUP_1_ID + "/group-id";
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testDeleteAllByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + GROUP_1_ID + "/group-id";
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

}
