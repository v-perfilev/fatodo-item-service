package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.service.exception.PermissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PermissionValidatorTest {

    GroupServiceClient groupServiceClient;
    PermissionValidator permissionValidator;

    @BeforeEach
    void setup() {
        groupServiceClient = mock(GroupServiceClient.class);
        permissionValidator = new PermissionValidator(groupServiceClient);
    }

    @Test
    void testValidateGet() {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        assertThatThrownBy(() -> permissionValidator.validateGet(any()))
                .isInstanceOf(PermissionException.class);
    }

    @Test
    void testValidateCreate() {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        List<UUID> groupIdList = List.of(UUID.randomUUID());
        assertThatThrownBy(() -> permissionValidator.validateCreate(groupIdList))
                .isInstanceOf(PermissionException.class);
    }


    @Test
    void testValidateUpdate_changeOtherValues() {
        when(groupServiceClient.canEdit(any())).thenReturn(false);
        List<UUID> groupIdList = List.of(UUID.randomUUID());
        assertThatThrownBy(() -> permissionValidator.validateUpdate(groupIdList))
                .isInstanceOf(PermissionException.class);
    }

    @Test
    void testValidateDelete() {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        List<UUID> groupIdList = List.of(UUID.randomUUID());
        assertThatThrownBy(() -> permissionValidator.validateDelete(groupIdList))
                .isInstanceOf(PermissionException.class);
    }

}
