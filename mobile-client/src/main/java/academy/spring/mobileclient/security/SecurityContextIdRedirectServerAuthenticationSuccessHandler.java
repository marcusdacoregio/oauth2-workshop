package academy.spring.mobileclient.security;

import reactor.core.publisher.Mono;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

public class SecurityContextIdRedirectServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

	private static final String SECURITY_CONTEXT_ID_ATTR_NAME = ServerSecurityContextRepository.class.getName() + ".SECURITY_CONTEXT_ID";

	private final String redirectUri;

	private String parameterName = "token";

	public SecurityContextIdRedirectServerAuthenticationSuccessHandler(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
		return Mono.defer(() -> performRedirect(exchange, authentication));
	}

	private Mono<Void> performRedirect(WebFilterExchange exchange, Authentication authentication) {
		return exchange.getExchange().getSession()
				.map((session) -> buildRedirectUrl(session.getId()))
				.map(RedirectServerAuthenticationSuccessHandler::new)
				.flatMap((handler) -> handler.onAuthenticationSuccess(exchange, authentication));
	}

	private String buildRedirectUrl(String sessionId) {
		return UriComponentsBuilder.fromUriString(this.redirectUri)
			.queryParam(this.parameterName, sessionId)
			.build()
			.toString();
	}

	/**
	 * Set the name of the query parameter used to pass the {@code SECURITY_CONTEXT_ID} in the redirect.
	 * @param parameterName the name of the query parameter used in the redirect
	 */
	public void setParameterName(String parameterName) {
		Assert.hasText(parameterName, "parameterName cannot be empty");
		this.parameterName = parameterName;
	}
}
