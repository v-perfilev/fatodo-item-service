package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.FactoryUtils;
import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.service.exception.PermissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Item item = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        assertThatThrownBy(() -> permissionValidator.validateCreate(item))
                .isInstanceOf(PermissionException.class);
    }

    @Test
    void testValidateUpdate_moveToAnotherGroup() {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        Item item1 = FactoryUtils.createItem("1", "test_group_id_1", ItemStatus.ACTIVE);
        Item item2 = FactoryUtils.createItem("1", "test_group_id_2", ItemStatus.ACTIVE);
        assertThatThrownBy(() -> permissionValidator.validateUpdate(item2, item1))
                .isInstanceOf(PermissionException.class);
    }

    @Test
    void testValidateUpdate_changeStatus() {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        Item item1 = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        Item item2 = FactoryUtils.createItem("1", "test_group_id", ItemStatus.CLOSED);
        assertThatThrownBy(() -> permissionValidator.validateUpdate(item2, item1))
                .isInstanceOf(PermissionException.class);
    }

    @Test
    void testValidateUpdate_changeOtherValues() {
        when(groupServiceClient.canEdit(any())).thenReturn(false);
        Item item1 = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        Item item2 = FactoryUtils.createItem("2", "test_group_id", ItemStatus.ACTIVE);
        assertThatThrownBy(() -> permissionValidator.validateUpdate(item2, item1))
                .isInstanceOf(PermissionException.class);
    }

    @Test
    void testValidateDelete() {
        when(groupServiceClient.canAdmin(any())).thenReturn(false);
        Item item = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        assertThatThrownBy(() -> permissionValidator.validateDelete(item))
                .isInstanceOf(PermissionException.class);
    }

}
