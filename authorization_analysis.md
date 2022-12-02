# Spring Security授权流程分析

`FilterSecurityInterceptor`是Spring Security的内置Filter，用于对HTTP资源进行安全拦截处理，通常放在Spring Security Chain最后的位置。



AbstractSecurityInterceptor: 为安全对象(secure objects)实现安全拦截的抽象类。
AbstractSecurityInterceptor将确保安全拦截器的正确启动配置。它还将实现对安全对象调用的正确处理，即

1. 从SecurityContextHolder获取Authentication对象。
2. 通过对照SecurityMetadataSource查找安全对象请求，确定请求是否与安全调用或公共调用相关。
3. 对于安全的调用(有一个用于安全对象调用的 ConfigAttributes 列表)
    a. 如果 Authentication.isAuthenticated() 返回 false，或者 alwaysReauthenticate 为 true，则根据配置的 AuthenticationManager 验证请求。 经过身份验证后，将 SecurityContextHolder 上的 Authentication 对象替换为返回值。
    b. 通过针对 SecurityMetadataSource 查找安全对象请求来确定请求是否与安全调用或公共调用相关。
    c. 通过配置的RunAsManager执行任何运行方式替换。
    d. 将控制权交还给具体子类，具体子类将继续执行该对象。 返回一个 InterceptorStatusToken，以便在子类完成对象的执行后，其 finally 子句可以确保使用 finallyInvocation(InterceptorStatusToken) 重新调用和正确整理 AbstractSecurityInterceptor。
    e. 具体子类将通过 afterInvocation(InterceptorStatusToken, Object) 方法重新调用 AbstractSecurityInterceptor。
    f. 如果 RunAsManager 替换了 Authentication 对象，则将 SecurityContextHolder 返回到调用 AuthenticationManager 后存在的对象。
    g. 如果定义了 AfterInvocationManager，则调用调用管理器并允许它替换应返回给调用者的对象。
4. 对于公开的调用（安全对象调用没有 ConfigAttributes）
    a. 具体子类将返回一个 InterceptorStatusToken，随后在执行安全对象后将其重新呈现给 AbstractSecurityInterceptor。 AbstractSecurityInterceptor 在其 afterInvocation(InterceptorStatusToken, Object) 被调用时将不会采取进一步的行动。
5. 控制再次返回到具体的子类，连同应该返回给调用者的对象。 子类然后将该结果或异常返回给原始调用者。


FilterSecurityInterceptor: 通过过滤器实现对 HTTP 资源进行安全处理，其指定的安全对象是FilterInvocation；FilterInvocation持有与HTTP Filter相关的对象

AccessDecisionManager: 用于对访问授权进行决策；综合AccessDecisionVoter的结果，对访问授权做出决策
AccessDecisionVoter: 用于负责对授权决定进行投票




为什么要投票机制？
