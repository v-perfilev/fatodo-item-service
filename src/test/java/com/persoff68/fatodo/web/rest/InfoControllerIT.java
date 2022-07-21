package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.dto.GroupInfoDTO;
import com.persoff68.fatodo.model.dto.ItemInfoDTO;
import com.persoff68.fatodo.model.dto.ReminderMailInfoDTO;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class InfoControllerIT {
    private static final String ENDPOINT = "/api/info";

    private static final String USER_ID = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";

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
        Member member1 = TestMember.defaultBuilder().userId(UUID.fromString(USER_ID)).group(group).build().toParent();
        Member member2 = TestMember.defaultBuilder().group(group).build().toParent();
        item = TestItem.defaultBuilder().group(group).build().toParent();
        group.setMembers(List.of(member1, member2));
        group.setItems(List.of(item));
        group = groupRepository.save(group);
        item = group.getItems().get(0);

        Group group2 = TestGroup.defaultBuilder().build().toParent();
        Item item2 = TestItem.defaultBuilder().group(group2).build().toParent();
        group2.setItems(List.of(item2));
        groupRepository.save(group2);
    }

    @AfterEach
    void cleanup() {
        groupRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @WithCustomSecurityContext(id = USER_ID)
    void testGetAllGroupInfoByIds_ok() throws Exception {
        String url = ENDPOINT + "/groups";
        List<UUID> groupIdList = List.of(group.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        TypeReference<List<GroupInfoDTO>> typeRef = new TypeReference<>() {
        };
        List<GroupInfoDTO> groupInfoDTOList = objectMapper.readValue(resultString, typeRef);
        assertThat(groupInfoDTOList).hasSize(1);
    }

    @Test
    @WithAnonymousUser
    void testGetAllGroupInfoByIds_unauthorized() throws Exception {
        String url = ENDPOINT + "/groups";
        List<UUID> groupIdList = List.of(group.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = USER_ID)
    void testGetAllItemInfoByIds_ok() throws Exception {
        String url = ENDPOINT + "/items";
        List<UUID> itemIdList = List.of(item.getId());
        String requestBody = objectMapper.writeValueAsString(itemIdList);
        ResultActions resultActions = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        TypeReference<List<ItemInfoDTO>> typeRef = new TypeReference<>() {
        };
        List<ItemInfoDTO> groupSummaryDTOList = objectMapper.readValue(resultString, typeRef);
        assertThat(groupSummaryDTOList).hasSize(1);
    }

    @Test
    @WithAnonymousUser
    void testGetAllItemInfoByIds_unauthorized() throws Exception {
        String url = ENDPOINT + "/items";
        List<UUID> groupIdList = List.of(item.getId());
        String requestBody = objectMapper.writeValueAsString(groupIdList);
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(authority = "ROLE_SYSTEM")
    void testGetReminderMailInfo_ok() throws Exception {
        String url = ENDPOINT + "/item-reminder/" + item.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        ReminderMailInfoDTO resultDTO = objectMapper.readValue(resultString, ReminderMailInfoDTO.class);
        assertThat(resultDTO.getUserIds()).hasSize(2);
        assertThat(resultDTO.getUrl()).contains("/items/");
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_SYSTEM")
    void testGetReminderMailInfo_notFound() throws Exception {
        String url = ENDPOINT + "/item-reminder/" + UUID.randomUUID();
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(authority = "ROLE_USER")
    void testGetReminderMailInfo_forbidden() throws Exception {
        String url = ENDPOINT + "/item-reminder/" + item.getId();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetReminderMailInfo_unauthorized() throws Exception {
        String url = ENDPOINT + "/item-reminder/" + item.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }

}
