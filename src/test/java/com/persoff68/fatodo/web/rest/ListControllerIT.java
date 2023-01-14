package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.repository.ConfigurationRepository;
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
class ListControllerIT {
    private static final String ENDPOINT = "/api/list";

    private static final String USER_ID = "3c300277-b5ea-48d1-80db-ead620cf5846";

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

    Group group1;
    Group group2;
    Item item1;

    @BeforeEach
    @Transactional
    void setup() {
        group1 = TestGroup.defaultBuilder().build().toParent();
        Member member1 = TestMember.defaultBuilder().userId(UUID.fromString(USER_ID)).group(group1).build().toParent();
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
        Member member3 = TestMember.defaultBuilder().userId(UUID.fromString(USER_ID)).group(group2).build().toParent();
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
    @WithCustomSecurityContext(id = USER_ID)
    void testGetAll_ok() throws Exception {
        ResultActions resultActions = mvc.perform(get(ENDPOINT))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID)
    void testGetAll_ok_pageable() throws Exception {
        String url = ENDPOINT + "?offset=1&size=10";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageableList.class, ItemDTO.class);
        PageableList<ItemDTO> resultPageableList = objectMapper.readValue(resultString, type);
        assertThat(resultPageableList.getCount()).isEqualTo(2);
        assertThat(resultPageableList.getData()).hasSize(1);
    }

    @Test
    @WithAnonymousUser
    void testGetAll_unauthorized() throws Exception {
        mvc.perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

}
