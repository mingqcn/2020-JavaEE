package cn.edu.xmu.privilegeservice.client;

import java.util.List;

/**
 * @title IGatewayService.java
 * @description 网关内部调用接口
 * @author wwc
 * @date 2020/12/01 23:17
 * @version 1.0
 */
public interface IGatewayService {
    /**
     * @title loadSingleUserPriv
     * @description 加载单个用户的权限
     * @author wwc
     * @param userId
     * @param jwt
     * @date 2020/12/01 23:18
     */
    void loadSingleUserPriv(Long userId, String jwt);

}
