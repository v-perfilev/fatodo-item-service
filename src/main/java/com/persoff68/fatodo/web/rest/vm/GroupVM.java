package com.persoff68.fatodo.web.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupVM {

    private UUID id;

    @NotNull
    private String title;

    @NotNull
    private String color;

    private String imageFilename;

    private MultipartFile imageContent;

}
