package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.ImageServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.ImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageServiceClient imageServiceClient;

    public String createGroup(byte[] image) {
        String filename = null;
        if (image != null) {
            ImageDTO imageDTO = new ImageDTO(null, image);
            filename = imageServiceClient.createGroupImage(imageDTO);
        }
        return filename;
    }

    public String updateGroup(Group oldGroup, Group newGroup, byte[] image) {
        String filename = oldGroup.getImageFilename();
        String newFilename = newGroup.getImageFilename();
        if (image != null && filename == null) {
            ImageDTO imageDTO = new ImageDTO(null, image);
            filename = imageServiceClient.createGroupImage(imageDTO);
        } else if (image != null) {
            ImageDTO imageDTO = new ImageDTO(filename, image);
            filename = imageServiceClient.updateGroupImage(imageDTO);
        } else if (filename != null && newFilename == null) {
            imageServiceClient.deleteGroupImage(filename);
        }
        return filename;
    }

    public void deleteGroup(Group group) {
        if (group.getImageFilename() != null) {
            imageServiceClient.deleteGroupImage(group.getImageFilename());
        }
    }

}
