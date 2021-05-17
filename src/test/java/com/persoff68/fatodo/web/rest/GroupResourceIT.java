package com.persoff68.fatodo.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.TestUtils;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestGroupUser;
import com.persoff68.fatodo.builder.TestGroupVM;
import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.service.ItemService;
import com.persoff68.fatodo.web.rest.vm.GroupVM;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
public class GroupResourceIT {
    private static final String ENDPOINT = "/api/groups";

    private static final String ADMIN_ID = "3c300277-b5ea-48d1-80db-ead620cf5846";
    private static final String READ_ID = "357a2a99-7b7e-4336-9cd7-18f2cf73fab9";
    private static final String GROUP_ID = "35e7896d-5967-4917-abad-f8a5dc01f1ca";
    private static final String WRONG_GROUP_ID = "009cc132-8454-44ee-8ece-2498c4579dd2";

    @Autowired
    WebApplicationContext context;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;
    @MockBean
    ImageServiceClient imageServiceClient;

    MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        Group.User groupUser1 = TestGroupUser.defaultBuilder()
                .id(UUID.fromString(ADMIN_ID)).permission(Permission.ADMIN).build();
        Group.User groupUser2 = TestGroupUser.defaultBuilder()
                .permission(Permission.ADMIN).build();
        Group.User groupUser3 = TestGroupUser.defaultBuilder()
                .id(UUID.fromString(READ_ID)).permission(Permission.READ).build();

        Group group1 = TestGroup.defaultBuilder()
                .users(List.of(groupUser1)).build();
        Group group2 = TestGroup.defaultBuilder()
                .id(UUID.fromString(GROUP_ID)).users(List.of(groupUser1, groupUser3)).build();
        Group group3 = TestGroup.defaultBuilder()
                .id(UUID.fromString(WRONG_GROUP_ID)).users(List.of(groupUser2)).build();

        groupRepository.deleteAll();
        groupRepository.save(group1);
        groupRepository.save(group2);
        groupRepository.save(group3);

        when(imageServiceClient.createGroupImage(any())).thenReturn("filename");
        when(imageServiceClient.updateGroupImage(any())).thenReturn("filename");
        doNothing().when(imageServiceClient).deleteGroupImage(any());
        doNothing().when(itemService).deleteAllByGroupId(any());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    void testGetAllForUser_ok() throws Exception {
        ResultActions resultActions = mvc.perform(get(ENDPOINT))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, GroupDTO.class);
        List<GroupDTO> resultDTOList = objectMapper.readValue(resultString, listType);
        assertThat(resultDTOList.size()).isEqualTo(2);
    }

    @Test
    @WithAnonymousUser
    void testGetAllForUser_unauthorized() throws Exception {
        mvc.perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetById_ok() throws Exception {
        UUID id = UUID.fromString(GROUP_ID);
        String url = ENDPOINT + "/" + id;
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        GroupDTO resultDTO = objectMapper.readValue(resultString, GroupDTO.class);
        assertThat(resultDTO.getId()).isEqualTo(id);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetById_badRequest_wrongUser() throws Exception {
        String url = ENDPOINT + "/" + WRONG_GROUP_ID;
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        String url = ENDPOINT + "/" + id;
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void testGetById_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + GROUP_ID;
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetUserIdsById_ok() throws Exception {
        UUID id = UUID.fromString(GROUP_ID);
        String url = ENDPOINT + "/" + id + "/user-ids";
        ResultActions resultActions = mvc.perform(get(url))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, UUID.class);
        List<UUID> userIdList = objectMapper.readValue(resultString, listType);
        assertThat(userIdList.size()).isEqualTo(2);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetUserIdsById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        String url = ENDPOINT + "/" + id + "/user-ids";
        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testGetUserIdsById_badRequest_wrongUser() throws Exception {
        String url = ENDPOINT + "/" + WRONG_GROUP_ID + "/user-ids";
        mvc.perform(get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void testGetUserIdsById_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + GROUP_ID + "/user-ids";
        mvc.perform(get(url))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testCreate_created() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(null).build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        ResultActions resultActions = mvc.perform(post(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        GroupDTO resultDTO = objectMapper.readValue(resultString, GroupDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(vm.getTitle());
        assertThat(resultDTO.getColor()).isEqualTo(vm.getColor());
        assertThat(resultDTO.getUsers()).hasSize(1);
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testCreate_badRequest_alreadyExists() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(UUID.fromString(GROUP_ID)).build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testCreate_badRequest_invalid() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(null).title(null).build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testCreate_badRequest_idSet() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    public void testCreate_unauthorized() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(null).build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(post(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testUpdate_ok() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(UUID.fromString(GROUP_ID)).title("test").build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        ResultActions resultActions = mvc.perform(put(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        GroupDTO resultDTO = objectMapper.readValue(resultString, GroupDTO.class);
        assertThat(resultDTO.getId()).isNotNull();
        assertThat(resultDTO.getTitle()).isEqualTo(vm.getTitle());
        assertThat(resultDTO.getUsers()).hasSize(2);
    }

    @Test
    @WithCustomSecurityContext
    public void testUpdate_badRequest_wrongUser() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(UUID.fromString(GROUP_ID)).title("test").build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = READ_ID)
    public void testUpdate_badRequest_wrongPermission() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(UUID.fromString(GROUP_ID)).title("test").build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testUpdate_badRequest_notExists() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(UUID.randomUUID()).title("test").build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testUpdate_badRequest_invalidId() throws Exception {
        GroupVM vm = new GroupVM();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    public void testUpdate_unauthorized() throws Exception {
        GroupVM vm = TestGroupVM.defaultBuilder().id(UUID.fromString(GROUP_ID)).title("test").build();
        MultiValueMap<String, String> multiValueMap = TestUtils.objectToMap(vm);
        mvc.perform(put(ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA).params(multiValueMap))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testDelete_ok() throws Exception {
        String url = ENDPOINT + "/" + GROUP_ID;
        mvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithCustomSecurityContext(id = ADMIN_ID)
    public void testDelete_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        String url = ENDPOINT + "/" + id;
        mvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithCustomSecurityContext(id = "3c300277-b5ea-48d1-80db-ead620cf5846")
    public void testDelete_badRequest_wrongUser() throws Exception {
        String url = ENDPOINT + "/" + WRONG_GROUP_ID;
        mvc.perform(delete(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    public void testDelete_unauthorized() throws Exception {
        String url = ENDPOINT + "/" + GROUP_ID;
        mvc.perform(delete(url))
                .andExpect(status().isUnauthorized());
    }

}
