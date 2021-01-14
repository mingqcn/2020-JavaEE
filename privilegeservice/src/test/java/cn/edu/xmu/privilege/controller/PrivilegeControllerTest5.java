package cn.edu.xmu.privilege.controller;


import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.vo.UserProxyVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Li Di Han
 * @date Created in 2020/11/5 0:33
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class PrivilegeControllerTest5 {

    @Autowired
    private MockMvc mvc;

    /**
     * 创建测试用token
     *
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        log.debug(token);
        return token;
    }


    /* auth012 测试用例开始 */

    /**
     * 设置用户代理关系
     * @throws Exception
     */
    @Test
    public void usersProxy() throws Exception{
        UserProxyVo userProxyVo = new UserProxyVo();
        userProxyVo.setBeginDate("2020-10-10 00:00:00");
        userProxyVo.setEndDate("2020-10-11 00:00:00");
        String token = creatTestToken(1L, 0L, 100);
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/privilege/users/2/proxy").header("authorization", token).contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(userProxyVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 设置用户代理关系
     * @throws Exception
     */
    @Test
    public void usersProxy1() throws Exception{
        UserProxyVo userProxyVo = new UserProxyVo();
        userProxyVo.setBeginDate("2020-10-10 00:00:00");
        userProxyVo.setEndDate("2020-10-11 00:00:00");
        String token = creatTestToken(1L, 0L, 100);
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/privilege/users/1/proxy").header("authorization", token).contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(userProxyVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"code\":\"USERPROXY_SELF\",\"errmsg\":\"自己不可以代理自己\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 设置用户代理关系
     * @throws Exception
     */
    @Test
    public void usersProxy2() throws Exception{
        UserProxyVo userProxyVo = new UserProxyVo();
        userProxyVo.setBeginDate("2020-10-12 00:00:00");
        userProxyVo.setEndDate("2020-10-11 00:00:00");
        String token = creatTestToken(1L, 0L, 100);
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/privilege/users/2/proxy").header("authorization", token).contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(userProxyVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"code\":\"USERPROXY_BIGGER\",\"errmsg\":\"开始时间要小于失效时间\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 设置用户代理关系
     * @throws Exception
     */
    @Test
    public void usersProxy3() throws Exception{
        UserProxyVo userProxyVo = new UserProxyVo();
        userProxyVo.setBeginDate("2020-10-03 00:00:00");
        userProxyVo.setEndDate("2020-10-11 00:00:00");
        String token = creatTestToken(49L, 0L, 100);
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/privilege/users/47/proxy").header("authorization", token).contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(userProxyVo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"code\":\"USERPROXY_CONFLICT\",\"errmsg\":\"同一时间段有冲突的代理关系\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 管理员设置用户代理关系
     * @throws Exception
     */
    @Test
    public void aUsersProxy() throws Exception{
        UserProxyVo userProxyVo = new UserProxyVo();
        userProxyVo.setBeginDate("2020-10-10 00:00:00");
        userProxyVo.setEndDate("2020-10-11 00:00:00");
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/privilege/ausers/1/busers/2").header("authorization", token).contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(userProxyVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    /**
     * 解除用户代理关系
     * @throws Exception
     */
    @Test
    public void removeUserProxy() throws Exception{
        String token = creatTestToken(49L, 0L, 100);
        String responseString = this.mvc.perform(delete("/privilege/proxie/1")
                .header("authorization", token)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse ="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 解除用户代理关系
     * @throws Exception
     */
    @Test
    public void removeUserProxy1() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(delete("/privilege/proxie/1")
                .header("authorization", token)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse ="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 查询所有用户代理关系
     * @throws Exception
     */
    @Test
    public void listProxies() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/privilege/proxies")
                .header("authorization", token)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":[{\"id\":1,\"userAId\":49,\"userBId\":47,\"beginDate\":\"2020-10-03T18:51:42\",\"endDate\":\"2021-11-03T18:51:52\",\"gmtCreate\":\"2020-11-03T18:52:00\",\"signature\":\"bb1378ee78a41e6a37abd37aa2247af1f2962fc229ca84cf53981fb6b2fe37bc\",\"valid\":1},{\"id\":2,\"userAId\":49,\"userBId\":46,\"beginDate\":\"2020-05-03T18:52:25\",\"endDate\":\"2020-10-03T18:52:31\",\"gmtCreate\":\"2020-11-03T18:52:37\",\"signature\":\"006d2a321a041446b8c19f33bda62c49bdefe6bd12705d1be50c45dedb4842bb\",\"valid\":1},{\"id\":3,\"userAId\":49,\"userBId\":48,\"beginDate\":\"2021-12-03T18:53:01\",\"endDate\":\"2022-11-03T18:53:19\",\"gmtCreate\":\"2020-11-03T18:53:39\",\"signature\":\"6c0504294505cbd9b280954ea8442b478bdfc4b1184dc49b9cc1055f026a24f8\",\"valid\":1},{\"id\":4,\"userAId\":49,\"userBId\":50,\"beginDate\":\"2020-11-01T18:53:59\",\"endDate\":\"2020-12-03T18:54:07\",\"gmtCreate\":\"2020-11-03T18:54:17\",\"signature\":\"fba6d947de10ea75670dacc896e64fa393f44280ab55ff06f7a1f3333aee52b2\",\"valid\":0},{\"id\":5,\"userAId\":49,\"userBId\":51,\"beginDate\":\"2020-05-03T18:54:29\",\"endDate\":\"2020-07-03T18:54:37\",\"gmtCreate\":\"2020-11-03T18:54:42\",\"signature\":\"7b6bef43e290a29c964a4d5bad7208309ca4b583ae029579d2ce3b5a70e5c6ec\",\"valid\":1}]}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 禁止代理关系
     * @throws Exception
     */
    @Test
    public void removeAllProxies() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(delete("/privilege/allproxie/1")
                .header("authorization", token)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse ="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /* auth012 测试用例结束 */
}
