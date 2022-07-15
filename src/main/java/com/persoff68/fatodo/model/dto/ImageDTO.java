package com.persoff68.fatodo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ImageDTO {

    private String filename;

    @NotNull
    private byte[] content;

}
