package academy.spring.mobileclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration(proxyBeanMethods = false)
@EnableSpringWebSession
class SessionConfig {

	@Bean
	ReactiveMapSessionRepository sessionRepository() {
		return new ReactiveMapSessionRepository(new ConcurrentHashMap<>());
	}

	@Bean
	WebSessionIdResolver webSessionIdResolver() {
		return new DelegatingWebSessionIdResolver(List.of(
				new BearerWebSessionIdResolver(), // used for communication between this client and the mobile app
				new CookieWebSessionIdResolver())); // used for oauth2 redirects between this client and the auth server
	}

	static class DelegatingWebSessionIdResolver implements WebSessionIdResolver {

		private final List<WebSessionIdResolver> delegates;

		DelegatingWebSessionIdResolver(List<WebSessionIdResolver> delegates) {
			this.delegates = delegates;
		}

		@Override
		public List<String> resolveSessionIds(ServerWebExchange exchange) {
			for (WebSessionIdResolver delegate : this.delegates) {
				List<String> sessionIds = delegate.resolveSessionIds(exchange);
				if (!CollectionUtils.isEmpty(sessionIds)) {
					return sessionIds;
				}
			}
			return Collections.emptyList();
		}

		@Override
		public void setSessionId(ServerWebExchange exchange, String sessionId) {
			for (WebSessionIdResolver delegate : this.delegates) {
				delegate.setSessionId(exchange, sessionId);
			}
		}

		@Override
		public void expireSession(ServerWebExchange exchange) {
			for (WebSessionIdResolver delegate : this.delegates) {
				delegate.expireSession(exchange);
			}
		}

	}

	static class BearerWebSessionIdResolver implements WebSessionIdResolver {

		@Override
		public List<String> resolveSessionIds(ServerWebExchange exchange) {
			List<String> authorization = exchange.getRequest().getHeaders().get("Authorization");
			if (CollectionUtils.isEmpty(authorization)) {
				return Collections.emptyList();
			}
			return authorization.stream().map((auth) -> auth.substring("Bearer ".length())).toList();
		}

		@Override
		public void setSessionId(ServerWebExchange exchange, String sessionId) {

		}

		@Override
		public void expireSession(ServerWebExchange exchange) {

		}

	}

}
