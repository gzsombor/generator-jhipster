<%#
 Copyright 2013-2017 the original author or authors.

 This file is part of the JHipster project, see https://jhipster.github.io/
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
package <%=packageName%>.service.dto;

<% if (fieldsContainInstant === true) { %>
import java.time.Instant;<% } %><%_ if (fieldsContainLocalDate == true) { _%>
import java.time.LocalDate;<% } %><% if (fieldsContainZonedDateTime == true) { %>
import java.time.ZonedDateTime;<% } %><% if (validation) { %>
import javax.validation.constraints.*;<% } %>
import java.io.Serializable;<% if (fieldsContainBigDecimal == true) { %>
import java.math.BigDecimal;<% } %><% if (fieldsContainBlob && databaseType === 'cassandra') { %>
import java.nio.ByteBuffer;<% } %><% if (relationships.length > 0) { %>
import java.util.HashSet;
import java.util.Set;<% } %>
import java.util.Objects;<% if (databaseType == 'cassandra') { %>
import java.util.UUID;<% } %><% if (fieldsContainBlob && databaseType === 'sql') { %>
import javax.persistence.Lob;<% } %>
<%_ for (idx in fields) { if (fields[idx].fieldIsEnum == true) { _%>
import <%=packageName%>.domain.enumeration.<%= fields[idx].fieldType %>;
<%_ } } _%>

/**
 * An update/creation DTO for the <%= entityClass %> entity.
 */
public class <%= entityClass %>UpdateDTO implements Serializable {
<% if (databaseType == 'sql') { %>
    private Long id;<% } %><% if (databaseType == 'mongodb') { %>
    private String id;<% } %><% if (databaseType == 'cassandra') { %>
    private UUID id;<% } %>
    <%_ for (idx in fields) {
        const fieldValidate = fields[idx].fieldValidate;
        const fieldValidateRules = fields[idx].fieldValidateRules;
        const fieldValidateRulesMinlength = fields[idx].fieldValidateRulesMinlength;
        const fieldValidateRulesMaxlength = fields[idx].fieldValidateRulesMaxlength;
        const fieldValidateRulesMinbytes = fields[idx].fieldValidateRulesMinbytes;
        const fieldValidateRulesMaxbytes = fields[idx].fieldValidateRulesMaxbytes;
        const fieldValidateRulesMin = fields[idx].fieldValidateRulesMin;
        const fieldValidateRulesMax = fields[idx].fieldValidateRulesMax;
        const fieldValidateRulesPatternJava = fields[idx].fieldValidateRulesPatternJava;
        const fieldType = fields[idx].fieldType;
        const fieldTypeBlobContent = fields[idx].fieldTypeBlobContent;
        const fieldName = fields[idx].fieldName;_%>

    <%_ if (fieldValidate == true) {
            let required = false;
            if (fieldValidate == true && fieldValidateRules.indexOf('required') != -1) {
                required = true;
            } _%>
    <%- include ../../common/field_validators -%>
    <%_ } _%>
    <%_ if (fieldType == 'byte[]' && databaseType === 'sql') { _%>
    @Lob
    <%_ } _%>
    <%_ if (fieldTypeBlobContent != 'text') { _%>
    private <%= fieldType %> <%= fieldName %>;
    <%_ } else { _%>
    private String <%= fieldName %>;
    <%_ } _%>
    <%_ if ((fieldType == 'byte[]' || fieldType === 'ByteBuffer') && fieldTypeBlobContent != 'text') { _%>
    private String <%= fieldName %>ContentType;
        <%_ } _%>
    <%_ } _%>
    <%_ for (idx in relationships) {
        const otherEntityRelationshipName = relationships[idx].otherEntityRelationshipName;
        const relationshipFieldName = relationships[idx].relationshipFieldName;
        const relationshipFieldNamePlural = relationships[idx].relationshipFieldNamePlural;
        const relationshipType = relationships[idx].relationshipType;
        const otherEntityNameCapitalized = relationships[idx].otherEntityNameCapitalized;
        const otherEntityFieldCapitalized = relationships[idx].otherEntityFieldCapitalized;
        const ownerSide = relationships[idx].ownerSide; _%>
    <%_ if (relationshipType == 'many-to-many' && ownerSide == true) { _%>

    private Set<<%= otherEntityNameCapitalized %>DTO> <%= relationshipFieldNamePlural %> = new HashSet<>();
    <%_ } else if (relationshipType == 'many-to-one' || (relationshipType == 'one-to-one' && ownerSide == true)) { _%>

    private Long <%= relationshipFieldName %>Id;
    <%_ } } _%>

    public <% if (databaseType == 'sql') { %>Long<% } %><% if (databaseType == 'mongodb') { %>String<% } %><% if (databaseType == 'cassandra') { %>UUID<% } %> getId() {
        return id;
    }

    public void setId(<% if (databaseType == 'sql') { %>Long<% } %><% if (databaseType == 'mongodb') { %>String<% } %><% if (databaseType == 'cassandra') { %>UUID<% } %> id) {
        this.id = id;
    }
    <%_ for (idx in fields) {
        const fieldType = fields[idx].fieldType;
        const fieldTypeBlobContent = fields[idx].fieldTypeBlobContent;
        const fieldInJavaBeanMethod = fields[idx].fieldInJavaBeanMethod;
        const fieldName = fields[idx].fieldName; _%>
    <%_ if(fieldTypeBlobContent != 'text') { _%>
    public <%= fieldType %> get<%= fieldInJavaBeanMethod %>() {
    <%_ } else { _%>
    public String get<%= fieldInJavaBeanMethod %>() {
    <%_ } _%>
        return <%= fieldName %>;
    }

    <%_ if(fieldTypeBlobContent != 'text') { _%>
    public void set<%= fieldInJavaBeanMethod %>(<%= fieldType %> <%= fieldName %>) {
    <%_ } else { _%>
    public void set<%= fieldInJavaBeanMethod %>(String <%= fieldName %>) {
    <%_ } _%>
        this.<%= fieldName %> = <%= fieldName %>;
    }
    <%_ if ((fieldType == 'byte[]' || fieldType === 'ByteBuffer') && fieldTypeBlobContent != 'text') { _%>

    public String get<%= fieldInJavaBeanMethod %>ContentType() {
        return <%= fieldName %>ContentType;
    }

    public void set<%= fieldInJavaBeanMethod %>ContentType(String <%= fieldName %>ContentType) {
        this.<%= fieldName %>ContentType = <%= fieldName %>ContentType;
    }
    <%_ } } _%>
    <%_ for (idx in relationships) {
        relationshipFieldName = relationships[idx].relationshipFieldName,
        relationshipFieldNamePlural = relationships[idx].relationshipFieldNamePlural,
        otherEntityName = relationships[idx].otherEntityName,
        otherEntityNamePlural = relationships[idx].otherEntityNamePlural,
        relationshipType = relationships[idx].relationshipType,
        otherEntityNameCapitalized = relationships[idx].otherEntityNameCapitalized,
        otherEntityFieldCapitalized = relationships[idx].otherEntityFieldCapitalized,
        relationshipNameCapitalized = relationships[idx].relationshipNameCapitalized,
        relationshipNameCapitalizedPlural = relationships[idx].relationshipNameCapitalizedPlural,
        ownerSide = relationships[idx].ownerSide;
        if (relationshipType == 'many-to-many' && ownerSide == true) { _%>

    public Set<<%= otherEntityNameCapitalized %>DTO> get<%= relationshipNameCapitalizedPlural %>() {
        return <%= relationshipFieldNamePlural %>;
    }

    public void set<%= relationshipNameCapitalizedPlural %>(Set<<%= otherEntityNameCapitalized %>DTO> <%= otherEntityNamePlural %>) {
        this.<%= relationshipFieldNamePlural %> = <%= otherEntityNamePlural %>;
    }
    <%_ } else if (relationshipType == 'many-to-one' || (relationshipType == 'one-to-one' && ownerSide == true)) { _%>

    <%_ if (relationshipNameCapitalized.length > 1) { _%>
    public Long get<%= relationshipNameCapitalized %>Id() {
        return <%= relationshipFieldName %>Id;
    }

    public void set<%= relationshipNameCapitalized %>Id(Long <%= otherEntityName %>Id) {
        this.<%= relationshipFieldName %>Id = <%= otherEntityName %>Id;
    }
    <%_ } else { // special case when the entity name has one character _%>
    public Long get<%= relationshipNameCapitalized.toLowerCase() %>Id() {
        return <%= relationshipFieldName %>Id;
    }

    public void set<%= relationshipNameCapitalized.toLowerCase() %>Id(Long <%= otherEntityName %>Id) {
        this.<%= relationshipFieldName %>Id = <%= otherEntityName %>Id;
    }
    <%_ } } } _%>

    @Override
    public String toString() {
        return "<%= entityClass %>UpdateDTO{" +
            "id=" + id +<% for (idx in fields) {
                const fieldName = fields[idx].fieldName; %>
            ", <%= fieldName %>='" + <%= fieldName %> + "'" +<% } %>
            '}';
    }
}
