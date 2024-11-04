package com.ngw.fusion.common.util;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.Enumeration;

public class IPUtils {

    public static String getRequestIp(HttpServletRequest request) {
        String sourceIp = null;

        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("X-Real-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ipAddresses = request.getRemoteAddr();
        }
        if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(ipAddresses)) {
            sourceIp = ipAddresses.split(",")[0];
        }

        return sourceIp;
    }

    public static String getLocalIp(){
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface networkInterface = en.nextElement();
                String name = networkInterface.getName();
                //用于排除回送接口,非虚拟网卡,未在使用中的网络接口
                if (!networkInterface.isLoopback() && !networkInterface.isVirtual() && networkInterface.isUp() && !name.contains("docker") && !name.contains("lo") && !name.contains("vir")) {
                    for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        //获得IP
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                if(StringUtils.isBlank(ip) || "127.0.0.1".equals(ip)){
                                    ip = ipaddress;
                                }
                            }
                        }
                    }
                }
            }
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }
}
