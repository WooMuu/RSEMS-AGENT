package com.agent.client;

import com.agent.config.LocalSettings;
import com.alibaba.fastjson.JSON;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CxfClient {
    @Autowired
    private LocalSettings localSettings;
    JaxWsDynamicClientFactory dcf;

    @PostConstruct
    private void initClient() {
        dcf = JaxWsDynamicClientFactory.newInstance();
    }

    private String getServerUrl() {
        return localSettings.getServerIp() + ":" + localSettings.getServerPort() + "/RSEMSServer/services/MonitorService?wsdl";
    }

    //动态调用方式
    public void sendMonitorInfo(Object data, String method) {
        String wsdlUrl = getServerUrl();
        //client = dcf.createClient("http://127.0.0.1:8081/RSEMSServer/monitor/MonitorService?wsdl");
        Client client = dcf.createClient(wsdlUrl);
        // 需要密码的情况需要加上用户名和密码
        // client.getOutInterceptors()  .add(new ClientLoginInterceptor(USER_NAME,
        // PASS_WORD));
        //将对象序列化为json
        String param = JSON.toJSONString(data, true);
        Object[] objects = new Object[0];
        try {
            // invoke("方法名",参数1,参数2,参数3....);
            objects = client.invoke(method, param);
            System.out.println("返回数据:" + objects[0]);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }
}
