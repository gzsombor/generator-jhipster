<%#
 Copyright 2013-2018 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see http://www.jhipster.tech/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
package <%=packageName%>.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;<% if (databaseType === 'sql') { %>
import org.hibernate.envers.Audited;<% } %>
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;<% if (databaseType === 'mongodb') { %>
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;
<% } %><% if (databaseType === 'couchbase') { %>
import com.couchbase.client.java.repository.annotation.Field;
import java.time.Instant;
<% } %><% if (databaseType === 'sql') { %>
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.JoinColumn;<% } %>

<%_
 noUserEntity = skipUserManagement && (applicationType !== 'monolith' || authenticationType !== 'oauth2');
 auditorIdType = noUserEntity ? 'String' : pkType;
 _%>
/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */<% if (databaseType === 'sql') { %>
@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener.class)<% } %>
public abstract class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedBy<% if (databaseType === 'sql') { %>
    @Column(name = "created_by", nullable = false, updatable = false)<% } %><% if (databaseType === 'mongodb' || databaseType === 'couchbase') { %>
    @Field("created_by")<% } %>
    @JsonIgnore
    private <%= auditorIdType %> createdBy;

    @CreatedDate<% if (databaseType === 'sql') { %>
    @Column(name = "created_date", nullable = false)<% } %><% if (databaseType === 'mongodb' || databaseType === 'couchbase') { %>
    @Field("created_date")<% } %>
    @JsonIgnore
    private Instant createdDate = Instant.now();

    @LastModifiedBy<% if (databaseType === 'sql') { %>
    @Column(name = "last_modified_by")<% } %><% if (databaseType === 'mongodb' || databaseType === 'couchbase') { %>
    @Field("last_modified_by")<% } %>
    @JsonIgnore
    private <%= auditorIdType %> lastModifiedBy;

    @LastModifiedDate<% if (databaseType === 'sql') { %>
    @Column(name = "last_modified_date")<% } %><% if (databaseType === 'mongodb' || databaseType === 'couchbase') { %>
    @Field("last_modified_date")<% } %>
    @JsonIgnore
    private Instant lastModifiedDate = Instant.now();

<%_ if (databaseType === 'sql' && !noUserEntity) { _%>
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by", insertable = false, updatable = false)
    private User lastModifiedByUser;

    public User getCreatedByUser() {
        return createdByUser;
    }

    public User getLastModifiedByUser() {
        return lastModifiedByUser;
    }

<%_ } _%>

    public <%= auditorIdType %> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(<%= auditorIdType %> createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public <%= auditorIdType %> getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(<%= auditorIdType %> lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    <%_ if (databaseType === 'sql' && !noUserEntity) { _%>
    public String getCreatedByUserName() {
        return createdByUser != null ? createdByUser.getFullName() : null;
    }

    public String getLastModifiedByUserName() {
        return lastModifiedByUser != null ? lastModifiedByUser.getFullName() : null;
    }
    <%_ } else { _%>
    @Deprecated
    public String getCreatedByUserName() {
        return createdBy;
    }

    @Deprecated
    public String getLastModifiedByUserName() {
        return lastModifiedBy;
    }
    <%_ } _%>
}
