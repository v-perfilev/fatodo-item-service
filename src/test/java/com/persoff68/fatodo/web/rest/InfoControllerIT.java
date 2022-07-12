package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.dto.ReminderMessageDTO;
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
class InfoControllerIT {
    private static final String ENDPOINT = "/api/reminders";

    @Autowired
    MockMvc mvc;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ObjectMapper objectMapper;

    Item item;

    @BeforeEach
    @Transactional
    void setup() {
        Group group = TestGroup.defaultBuilder().build().toParent();
        Member member1 = TestMember.defaultBuilder().group(group).build().toParent();
        Member member2 = TestMember.defaultBuilder().group(group).build().toParent();
        item = TestItem.defaultBuilder().group(group).build().toParent();
        group.setMembers(List.of(member1, member2));
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
    @WithCustomSecurityContext(authority = "ROLE_SYSTEM")
    void testGetReminderForItem_ok() throws Exception {
        String url = ENDPOINT + "/item/" + item.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ReminderMessageDTO resultDTO = objectMapper.readValue(resultString, ReminderMessageDTO.class);
        assertThat(resultDTO.getUserIds()).hasSize(2);
        assertThat(resultDTO.getUrl()).contains("/items/");
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_SYSTEM")
    void testGetReminderForItem_notFound() throws Exception {
        String url = ENDPOINT + "/item/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetReminderForItem_forbidden() throws Exception {
        String url = ENDPOINT + "/item/" + item.getId();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetReminderForItem_unauthorized() throws Exception {
        String url = ENDPOINT + "/item/" + item.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
