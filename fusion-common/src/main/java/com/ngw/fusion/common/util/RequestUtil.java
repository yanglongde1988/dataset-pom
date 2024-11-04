package com.ngw.fusion.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class RequestUtil {
    // 获取IP地址

    public static String getDefaultGateway()   {
        String defaultGateway = null;
        try {
            defaultGateway = "";
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress()) {
                        defaultGateway = inetAddress.getHostAddress();
                        System.out.println(defaultGateway);
                    }
                }
                if (!defaultGateway.isEmpty()) {
                    break;
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return defaultGateway;
    }

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(",") > 0) {
            String[] parts = ip.split(",");
            for (String part : parts) {
                if (!part.isEmpty() && !"unknown".equalsIgnoreCase(part)) {
                    ip = part.trim();
                    break;
                }
            }
        }
        //    "0:0:0:0:0:0:0:1" 本地方式访问
        if("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
    // 获取IP地址
    public static String getRemoteIP(ServerHttpRequest request) {
        HttpHeaders reuqestHttpHeaders = request.getHeaders();
        String ip = reuqestHttpHeaders.getFirst("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = reuqestHttpHeaders.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = reuqestHttpHeaders.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().toString();
        }
        if (ip != null && ip.indexOf(",") > 0) {
            String[] parts = ip.split(",");
            for (String part : parts) {
                if (!part.isEmpty() && !"unknown".equalsIgnoreCase(part)) {
                    ip = part.trim();
                    break;
                }
            }
        }
        //    "0:0:0:0:0:0:0:1" 本地方式访问
        if("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
    // 获取 USERAGENT
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            return "";
        }
        return userAgent;
    }
    // 获取 USERAGENT
    public static String getUserAgent(ServerHttpRequest request) {
        if (request == null) {
            return "";
        }
        HttpHeaders reuqestHttpHeaders = request.getHeaders();
        String userAgent = reuqestHttpHeaders.getFirst("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            return "";
        }
        return userAgent;
    }
    public static boolean isOptionsRequest(ServerHttpRequest request){
        String method = request.getMethod().name();
        if ("OPTIONS".equals(method)) {
            return true; // 放行
        }
        return false; // 拦截
    }
    // 获取 token
    public static String getToken(ServerHttpRequest request) {
        if (request == null) {
            return "";
        }
        HttpHeaders reuqestHttpHeaders = request.getHeaders();
        String token = reuqestHttpHeaders.getFirst("Authorization");
        if (StringUtils.isNotBlank(token) && token.startsWith("Bearer ")) {
            // 去掉 "Bearer " 前缀
            token = token.substring(7);
            return token;
        }
        return "";
    }
}
