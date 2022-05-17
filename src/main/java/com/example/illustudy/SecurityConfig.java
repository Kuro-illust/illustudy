package com.example.illustudy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

//import com.example.illustudy.filter.FormAuthenticationProvider;
import com.example.illustudy.repository.UserRepository;
import com.example.illustudy.service.UserDetailsServiceImpl;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	protected static Logger log = LoggerFactory.getLogger(SecurityConfig.class);
	
    @Autowired
    private UserRepository repository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	//private FormAuthenticationProvider authenticationProvider;
	

	/**
	 * 認証から除外する
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers( "/css/**", "/images/**", "/scripts/**", "/h2-console/**");
	}

	 /**
	  * 認証を設定する
	  */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/","/login","/logout-complete", "/users/new", "/user","/topics","/artworks/**")
		.permitAll()
		.anyRequest()
		.authenticated()
		//ログイン
		.and()
		.formLogin()
		.loginPage("/login")
		.defaultSuccessUrl("/")
		.failureUrl("/login-failure")
		.usernameParameter("email")
		.passwordParameter("password")
		.permitAll()
		//ログアウト
		.and()
		.logout()
		.logoutUrl("/logout")
		.logoutSuccessUrl("/logout-complete")
		.clearAuthentication(true)
		.deleteCookies("JSESSIONID")
		.invalidateHttpSession(true).permitAll().and().csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//auth.authenticationProvider(authenticationProvider);
		auth.userDetailsService(userDetailsService);
		/*
		 * インメモリー（テスト） auth.inMemoryAuthentication()
		 * .withUser("user").password(passwordEncoder().encode("password")).roles("USER"
		 * );
		 */
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
