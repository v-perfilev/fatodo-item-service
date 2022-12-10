package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestItem;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.model.Configuration;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.model.dto.ItemInfoDTO;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
@AutoConfigureMockMvc
class SystemControllerIT {
    private static final String ENDPOINT = "/api/system";

    private static final String ADMIN_ID = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String READ_ID = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";

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

    @MockBean
    ImageServiceClient imageServiceClient;

    Group group1;
    Group group2;
    Item item1;


    @BeforeEach
    void setup() {
        group1 = TestGroup.defaultBuilder().build().toParent();
        group2 = TestGroup.defaultBuilder().build().toParent();

        Member member1 = TestMember.defaultBuilder()
                .group(group1).userId(UUID.fromString(ADMIN_ID)).permission(Permission.ADMIN).build().toParent();
        Member member2 = TestMember.defaultBuilder()
                .group(group2).userId(UUID.fromString(ADMIN_ID)).permission(Permission.ADMIN).build().toParent();
        Member member3 = TestMember.defaultBuilder()
                .group(group2).userId(UUID.fromString(READ_ID)).permission(Permission.READ).build().toParent();

        item1 = TestItem.defaultBuilder().group(group1).build().toParent();

        group1.setItems(List.of(item1));
        group1.setMembers(List.of(member1));
        group2.setMembers(List.of(member2, member3));

        group1 = groupRepository.save(group1);
        group2 = groupRepository.save(group2);

        Configuration configuration = new Configuration(UUID.fromString(ADMIN_ID));
        configurationRepository.save(configuration);
    }

    @AfterEach
    void cleanup() {
        configurationRepository.deleteAll();
        groupRepository.deleteAll();
    }


    @Test
    @WithCustomSecurityContext(authority = "ROLE_SYSTEM")
    void testGetAllItemInfoByIds_ok() throws Exception {
        String url = ENDPOINT + "/item?ids=" + item1.getId();
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, ItemInfoDTO.class);
        List<ItemInfoDTO> groupSummaryDTOList = objectMapper.readValue(resultString, javaType);
        assertThat(groupSummaryDTOList).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext
    void testGetAllItemInfoByIds_wrongPermissions() throws Exception {
        String url = ENDPOINT + "/item?ids=" + item1.getId();
        mvc.perform(get(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllItemInfoByIds_unauthorized() throws Exception {
        String url = ENDPOINT + "/item?ids=" + item1.getId();
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(authority = "ROLE_SYSTEM")
    void testDeleteAccountPermanently_ok() throws Exception {
        String url = ENDPOINT + "/" + ADMIN_ID;
        mvc.perform(delete(url))
                .andExpect(status().isOk());
        List<Group> groupList = groupRepository.findAllByUserId(UUID.fromString(ADMIN_ID));
        List<Configuration> configurationList = configurationRepository.findAll();
        assertThat(groupList).isEmpty();
        assertThat(configurationList).isEmpty();
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    void testGetAllPageable_ok_withParams() throws Exception {
        String url = ENDPOINT + "/" + ADMIN_ID;
        mvc.perform(delete(url))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testGetAllPageable_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + ADMIN_ID;
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

}
