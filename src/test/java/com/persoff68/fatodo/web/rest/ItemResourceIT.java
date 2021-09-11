package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestItemVM;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.PermissionService;
import com.persoff68.fatodo.web.rest.vm.ItemVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
public class ItemResourceIT {
    private static final String ENDPOINT = "/api/items";

    private static final UUID GROUP_ID = UUID.randomUUID();
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

    @BeforeEach
    void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        Member member1 = TestMember.defaultBuilder().build();
        Member member2 = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder()
                .id(GROUP_ID).members(List.of(member1, member2)).build();

        groupRepository.save(group);

        Item item1 = TestItem.defaultBuilder().id(ITEM_ID).groupId(GROUP_ID).build();
        Item item2 = TestItem.defaultBuilder().groupId(GROUP_ID).build();

        itemRepository.save(item1);
        itemRepository.save(item2);
    }


    @Test
    @WithCustomSecurityContext
    void testGetAllByGroupId_ok() throws Exception {
        doReturn(true).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/" + GROUP_ID + "/group-id";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, ItemDTO.class);
        List<ItemDTO> itemDTOList = objectMapper.readValue(resultString, collectionType);
        assertThat(itemDTOList.size()).isEqualTo(2);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllByGroupId_badRequest_wrongPermission() throws Exception {
        doReturn(false).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/" + GROUP_ID + "/group-id";
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testGetAllByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + GROUP_ID + "/group-id";
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
    void testGetById_badRequest_wrongPermission() throws Exception {
        doReturn(false).when(permissionService).hasReadPermission(any());
        String url = ENDPOINT + "/" + ITEM_ID;
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
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
    public void testCreate_created() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(GROUP_ID).build();
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
    public void testCreate_badRequest_invalidModel() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext
    public void testCreate_badRequest_invalid() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).title(null).groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext
    public void testCreate_badRequest_wrongPermission() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    public void testCreate_unauthorized() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().id(null).groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    public void testUpdate_ok() throws Exception {
        doReturn(true).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).groupId(GROUP_ID).build();
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
    public void testUpdate_notFound() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext
    public void testUpdate_badRequest_canNotEdit() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext
    public void testUpdate_badRequest_invalid() throws Exception {
        doReturn(false).when(permissionService).hasEditPermission(any());
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).title(null).groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    public void testUpdate_unauthorized() throws Exception {
        ItemVM vm = TestItemVM.defaultBuilder().id(ITEM_ID).groupId(GROUP_ID).build();
        String requestBody = objectMapper.writeValueAsString(vm);
        mvc.perform(put(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testDelete_ok() throws Exception {
        doReturn(true).when(permissionService).hasAdminPermission(any());
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
    void testDelete_badRequest_wrongPermission() throws Exception {
        doReturn(false).when(permissionService).hasAdminPermission(any());
        String url = ENDPOINT + "/" + ITEM_ID;
        mvc.perform(delete(url))
                .andExpect(status().isBadRequest());
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
        String url = ENDPOINT + "/" + GROUP_ID + "/group-id";
        mvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithCustomSecurityContext
    void testDeleteAllByGroupId_badRequest_wrongPermission() throws Exception {
        doReturn(false).when(permissionService).hasAdminPermission(any());
        String url = ENDPOINT + "/" + GROUP_ID + "/group-id";
        mvc.perform(delete(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testDeleteAllByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + GROUP_ID + "/group-id";
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

}
