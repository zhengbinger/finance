package com.zhengbing.common.security;

import com.zhengbing.common.security.FinanceUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by zhengbing on 2017-10-31.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConifg extends WebSecurityConfigurerAdapter{

    @Autowired
    private FinanceUserDetailsService financeUserDetailsService;

    //http://localhost:8080/login 输入正确的用户名密码 并且选中remember-me 则登陆成功，转到 index页面
    //再次访问index页面无需登录直接访问
    //访问http://localhost:8080/home 不拦截，直接访问，
    //访问http://localhost:8080/hello 需要登录验证后，且具备 “ADMIN”权限hasAuthority("ADMIN")才可以访问
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
//                .antMatchers("/home").permitAll()//访问：/home 无需登录认证权限
                .anyRequest().permitAll() //其他所有资源都需要认证，登陆后访问
//                .antMatchers("/hello").hasAuthority("ROLE_ADMIN") //登陆后之后拥有“ADMIN”权限才可以访问/hello方法，否则系统会出现“403”权限不足的提示
                .and()
                .formLogin()
                .loginPage("/login")//指定登录页是”/login”
                .permitAll()
                .successHandler(loginSuccessHandler()) //登录成功后可使用loginSuccessHandler()存储用户信息，可选。
                .and()
                .logout()
                .logoutSuccessUrl("/main") //退出登录后的默认网址是”/main”
                .permitAll()
                .invalidateHttpSession(true);
                //.and()
               // .rememberMe()//登录后记住用户，下次自动登录,数据库中必须存在名为persistent_logins的表
                //.tokenValiditySeconds(1209600);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //指定密码加密所使用的加密器为passwordEncoder()
        //需要将密码加密后写入数据库
        auth.userDetailsService(financeUserDetailsService).passwordEncoder(passwordEncoder());
        auth.eraseCredentials(false);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler(){
        return new LoginSuccessHandler();
    }

    public static void main(String[] args){

        System.out.println(new BCryptPasswordEncoder(  ).encode( "000000" ));
    }

}
