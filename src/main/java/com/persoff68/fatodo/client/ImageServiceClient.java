package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignAuthConfiguration;
import com.persoff68.fatodo.model.dto.ImageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "image-service", primary = false,
        configuration = {FeignAuthConfiguration.class},
        qualifiers = {"feignImageServiceClient"})
public interface ImageServiceClient {

    @PostMapping(value = "/api/group-image")
    String createGroupImage(ImageDTO imageDTO);

    @PutMapping(value = "/api/group-image")
    String updateGroupImage(ImageDTO imageDTO);

    @DeleteMapping(value = "/api/group-image/{filename}")
    void deleteGroupImage(@PathVariable String filename);

}
