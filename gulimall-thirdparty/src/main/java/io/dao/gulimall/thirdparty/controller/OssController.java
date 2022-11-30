package io.dao.gulimall.thirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import io.dao.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class OssController {

    @Autowired
    private OSS client;

    @Value("${spring.cloud.alicloud.oss.endpoint")
    private String endpoint;

    @Value("${spring.cloud.alicloud.access-key")
    private String accessKey;

    // 自定义配置属性
    @Value("${spring.cloud.alicloud.oss.bucket")
    private String bucket;

    @RequestMapping("/oss/policy")
    public R policy() {
        // host的格式为bucketname.endpoint
        String host = "https://" + bucket + "." + endpoint;
        // callbackUrl为上传回调服务器的URL，请将下面的ip和port配置您自己的真实信息
        String callbackUrl = "";
        // 用户上传文件时指定的前缀
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = format;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = client.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("accessid", accessKey);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            return R.ok().put("data", respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
