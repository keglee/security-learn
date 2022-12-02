# Spring Security实现JSON方式登录

Spring Security默认只提供表单方式登录，在实际工作中，项目通常使用前后端分离模式，前后端之间使用JSON格式进行数据交互；因此在项目里使用Spring Security，则需要自行实现JSON方式登录。

## 自定义AbstractAuthenticationProcessingFilter实现类

`AbstractAuthenticationProcessingFilter`是Spring Security用于处理认证请求的抽象Filter，其子类`UsernamePasswordAuthenticationFilter`用于处理基于表单提交的认证请求，默认匹配/login请求。

为了实现使用JSON方式登录，第一步需要自定义`AbstractAuthenticationProcessingFilter`实现来代替`UsernamePasswordAuthenticationFilter`。

```java

public class JsonLoginFilter extends AbstractAuthenticationProcessingFilter {
    
    public JsonLoginFilter() {
        // 定义请求匹配: 当以post方式发送的/login请求，则认为是登录认证请求
        super(new AntPathRequestMatcher("/login", "POST"));
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String method = request.getMethod();
        if(!HttpMethod.POST.matches(method)) {
            // 如果不是请求方式不是post，则抛出异常
            throw new AuthenticationServiceException("Authentication method not supported :" + method);
        }
        
        String contentType = request.getContentType();
        if(!contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            // 如果contentType不是application/json,则抛出异常
            throw new AuthenticationServiceException("Authentication contentType not supported :" + contentType);
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 读取请求输入流，并转换为map
            Map<String, String> loginInfo = objectMapper.readValue(request.getInputStream(), Map.class);
            String username = loginInfo.get("username");
            String password = loginInfo.get("password");
            // 构建Authentication
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
            // 调用AuthenticationManager处理认证请求
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Authentication failed :" + e.getMessage());
        }
    }
}

```

第二步将自定义的`JsonLoginFilter`配置到Spring Security的过滤器链中

```java

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JsonLoginFilter loginFilter() throws Exception{
        JsonLoginFilter filter = new JsonLoginFilter();
        // 配置AuthenticationManager
        filter.setAuthenticationManager(authenticationManagerBean());
        // 配置认证成功后的处理逻辑
        filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(authentication));
            }
        });
        // 配置认证失败后的处理逻辑
        filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException, ServletException {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
    
                Map<String, String> result = new HashMap<>();
                result.put("message", exception.getMessage());
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(result));
            }
        });
        return filter;
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // 自定义UserDetailsService，模拟从数据库查询用户信息
        return new CustomizeUserDetailsService(Arrays.asList("admin", "test", "dev"));
    }
}

public class CustomizeUserDetailsService implements UserDetailsService {
    private final List<String> usernameList;
    public CustomizeUserDetailsService(List<String> usernameList) {
        this.usernameList = usernameList;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!exists(username)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(username).authorities("TEST").password("{noop}123456").build();
    }

    private boolean exists(String username) {
        boolean exist = false;
        for(String item : usernameList) {
            if(item.equals(username)) {
                exist = true;
                break;
            }
        }
        return exist;
    }
}
```