package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FactoryUtils;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
public class ItemControllerIT {
    private static final String ENDPOINT = "/api/item";

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
        Item item = FactoryUtils.createItem("1", "test_group_id", ItemType.TASK, ItemStatus.ACTIVE);
        itemRepository.save(item);
        item = FactoryUtils.createItem("2", "test_group_id", ItemType.TASK, ItemStatus.ACTIVE);
        itemRepository.save(item);
        item = FactoryUtils.createItem("2", "test_group_id-2", ItemType.TASK, ItemStatus.ACTIVE);
        itemRepository.save(item);
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetAllByGroupId_ok() throws Exception {
        when(groupServiceClient.canRead(any())).thenReturn(true);
        String url = ENDPOINT + "/all-by-group-id/test_group_id";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, ItemDTO.class);
        List<ItemDTO> itemDTOList = objectMapper.readValue(resultString, collectionType);
        assertThat(itemDTOList.size()).isEqualTo(2);
    }

    @Test
    @WithAnonymousUser
    void testGetAllByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/all-by-group-id/test_group_id";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetAllByGroupId_badRequest_wrongPermission() throws Exception {
        when(groupServiceClient.canRead(any())).thenReturn(false);
        String url = ENDPOINT + "/all-by-group-id/test_group_id";
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testDeleteAllByGroupId_ok() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(true);
        String url = ENDPOINT + "/all-by-group-id/test_group_id";
        mvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testDeleteAllByGroupId_unauthorized() throws Exception {
        String url = ENDPOINT + "/all-by-group-id/test_group_id";
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testDeleteAllByGroupId_badRequest_wrongPermission() throws Exception {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        String url = ENDPOINT + "/all-by-group-id/test_group_id";
        mvc.perform(delete(url))
                .andExpect(status().isBadRequest());
    }


}
