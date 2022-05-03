package account.config;

import account.security.RestAccessDeniedHandler;
import account.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class WebSecurityConfigImpl extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    RestAccessDeniedHandler restAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint);
        http.exceptionHandling().accessDeniedHandler(restAccessDeniedHandler);
        http.csrf().disable().headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .antMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                .mvcMatchers("/api/acct/payments").hasRole("ACCOUNTANT")
                .mvcMatchers("/api/admin/user", "/api/admin/user/*","/api/admin/user/").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/security/events").hasRole("AUDITOR")
                .mvcMatchers("/api/auth/changepass").authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getEncoder());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }

}