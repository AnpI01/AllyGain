package com.zenzsol.allygain.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AGSecurityConfiguration{

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(
				(requests) -> requests.requestMatchers("/", "/comp/*", "/addcommunity").permitAll()
			 	.requestMatchers("/user/**").hasAnyAuthority("ROLE_AGADMIN")
			 	.requestMatchers("/myaccount").hasAnyAuthority("ROLE_USER",  "ROLE_AGADMIN")
			 	.requestMatchers("/share/**").hasAnyAuthority("ROLE_USER")
			 	.requestMatchers("/reserve/**").hasAnyAuthority("ROLE_USER")
				.requestMatchers("/trade/**").hasAnyAuthority("ROLE_USER")
				.anyRequest().permitAll())
				.formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/myaccount",true).permitAll())
				.logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).clearAuthentication(true).deleteCookies("JSESSIONID")
						.invalidateHttpSession(true).logoutSuccessUrl("/").permitAll())
				.csrf((csrf) -> csrf.ignoringRequestMatchers("/trade/items/nextitems", "/share/items/nextitems")
					
				). build();
				
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return encoder;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new AGUserDetailsService();
	}

}
