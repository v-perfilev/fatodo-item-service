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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class CheckControllerIT {
    private static final String ENDPOINT = "/api/check";

    @Autowired
    MockMvc mvc;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    Group group;
    Item item;

    @BeforeEach
    @Transactional
    void setup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();

        group = TestGroup.defaultBuilder().build().toParent();
        item = TestItem.defaultBuilder().group(group).build().toParent();
        group.setItems(List.of(item));
        group = groupRepository.save(group);
        item = group.getItems().get(0);
    }

    @Test
    @WithCustomSecurityContext
    void testIsGroup_true() throws Exception {
        String url = ENDPOINT + "/is-group/" + group.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isTrue();
    }

    @Test
    @WithCustomSecurityContext
    void testIsGroup_false() throws Exception {
        String url = ENDPOINT + "/is-group/" + item.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isFalse();
    }

    @Test
    @WithAnonymousUser
    void testIsGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/is-group/" + group.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext
    void testIsItem_true() throws Exception {
        String url = ENDPOINT + "/is-item/" + item.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isTrue();
    }

    @Test
    @WithCustomSecurityContext
    void testIsItem_false() throws Exception {
        String url = ENDPOINT + "/is-item/" + group.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        boolean isGroup = objectMapper.readValue(resultString, Boolean.class);
        assertThat(isGroup).isFalse();
    }

    @Test
    @WithAnonymousUser
    void testIsItem_unauthorized() throws Exception {
        String url = ENDPOINT + "/is-item/" + item.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
