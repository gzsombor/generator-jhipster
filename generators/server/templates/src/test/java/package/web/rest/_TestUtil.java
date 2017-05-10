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
package <%=packageName%>.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
<%_ if (databaseType === 'couchbase') { _%>
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;
<%_ } _%>
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import <%=packageName%>.domain.Authority;
import <%=packageName%>.domain.User;
import <%=packageName%>.security.DomainUser;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    /** MediaType for JSON UTF8 */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);

    /**
     * Convert an object to JSON byte array.
     *
     * @param object
     *            the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        final JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }

    /**
     * Create a byte array with a specific size filled with specified data.
     *
     * @param size the size of the byte array
     * @param data the data to put in the byte array
     * @return the JSON byte array
     */
    public static byte[] createByteArray(int size, String data) {
        final byte[] byteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = Byte.parseByte(data, 2);
        }
        return byteArray;
    }
    <%_ if (databaseType === 'couchbase') { _%>

    /**
     * Mock user authentication for Spring SpEL expression used in {@link <%=packageName%>.repository.N1qlCouchbaseRepository}
     */
    public static void mockAuthentication() {
        TestSecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("user", null));
    }
    <%_ } _%>

    /**
     * A matcher that tests that the examined string represents the same instant as the reference datetime.
     */
    public static class ZonedDateTimeMatcher extends TypeSafeDiagnosingMatcher<String> {

        private final ZonedDateTime date;

        public ZonedDateTimeMatcher(ZonedDateTime date) {
            this.date = date;
        }

        @Override
        protected boolean matchesSafely(String item, Description mismatchDescription) {
            try {
                if (!date.isEqual(ZonedDateTime.parse(item))) {
                    mismatchDescription.appendText("was ").appendValue(item);
                    return false;
                }
                return true;
            } catch (final DateTimeParseException e) {
                mismatchDescription.appendText("was ").appendValue(item)
                    .appendText(", which could not be parsed as a ZonedDateTime");
                return false;
            }

        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a String representing the same Instant as ").appendValue(date);
        }
    }

    /**
     * Creates a matcher that matches when the examined string reprensents the same instant as the reference datetime
     * @param date the reference datetime against which the examined string is checked
     */
    public static ZonedDateTimeMatcher sameInstant(ZonedDateTime date) {
        return new ZonedDateTimeMatcher(date);
    }

    /**
     * Verifies the equals/hashcode contract on the domain object.
     */
    @SuppressWarnings("unchecked")
    public static void equalsVerifier(Class clazz) throws Exception {
        final Object domainObject1 = clazz.getConstructor().newInstance();
        assertThat(domainObject1.toString()).isNotNull();
        assertThat(domainObject1).isEqualTo(domainObject1);
        assertThat(domainObject1.hashCode()).isEqualTo(domainObject1.hashCode());
        // Test with an instance of another class
        final Object testOtherObject = new Object();
        assertThat(domainObject1).isNotEqualTo(testOtherObject);
        assertThat(domainObject1).isNotEqualTo(null);
        // Test with an instance of the same class
        final Object domainObject2 = clazz.getConstructor().newInstance();
        assertThat(domainObject1).isNotEqualTo(domainObject2);
        <%_ if (databaseType === 'sql' || databaseType === 'mongodb' || databaseType === 'couchbase') { _%>
        // HashCodes are equals because the objects are not persisted yet
        assertThat(domainObject1.hashCode()).isEqualTo(domainObject2.hashCode());
        <%_ } _%>
    }

    /**
     * Create a FormattingConversionService which use ISO date format, instead of the localized one.
     * @return the FormattingConversionService
     */
    public static FormattingConversionService createFormattingConversionService() {
        final DefaultFormattingConversionService dfcs = new DefaultFormattingConversionService ();
        final DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(dfcs);
        return dfcs;
    }

    private static void setTestUser(long userId, String name, Stream<String> authorities) {
        final List<SimpleGrantedAuthority> authorityList = authorities.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext
                .setAuthentication(new UsernamePasswordAuthenticationToken(new DomainUser(userId, name, "password", authorityList), "password", authorityList));
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Configures Sprint to use the provided informations as the currently
     * authenticated user.
     */
    public static void setTestUser(long userId, String name, String... authorities) {
        setTestUser(userId, name, Arrays.asList(authorities).stream());
    }

    /**
     * Creates a user object, and sets to be the currently authenticated user by
     * Spring.
     */
    public static void setTestUser(User user) {
        setTestUser(user.getId(), user.getLogin(), user.getAuthorities().stream().map(Authority::getName));
    }
}
