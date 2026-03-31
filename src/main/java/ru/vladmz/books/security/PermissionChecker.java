package ru.vladmz.books.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ru.vladmz.books.services.Ownable;

@Component
public class PermissionChecker {

    private final SecurityUtils securityUtils;

    public PermissionChecker(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public void checkPermission(Ownable ownable){
        if (!ownable.getOwner().getEmail().equals(securityUtils.getCurrentUserEmail()))
            throw new AccessDeniedException("No rights to change resource with id: " + ownable.getId());
    }
}
