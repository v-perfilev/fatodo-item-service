package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ElementType;
import com.persoff68.fatodo.model.dto.TypeAndParentDTO;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
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
import java.util.UUID;

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
        group = TestGroup.defaultBuilder().build().toParent();
        item = TestItem.defaultBuilder().group(group).build().toParent();
        group.setItems(List.of(item));
        group = groupRepository.save(group);
        item = group.getItems().get(0);
    }

    @AfterEach
    void cleanup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext
    void testGetTypeAndParent_group() throws Exception {
        String url = ENDPOINT + "/type-and-parent/" + group.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        TypeAndParentDTO typeAndParentDTO = objectMapper.readValue(resultString, TypeAndParentDTO.class);
        assertThat(typeAndParentDTO.getType()).isEqualTo(ElementType.GROUP);
        assertThat(typeAndParentDTO.getParentId()).isEqualTo(group.getId());
    }

    @Test
    @WithCustomSecurityContext
    void testGetTypeAndParent_item() throws Exception {
        String url = ENDPOINT + "/type-and-parent/" + item.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        TypeAndParentDTO typeAndParentDTO = objectMapper.readValue(resultString, TypeAndParentDTO.class);
        assertThat(typeAndParentDTO.getType()).isEqualTo(ElementType.ITEM);
        assertThat(typeAndParentDTO.getParentId()).isEqualTo(group.getId());
    }

    @Test
    @WithCustomSecurityContext
    void testGetTypeAndParent_notFound() throws Exception {
        String url = ENDPOINT + "/type-and-parent/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testIsGroup_unauthorized() throws Exception {
        String url = ENDPOINT + "/type-and-parent/" + group.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
