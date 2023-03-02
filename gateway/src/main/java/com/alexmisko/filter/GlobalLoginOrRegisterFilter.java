package com.alexmisko.filter;

import com.alexmisko.constant.CommonConstant;
import com.alexmisko.constant.GatewayConstant;
import com.alexmisko.util.TokenParseUtil;
import com.alexmisko.vo.JwtToken;
import com.alexmisko.vo.LoginUserInfo;
import com.alexmisko.vo.UsernameAndPassword;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class GlobalLoginOrRegisterFilter implements GlobalFilter, Ordered {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    /**
     * 登录、注册、鉴权
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        log.info("请求路径和参数是 [{}] [{}]", request.getURI().getPath(), JSON.toJSONString(request.getQueryParams()));
        if(request.getURI().getPath().contains(GatewayConstant.LOGIN_IN_URI)){
            String token = getTokenFromAuthCenter(request, GatewayConstant.AUTH_CENTER_LOGIN_URL_FORMAT);
            response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, null == token ? "null":token);
            response.setStatusCode(HttpStatus.OK);
            String body = "{\"code\":200,\"msg\":\"登录成功\"}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer).doOnError(error -> DataBufferUtils.release(buffer)));
        }
        if(request.getURI().getPath().contains(GatewayConstant.LOGIN_UP_URI)){
            String token = getTokenFromAuthCenter(request, GatewayConstant.AUTH_CENTER_REGISTER_URL_FORMAT);
            response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, null == token ? "null":token);
            String body = "{\"code\":200,\"msg\":\"注册成功\"}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer).doOnError(error -> DataBufferUtils.release(buffer)));
        }
        if(request.getURI().getPath().contains(GatewayConstant.VIDEO_LIST_URI)){
            return chain.filter(exchange);
        }
        if(request.getURI().getPath().contains("/userInfo")){
            return chain.filter(exchange);
        }
        //不是登录或注册，校验token是否能解析
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(CommonConstant.JWT_USER_INFO_KEY);
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.getLoginUserInfo(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (null == loginUserInfo){
            response.setStatusCode(HttpStatus.OK);
            String body = "{\"code\":403,\"msg\":\"您没有权限访问\"}";
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer).doOnError(error -> DataBufferUtils.release(buffer)));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }

    private String parseBodyFromRequest(ServerHttpRequest request){
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }



    private String getTokenFromAuthCenter(ServerHttpRequest request, String uriFormat){
        ServiceInstance serviceInstance = loadBalancerClient.choose("AUTH");
        String requestUrl = String.format(uriFormat, serviceInstance.getHost(), serviceInstance.getPort());
        UsernameAndPassword requestBody = JSON.parseObject(parseBodyFromRequest(request), UsernameAndPassword.class);
        log.info("log in request with url and body: [{}] [{}]", requestUrl, JSON.toJSONString(requestBody));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JwtToken token = restTemplate.postForObject(requestUrl, new HttpEntity<>(JSON.toJSONString(requestBody), headers), JwtToken.class);
        if (null != token){
            return token.getToken();
        }
        return null;
    }
}
