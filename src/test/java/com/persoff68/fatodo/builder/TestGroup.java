package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TestGroup extends Group {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    TestGroup(UUID id,
              @NotNull String title,
              @NotNull String color,
              String imageFilename,
              boolean deleted,
              List<Member> members,
              List<Item> items) {
        super(title, color, imageFilename, deleted, members, items);
        this.setId(id);
    }

    public static TestGroupBuilder defaultBuilder() {
        return TestGroup.builder()
                .title(DEFAULT_VALUE)
                .color(DEFAULT_VALUE)
                .imageFilename(DEFAULT_VALUE);
    }

    public Group toParent() {
        Group group = new Group();
        group.setId(getId());
        group.setTitle(getTitle());
        group.setColor(getColor());
        group.setImageFilename(getImageFilename());
        group.setMembers(getMembers());
        group.setItems(getItems());
        return group;
    }

}
