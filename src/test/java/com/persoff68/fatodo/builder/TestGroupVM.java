package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.web.rest.vm.GroupVM;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestGroupVM extends GroupVM {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    TestGroupVM(UUID id, @NotNull String title, @NotNull String color, String imageFilename, MultipartFile imageContent) {
        super(id, title, color, imageFilename, imageContent);
    }

    public static TestGroupVMBuilder defaultBuilder() {
        return TestGroupVM.builder()
                .id(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .color(DEFAULT_VALUE)
                .imageFilename(DEFAULT_VALUE);
    }

}
