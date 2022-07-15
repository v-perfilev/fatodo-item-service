package com.persoff68.fatodo.model.vm;

import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupVM implements Serializable {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private UUID id;

    @NotNull
    private String title;

    @NotNull
    private String color;

    private String imageFilename;

    private CommonsMultipartFile imageContent;

}
