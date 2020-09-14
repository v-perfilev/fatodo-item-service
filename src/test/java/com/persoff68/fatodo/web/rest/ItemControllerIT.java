package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.FactoryUtils;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
public class ItemControllerIT {
    private static final String ENDPOINT = "/api/item";

    @Autowired
    WebApplicationContext context;
    @Autowired
    ItemRepository itemRepository;
    @MockBean
    GroupServiceClient groupServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        itemRepository.deleteAll();
        Item item = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        itemRepository.save(item);
        item = FactoryUtils.createItem("2", "test_group_id", ItemStatus.ACTIVE);
        itemRepository.save(item);
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetItemsCountByGroupId_ok() throws Exception {
        when(groupServiceClient.canRead(any())).thenReturn(true);
        String groupId = "test_group_id";
        String url = ENDPOINT + "/count/group/" + groupId;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        int count = Integer.parseInt(resultString);
        assertThat(count).isEqualTo(2);
    }

    @Test
    @WithAnonymousUser
    void testGetItemsCountByGroupId_unauthorized() throws Exception {
        String groupId = "test_group_id";
        String url = ENDPOINT + "/count/group/" + groupId;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetItemsCountByGroupId_badRequest_notFound() throws Exception {
        String groupId = "test_group_id_notExists";
        String url = ENDPOINT + "/count/group/" + groupId;
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetItemsCountByGroupId_badRequest_wrongPermission() throws Exception {
        when(groupServiceClient.canRead(any())).thenReturn(false);
        String groupId = "test_group_id";
        String url = ENDPOINT + "/count/group/" + groupId;
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

}
