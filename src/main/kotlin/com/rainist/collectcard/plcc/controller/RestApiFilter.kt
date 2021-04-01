package com.rainist.collectcard.plcc.controller

import com.rainist.common.log.Log
import java.nio.charset.Charset
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class RestApiFilter : Filter{

    companion object: Log

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        var req = ContentCachingRequestWrapper(request as HttpServletRequest)
        var res = ContentCachingResponseWrapper(response as HttpServletResponse)

        reqBodyLog(req)

        chain.doFilter(req, res)

        resBodyLog(req, res)

        res.copyBodyToResponse()
    }

    fun reqBodyLog(req : ContentCachingRequestWrapper){
        var url = req.requestURI
        logger.Warn("req filter : {}", url)
    }

    fun resBodyLog(req: ContentCachingRequestWrapper, res : ContentCachingResponseWrapper){
        var requestUrl = req.requestURI
        var status = res.status
        var responseBody = String(res.contentAsByteArray, Charset.forName("MS949"))
        logger.Warn("res filter url : {}, status : {}, body : {}", requestUrl, status, responseBody)
    }
}
