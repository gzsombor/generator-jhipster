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
package <%=packageName%>.config.social;

import <%=packageName%>.repository.SocialUserConnectionRepository;
import <%=packageName%>.repository.CustomSocialUsersConnectionRepository;
<%_ if (authenticationType === 'jwt') { _%>
import <%=packageName%>.security.jwt.TokenProvider;
<%_ } _%>
import <%=packageName%>.security.social.CustomSignInAdapter;

import io.github.jhipster.config.JHipsterProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.GenericOAuth2ServiceProvider;
import org.springframework.social.oauth2.TokenStrategy;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
// jhipster-needle-add-social-connection-factory-import-package

/**
 * Basic Spring Social configuration.
 *
 * <p>
 * Creates the beans necessary to manage Connections to social services and
 * link accounts from those services to internal Users.
 */
@Configuration
@EnableSocial
public class SocialConfiguration implements SocialConfigurer {

    private final Logger log = LoggerFactory.getLogger(SocialConfiguration.class);

    private final SocialUserConnectionRepository socialUserConnectionRepository;

    private final Environment environment;

    public SocialConfiguration(SocialUserConnectionRepository socialUserConnectionRepository,
            Environment environment) {

        this.socialUserConnectionRepository = socialUserConnectionRepository;
        this.environment = environment;
    }

    @Bean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
            ConnectionRepository connectionRepository) {

        ConnectController controller = new ConnectController(connectionFactoryLocator, connectionRepository);
        controller.setApplicationUrl(environment.getProperty("spring.application.url"));
        return controller;
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        configureGoogleConnection(connectionFactoryConfigurer, environment);

        configureFacebookConnection(connectionFactoryConfigurer, environment);

        configureTwitterConnection(connectionFactoryConfigurer, environment);

        configureGithubConnection(connectionFactoryConfigurer, environment);

        // jhipster-needle-add-social-connection-factory
    }

	/**
	 * Google configuration
	 */
	private void configureGoogleConnection(ConnectionFactoryConfigurer connectionFactoryConfigurer,
			Environment environment) {
        final String googleClientId = environment.getProperty("spring.social.google.client-id");
        final String googleClientSecret = environment.getProperty("spring.social.google.client-secret");
        if (googleClientId != null && googleClientSecret != null) {
            log.debug("Configuring GoogleConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                new GoogleConnectionFactory(
                    googleClientId,
                    googleClientSecret
                )
            );
        } else {
            log.error("Cannot configure GoogleConnectionFactory id or secret null");
        }
	}

	/**
	 * Facebook configuration
	 */
	private void configureFacebookConnection(ConnectionFactoryConfigurer connectionFactoryConfigurer,
			Environment environment) {
        final String facebookClientId = environment.getProperty("spring.social.facebook.client-id");
        final String facebookClientSecret = environment.getProperty("spring.social.facebook.client-secret");
        if (facebookClientId != null && facebookClientSecret != null) {
            log.debug("Configuring FacebookConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                new FacebookConnectionFactory(
                    facebookClientId,
                    facebookClientSecret
                )
            );
        } else {
            log.error("Cannot configure FacebookConnectionFactory id or secret null");
        }
	}

	/**
	 * Twitter configuration
	 */
	private void configureTwitterConnection(ConnectionFactoryConfigurer connectionFactoryConfigurer,
			Environment environment) {
        final String twitterClientId = environment.getProperty("spring.social.twitter.client-id");
        final String twitterClientSecret = environment.getProperty("spring.social.twitter.client-secret");
        if (twitterClientId != null && twitterClientSecret != null) {
            log.debug("Configuring TwitterConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                new TwitterConnectionFactory(
                    twitterClientId,
                    twitterClientSecret
                )
            );
        } else {
            log.error("Cannot configure TwitterConnectionFactory id or secret null");
        }
	}

	private void configureGithubConnection(ConnectionFactoryConfigurer connectionFactoryConfigurer,
			Environment environment) {
		final String githubClientId = environment.getProperty("spring.social.github.client-id");
		final String githubClientSecret = environment.getProperty("spring.social.github.client-secret");
        if (githubClientId != null && githubClientSecret != null) {
            log.debug("Configuring GithubConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                githubConnection(
                    githubClientId,
                    githubClientSecret
                )
            );
        } else {
            log.error("Cannot configure github connection: id or secret null");
        }
	}

    private OAuth2ConnectionFactory<Void> githubConnection(String clientId, String clientSecret) {
        return new OAuth2ConnectionFactory("github", createGithubServiceProvider(clientId, clientSecret), null);
    }

    private GenericOAuth2ServiceProvider createGithubServiceProvider(String clientId, String clientSecret) {
		return  new GenericOAuth2ServiceProvider(clientId, clientSecret,
				"https://github.com/login/oauth/authorize", null,
				"https://github.com/login/oauth/access_token", true, TokenStrategy.AUTHORIZATION_HEADER);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new CustomSocialUsersConnectionRepository(socialUserConnectionRepository, connectionFactoryLocator);
    }

    @Bean
    public SignInAdapter signInAdapter(UserDetailsService userDetailsService, JHipsterProperties jHipsterProperties<% if (authenticationType === 'jwt') { %>,
            TokenProvider tokenProvider<% } %>) {
        return new CustomSignInAdapter(userDetailsService, jHipsterProperties<% if (authenticationType === 'jwt') { %>,
            tokenProvider<% } %>);
    }

    @Bean
    public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository, SignInAdapter signInAdapter) {
        ProviderSignInController providerSignInController = new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
        providerSignInController.setSignUpUrl("/social/signup");
        providerSignInController.setApplicationUrl(environment.getProperty("spring.application.url"));
        return providerSignInController;
    }

    @Bean
    public ProviderSignInUtils getProviderSignInUtils(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
        return new ProviderSignInUtils(connectionFactoryLocator, usersConnectionRepository);
    }
}
