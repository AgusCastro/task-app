package com.challenge.taskapp.tenant;

import com.challenge.taskapp.exception.MissingTenantIdException;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        final String tenant = TenantContext.getCurrentTenant();
        if (tenant == null || tenant.isEmpty()) {
            throw new MissingTenantIdException();
        }
        return tenant;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
