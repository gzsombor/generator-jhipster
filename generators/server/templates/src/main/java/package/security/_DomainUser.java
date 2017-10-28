package <%=packageName%>.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * DomainUser object which implements {@link UserDetails} and stores the id of the user.
 *
 */
public class DomainUser extends User {

    private static final long serialVersionUID = 1L;
    private final <%= pkType %> id;
    private final String language;

    public DomainUser(<%= pkType %> id, String username, String password, Collection<? extends GrantedAuthority> authorities, String language) {
        super(username, password, authorities);
        this.id = id;
        this.language = language;
    }

    public DomainUser(<%= pkType %> id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities, String language) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.language = language;
    }

    public <%= pkType %> getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public boolean hasRole(String role) {
        for (final GrantedAuthority authority : getAuthorities()) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }

}
