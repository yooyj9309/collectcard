package com.rainist.collectcard.config

import com.rainist.common.log.Log
import java.lang.management.ManagementFactory
import java.util.Random
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NettyConfig {

    companion object : Log

    @Bean
    fun nettyPidSetting() {
        System.setProperty("io.netty.processId", defaultProcessId().toString())
    }

    private fun defaultProcessId(): Int? {
        var name = ManagementFactory.getRuntimeMXBean().name
        var pid: Int? = Random().nextInt(65535)

        try {
            pid = Integer.parseInt(name.substring(0, name.indexOf("@")))
            logger.withFieldInfo(Pair("NettyPid", pid))
        } catch (e: Exception) {
            logger.withFieldWarn(Pair("NettyPidException", "PID : $pid, Exception : ${e.localizedMessage}"), e)
        }
        return pid
    }
}
