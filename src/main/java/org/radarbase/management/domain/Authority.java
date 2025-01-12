package org.radarbase.management.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.radarbase.auth.authorization.RoleAuthority;
import org.radarbase.management.security.Constants;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Audited
@Table(name = "radar_authority")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 0, max = 50)
    @Id
    @Pattern(regexp = Constants.ENTITY_ID_REGEX)
    @Column(length = 50)
    private String name;

    public Authority() {
        // builder constructor
    }

    public Authority(String authorityName) {
        this.name = authorityName;
    }

    public Authority(RoleAuthority role) {
        this(role.getAuthority());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Authority authority = (Authority) o;

        if (name == null || authority.name == null) {
            return false;
        }

        return Objects.equals(name, authority.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Authority{"
                + "name='" + name + '\''
                + "}";
    }
}
