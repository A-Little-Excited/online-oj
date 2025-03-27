package com.excited.common.message.service;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AliSmsService {

    @Autowired
    private Client aliClient;

    @Value("${sms.aliyun.templateCode}")
    private String templateCode;

    @Value("${sms.aliyun.singName}")
    private String singName;

    /**
     * 发送验证码
     * @param phone 手机号码
     * @param code 验证码
     * @return 发送结果
     */
    public boolean sendCode(String phone, String code) {
        // 由于短信参数需要使用 JSON 字符串的形式进行传递, 因此需要设置为 key-value 的结构方便后续转化为 JSON
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        return sendTemplateMessage(phone, templateCode, singName, params);
    }

    /**
     * 发送模板短信
     * @param phone 手机号码
     * @param templateCode 短信模板 code
     * @param singName 短信签名
     * @param params 短信参数
     * @return 发送结果
     */
    public boolean sendTemplateMessage(String phone, String templateCode, String singName,
                                       Map<String, String> params) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(phone);
        sendSmsRequest.setSignName(singName);
        sendSmsRequest.setTemplateCode(templateCode);
        sendSmsRequest.setTemplateParam(JSON.toJSONString(params));

        try {
            SendSmsResponse sendSmsResponse = aliClient.sendSms(sendSmsRequest);
            // 响应数据存储在 body 中
            SendSmsResponseBody responseBody = sendSmsResponse.getBody();
            if (!"OK".equalsIgnoreCase(responseBody.getCode())) {
                log.error("短信 {} 发送失败, 失败原因是: {}", JSON.toJSONString(sendSmsRequest), responseBody.getMessage());
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("短信 {} 发送失败, 失败原因是: {}", JSON.toJSONString(sendSmsRequest), e.getMessage());
            return false;
        }
    }
}
