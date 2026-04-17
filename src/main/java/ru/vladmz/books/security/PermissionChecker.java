package ru.vladmz.books.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.entities.interfaces.Ownable;
import ru.vladmz.books.etc.UserRole;

@Component
public class PermissionChecker {

    private final SecurityUtils securityUtils;

    public PermissionChecker(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public void checkPermission(Ownable ownable){
        User owner = ownable.getOwner();

        if (!owner.getEmail().equals(securityUtils.getCurrentUserEmail()) && !securityUtils.getCurrentRole().equals(UserRole.ADMIN))
            throw new AccessDeniedException("No rights to change resource with id: " + ownable.getId());
    }
}
