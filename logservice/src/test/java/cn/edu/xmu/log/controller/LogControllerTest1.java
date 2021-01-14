package cn.edu.xmu.log.controller;

import cn.edu.xmu.log.LogServiceApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 日志控制器测试类
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Slf4j
@SpringBootTest(classes = LogServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LogControllerTest1 {

    @Autowired
    private MockMvc mvc;
    /**
     * 创建测试用token
     *
     * @author 24320182203281 王纬策
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.debug(token);
        return token;
    }

    /**
     * 查询日志 成功
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void selectAllLogsTest() {
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/log/shops/1/logs?pageNum=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":1,\"userId\":12,\"ip\":\"192.168.1.1\",\"desc\":\"lalala\",\"departId\":1,\"privilegeId\":12,\"success\":1,\"operationDate\":\"2020-11-18T10:00\"}]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
