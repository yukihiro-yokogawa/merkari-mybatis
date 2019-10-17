package com.example.demo.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

	/**
	 * 静的リソースに対するセキュリティ設定を無効
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**");
	}

	/**
	 * リクエストの設定
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
			.antMatchers("/**")
			.permitAll();
		
		http.formLogin()
			.loginPage("/user/login")
			.failureUrl("/user?error=true")
			.defaultSuccessUrl("/item/top",	false)
			//htmlのパラメーターと一致させる
			.usernameParameter("mailAddress")
			.passwordParameter("password")
			//独自の認証設定
			.authenticationDetailsSource(authenticationDetailsSource);

		
		http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
			.logoutSuccessUrl("/item/top")
			.deleteCookies("JSESSIONID")
			.invalidateHttpSession(true);
	}
	
	
	/**
	 * Bcryptアルゴリズムで暗号化する実装を返す.
	 * 
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 *「認証」設定
	 * 認証ユーザーを取得する「UserDetailsService」の設定
	 * パスワード照合時に使う「passwordEncoder」の設定
	 * 上記の設定を独自の認証ファイルにリターン.
	 * 
	 * @return 独自の認証データ
	 */
	@Bean
	public DaoAuthenticationProvider authProvider() {
		CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	/**
	 *「認証」設定
	 * 独自の認証設定.
	 *
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}
}
