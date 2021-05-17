package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
public class CheckControllerIT {
    private static final String ENDPOINT = "/api/check";

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

    @BeforeEach
    public void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        Group group = TestGroup.defaultBuilder().id(GROUP_ID).build();
        groupRepository.save(group);

        Item item = TestItem.defaultBuilder().id(ITEM_ID).build();
        itemRepository.save(item);
    }

    @Test
    @WithCustomSecurityContext
    void testIsGroup_true() throws Exception {
        String url = ENDPOINT + "/is-group/" + GROUP_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isTrue();
    }

    @Test
    @WithCustomSecurityContext
    void testIsGroup_false() throws Exception {
        String url = ENDPOINT + "/is-group/" + ITEM_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isFalse();
    }

    @Test
    @WithAnonymousUser
    void testIsGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/is-group/" + GROUP_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testIsItem_true() throws Exception {
        String url = ENDPOINT + "/is-item/" + ITEM_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isTrue();
    }

    @Test
    @WithCustomSecurityContext
    void testIsItem_false() throws Exception {
        String url = ENDPOINT + "/is-item/" + GROUP_ID;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isFalse();
    }

    @Test
    @WithAnonymousUser
    void testIsItem_unauthorized() throws Exception {
        String url = ENDPOINT + "/is-item/" + ITEM_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
