package com.ngw.fusion.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：RestTemplate的ab工具类
 * </pre>
 */
public class RestTemplateUtil {
    private static RestTemplate restTemplate;
    private static Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

    private RestTemplateUtil() {

    }

    private static RestTemplate restTemplate() {
        if (restTemplate == null) {
            FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.valueOf("text/html;charset=UTF-8"));
            mediaTypes.add(MediaType.valueOf("application/json;charset=UTF-8"));
            fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
            fastJsonHttpMessageConverter.getFastJsonConfig().setSerializerFeatures(
                    SerializerFeature.WriteDateUseDateFormat);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
            restTemplate.getMessageConverters().add(fastJsonHttpMessageConverter);
        }
        return restTemplate;
    }


    public static void main(String[] args) {
        logger.info("123{}", "1");
    }


    // 发起一个get请求，自定义header
    public static <T> T get(String url, Map<String, String> heads, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> param : heads.entrySet()) {
                headers.add(param.getKey(), param.getValue());
            }
        }

        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        logger.info("entity: {}", JSON.toJSONString(entity));
        logger.info("url:{}", url);
        ResponseEntity<T> responseEntity;
        try {
            responseEntity = restTemplate().exchange(url, HttpMethod.GET, entity, responseType);
        } catch (HttpClientErrorException e) {
            logger.info("responseEntity:{}", e.getResponseBodyAsString());
            throw e;
        }
        logger.info("responseEntity:{}", JSON.toJSONString(responseEntity));
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
//        throw new BusinessError(JSON.toJSONString(responseEntity), BaseStatusCode.REMOTE_ERROR);
            throw new RuntimeException(JSON.toJSONString(responseEntity));
        }

        return responseEntity.getBody();
    }

    // 发起一个get请求，自定义header
    public static <T> T post(String url, Map<String, String> heads, MultiValueMap<String, String> params, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> param : heads.entrySet()) {
                headers.add(param.getKey(), param.getValue());
            }
        }
        // headers ： 请求头
        // params ： 请求体
        // HttpEntity<Object>包括 请求提和请求头 两个部分
        HttpEntity<Object> entity = new HttpEntity<>(params, headers);
        logger.info("entity: {}", JSON.toJSONString(entity));
        logger.info("url:{}", url);
        ResponseEntity<T> responseEntity;
        try {
            responseEntity = restTemplate().exchange(url, HttpMethod.POST, entity, responseType);
        } catch (HttpClientErrorException e) {
            logger.info("responseEntity:{}", e.getResponseBodyAsString());
            throw e;
        }
        logger.info("responseEntity:{}", JSON.toJSONString(responseEntity));
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException(JSON.toJSONString(responseEntity));
        }

        return responseEntity.getBody();
    }

    public static <T> T post(String url, Map<String, String> heads, MultiValueMap<String, String> params, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> param : heads.entrySet()) {
                headers.add(param.getKey(), param.getValue());
            }
        }

        HttpEntity<Object> entity = new HttpEntity<>(params, headers);
        logger.info("entity: {}", JSON.toJSONString(entity));
        logger.info("url:{}", url);
        ResponseEntity<T> responseEntity;
        try {
            responseEntity = restTemplate().exchange(url, HttpMethod.POST, entity, responseType);
        } catch (HttpClientErrorException e) {
            logger.info("responseEntity:{}", e.getResponseBodyAsString());
            throw e;
        }
        logger.info("responseEntity:{}", JSON.toJSONString(responseEntity));
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException(JSON.toJSONString(responseEntity));
        }

        return responseEntity.getBody();
    }

    // 发起一个get请求，自定义header
    public static <T> T post(String url, Map<String, String> heads, Object params, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> param : heads.entrySet()) {
                headers.add(param.getKey(), param.getValue());
            }
        }

        HttpEntity<Object> entity = new HttpEntity<>(params, headers);
        logger.info("entity: {}", JSON.toJSONString(entity));
        logger.info("url:{}", url);
        ResponseEntity<T> responseEntity;
        try {
            responseEntity = restTemplate().exchange(url, HttpMethod.POST, entity, responseType);
        } catch (HttpClientErrorException e) {
            logger.info("responseEntity:{}", e.getResponseBodyAsString());
            throw e;
        }
        logger.info("responseEntity:{}", JSON.toJSONString(responseEntity));
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException(JSON.toJSONString(responseEntity));
        }

        return responseEntity.getBody();
    }

    public static <T> T post(String url, Map<String, String> heads, Map<String, String> params, ParameterizedTypeReference<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (heads != null && heads.size() > 0) {
            for (Map.Entry<String, String> param : heads.entrySet()) {
                headers.add(param.getKey(), param.getValue());
            }
        }

        HttpEntity<Object> entity = new HttpEntity<>(params, headers);
        logger.info("entity: {}", JSON.toJSONString(entity));
        logger.info("url:{}", url);
        ResponseEntity<T> responseEntity;
        try {
            responseEntity = restTemplate().exchange(url, HttpMethod.POST, entity, responseType);
        } catch (HttpClientErrorException e) {
            logger.info("responseEntity:{}", e.getResponseBodyAsString());
            throw e;
        }
        logger.info("responseEntity:{}", JSON.toJSONString(responseEntity));
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            throw new RuntimeException(JSON.toJSONString(responseEntity));
        }

        return responseEntity.getBody();
    }
}
