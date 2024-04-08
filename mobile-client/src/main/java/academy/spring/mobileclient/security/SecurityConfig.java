package academy.spring.mobileclient.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionOAuth2ServerAuthorizationRequestRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
			ReactiveClientRegistrationRepository clientRegistrationRepository) {

		http
			.authorizeExchange((authorize) -> authorize
				.pathMatchers("/", "/callback", "/login", "/favicon.ico").permitAll()
				.anyExchange().authenticated()
			)
			.oauth2Login((login) -> login
				.authorizationRequestRepository(new WebSessionOAuth2ServerAuthorizationRequestRepository()) // the default
				.authenticationSuccessHandler(
					new SecurityContextIdRedirectServerAuthenticationSuccessHandler("exp://127.0.0.1:8081/--/callback"))
			)
			.logout((logout) -> {
				OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler =
					new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
				logoutSuccessHandler.setPostLogoutRedirectUri("http://127.0.0.1:8080/");

				logout.logoutSuccessHandler(logoutSuccessHandler);
			})
			.securityContextRepository(new WebSessionServerSecurityContextRepository()) // the default
			.exceptionHandling((exceptions) -> exceptions
				.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
			)
			.csrf(ServerHttpSecurity.CsrfSpec::disable);

		return http.build();
	}

}
