package com.socket.dodgem;


import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

public class ApplicationConfig implements ServerApplicationConfig {

    //扫描注解
    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scan) {
        System.out.println("scan WebSocket" + scan.size());
        //返回（起到过滤的作用，可以在返回前把里面部分类进行过滤）
        return scan;
    }
    
    
    //实现接口
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(
            Set<Class<? extends Endpoint>> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}