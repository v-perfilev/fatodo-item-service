package com.persoff68.fatodo.config.database;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.security.util.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Optional<String> currentUsername = SecurityUtils.getCurrentUsername();
        return currentUsername.isPresent() ? currentUsername : Optional.of(AppConstants.SYSTEM_USERNAME);
    }
}
