package com.example.demo.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Logs HTTP request and response bodies using content-caching wrappers.
 * Works for both annotation-based and functional (WebMvc.fn) endpoints.
 */
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 1024 * 1024; // 1MB cap

    /**
     * Filters each HTTP request once, wrapping it with content-caching wrappers so the
     * request and response bodies can be logged after the processing chain completes.
     *
     * @param request  the incoming HTTP servlet request
     * @param response the HTTP servlet response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException in case of general servlet errors
     * @throws IOException in case of I/O errors
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("request_"+request.getRequestURI());
        // Wrap request/response to enable body caching
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(cachingRequest, cachingResponse);
        } finally {
            stopWatch.stop();
            // Log request
            String requestBody = getRequestBody(cachingRequest);
            LOG.info("Incoming request: method={}, uri={}, contentType={}, body={} timeTaken={}ms",
                    request.getMethod(), request.getRequestURI(), request.getContentType(), requestBody,stopWatch.getTotalTimeMillis());

//            LOG.info(stopWatch.prettyPrint());
            // Log response
            String responseBody = getResponseBody(cachingResponse);
            LOG.info("Outgoing response: status={}, contentType={}, body={}",
                    response.getStatus(), response.getContentType(), responseBody);

            // Important: copy cached body back to the real response
            cachingResponse.copyBodyToResponse();
        }
    }

    /**
     * Reads the cached request body from the content-caching request wrapper.
     *
     * @param request the content-caching request wrapper
     * @return the request payload as a String, or empty string when none
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length == 0) {
            return "";
        }
        Charset charset = getCharset(request.getCharacterEncoding());
        return new String(buf, charset);
    }

    /**
     * Reads the cached response body from the content-caching response wrapper.
     *
     * @param response the content-caching response wrapper
     * @return the response payload as a String, or empty string when none
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length == 0) {
            return "";
        }
        Charset charset = getCharset(response.getCharacterEncoding());
        return new String(buf, charset);
    }

    /**
     * Resolves a Charset from the provided encoding name, defaulting to UTF-8 when missing or invalid.
     *
     * @param encoding the character encoding name; may be null or blank
     * @return a Charset instance, UTF-8 by default
     */
    private Charset getCharset(String encoding) {
        if (StringUtils.hasText(encoding)) {
            try {
                return Charset.forName(encoding);
            } catch (Exception ignored) { }
        }
        return StandardCharsets.UTF_8;
    }
}
