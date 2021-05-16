package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.dto.ImageDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class ImageServiceClientWrapper implements ImageServiceClient {

    @Qualifier("imageServiceClient")
    private final ImageServiceClient imageServiceClient;

    @Override
    public String createGroupImage(ImageDTO imageDTO) {
        try {
            return imageServiceClient.createGroupImage(imageDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public String updateGroupImage(ImageDTO imageDTO) {
        try {
            return imageServiceClient.updateGroupImage(imageDTO);
        } catch (FeignException.NotFound e) {
            return imageServiceClient.createGroupImage(imageDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteGroupImage(String filename) {
        try {
            imageServiceClient.deleteGroupImage(filename);
        } catch (FeignException.NotFound e) {
            // skip
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
