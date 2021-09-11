package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestGroup extends Group {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    TestGroup(UUID id,
              @NotNull String title,
              @NotNull String color,
              String imageFilename,
              @NotNull List<Member> members) {
        super(title, color, imageFilename, members);
        this.setId(id);
    }

    public static TestGroupBuilder defaultBuilder() {
        return TestGroup.builder()
                .id(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .color(DEFAULT_VALUE)
                .imageFilename(DEFAULT_VALUE)
                .members(Collections.emptyList());
    }

}
