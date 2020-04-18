package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupServiceFallbackFactory implements FallbackFactory<GroupServiceClient> {
    @Override
    public GroupServiceClient create(Throwable throwable) {
        int status = throwable instanceof FeignException
                ? ((FeignException) throwable).status()
                : 0;

        return new GroupServiceClient() {
            @Override
            public boolean canRead(List<String> groupIds) {
                check404(status);
                throw new ClientException();
            }

            @Override
            public boolean canEdit(List<String> groupIds) {
                check404(status);
                throw new ClientException();
            }

            @Override
            public boolean canAdmin(List<String> groupIds) {
                check404(status);
                throw new ClientException();
            }

            @Override
            public List<String> getAllGroupIdsForUser() {
                throw new ClientException();
            }

            private void check404(int status) {
                if (status == 404) {
                    throw new ModelNotFoundException();
                }
            }
        };
    }
}
