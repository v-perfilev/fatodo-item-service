package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FactoryUtils;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
public class ItemResourceIT {
    private static final String ENDPOINT = "/api/items";

    @Autowired
    WebApplicationContext context;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    GroupServiceClient groupServiceClient;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        itemRepository.deleteAll();
        Item item = FactoryUtils.createItem("1", "test_group_id_1", ItemStatus.ACTIVE);
        item.setId("test_id_1");
        itemRepository.save(item);
        item = FactoryUtils.createItem("2", "test_group_id_1", ItemStatus.ACTIVE);
        itemRepository.save(item);
        item = FactoryUtils.createItem("3", "test_group_id_2", ItemStatus.ACTIVE);
        itemRepository.save(item);
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetAllForUser_ok() throws Exception {
        when(groupServiceClient.getAllGroupIdsForUser()).thenReturn(List.of("test_group_id_1"));
        when(groupServiceClient.canRead(any())).thenReturn(true);
        ResultActions resultActions = mvc.perform(get(ENDPOINT))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, ItemDTO.class);
        List<ItemDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList).hasSize(2);
    }

    @Test
    @WithAnonymousUser
    void testGetAllForUser_unauthorized() throws Exception {
        mvc.perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetById_ok() throws Exception {
        when(groupServiceClient.canRead(any())).thenReturn(true);
        String url = ENDPOINT + "/test_id_1";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isEqualTo("test_id_1");
    }

    @Test
    @WithAnonymousUser
    void testGetById_unauthorized() throws Exception {
        String url = ENDPOINT + "/test_id_1";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetById_notFound() throws Exception {
        when(groupServiceClient.canRead(any())).thenReturn(false);
        String url = ENDPOINT + "/test_id_2";
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetById_badRequest_wrongPermission() throws Exception {
        when(groupServiceClient.canRead(any())).thenReturn(false);
        String url = ENDPOINT + "/test_id_1";
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testCreate_created() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(true);
        ItemDTO dto = FactoryUtils.createItemDTO("4", "test_group_id_1", ItemStatus.ACTIVE);
        String requestBody = objectMapper.writeValueAsString(dto);
        ResultActions resultActions = mvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(dto.getTitle());
        assertThat(resultDTO.getBody()).isEqualTo(dto.getBody());
        assertThat(resultDTO.getGroupId()).isEqualTo(dto.getGroupId());
        assertThat(resultDTO.getStatus()).isEqualTo(dto.getStatus());
    }

    @Test
    @WithAnonymousUser
    public void testCreate_unauthorized() throws Exception {
        ItemDTO dto = FactoryUtils.createItemDTO("4", "test_group_id_1", ItemStatus.ACTIVE);
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testCreate_badRequest_invalidModel() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(true);
        ItemDTO dto = FactoryUtils.createItemDTO("4", "test_group_id_1", ItemStatus.ACTIVE);
        dto.setId("test_id");
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testCreate_badRequest_invalid() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(true);
        ItemDTO dto = FactoryUtils.createItemDTO("4", "test_group_id_1", ItemStatus.ACTIVE);
        dto.setBody(null);
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testCreate_badRequest_wrongPermission() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        ItemDTO dto = FactoryUtils.createItemDTO("4", "test_group_id_1", ItemStatus.ACTIVE);
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testUpdate_ok() throws Exception {
        when(groupServiceClient.canEdit(any())).thenReturn(true);
        ItemDTO dto = FactoryUtils.createItemDTO("1", "test_group_id_1", ItemStatus.ACTIVE);
        dto.setId("test_id_1");
        String requestBody = objectMapper.writeValueAsString(dto);
        ResultActions resultActions = mvc.perform(put(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ItemDTO resultDTO = objectMapper.readValue(resultString, ItemDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(dto.getTitle());
        assertThat(resultDTO.getBody()).isEqualTo(dto.getBody());
        assertThat(resultDTO.getGroupId()).isEqualTo(dto.getGroupId());
        assertThat(resultDTO.getStatus()).isEqualTo(dto.getStatus());
    }

    @Test
    @WithAnonymousUser
    public void testUpdate_unauthorized() throws Exception {
        ItemDTO dto = FactoryUtils.createItemDTO("1", "test_group_id_1", ItemStatus.ACTIVE);
        dto.setId("test_id_1");
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testUpdate_notFound() throws Exception {
        ItemDTO dto = FactoryUtils.createItemDTO("1", "test_group_id_1", ItemStatus.ACTIVE);
        dto.setId("test_id_2");
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testUpdate_badRequest_canNotEdit() throws Exception {
        when(groupServiceClient.canEdit(any())).thenReturn(false);
        ItemDTO dto = FactoryUtils.createItemDTO("1", "test_group_id_1", ItemStatus.ACTIVE);
        dto.setId("test_id_1");
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testUpdate_badRequest_canNotAdmin() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        ItemDTO dto = FactoryUtils.createItemDTO("1", "test_group_id_2", ItemStatus.ACTIVE);
        dto.setId("test_id_1");
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    public void testUpdate_badRequest_invalid() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        ItemDTO dto = FactoryUtils.createItemDTO("1", "test_group_id_2", ItemStatus.ACTIVE);
        dto.setId("test_id_1");
        dto.setBody(null);
        String requestBody = objectMapper.writeValueAsString(dto);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testDelete_ok() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(true);
        String url = ENDPOINT + "/test_id_1";
        mvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testDelete_unauthorized() throws Exception {
        String url = ENDPOINT + "/test_id_1";
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testDelete_notFound() throws Exception {
        String url = ENDPOINT + "/test_id_2";
        mvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testDelete_badRequest_wrongPermission() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        String url = ENDPOINT + "/test_id_1";
        mvc.perform(delete(url))
                .andExpect(status().isBadRequest());
    }

}
