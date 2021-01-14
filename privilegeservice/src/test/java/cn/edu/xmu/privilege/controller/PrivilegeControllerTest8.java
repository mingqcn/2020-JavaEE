package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Li Zihan
 * @date Created at 2020/11/18 23:35
 */
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional

public class PrivilegeControllerTest8 {
    @Autowired
    private MockMvc mvc;
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }
    /**
     * 审核用户
     * @author Li Zihan
     **/
    @Test
    public  void approveUser1() throws Exception {
        String token=creatTestToken(1L,0l,100);
        String responseString="";
        String approve=JacksonUtil.toJson(true);
        try
        {
            responseString = this.mvc.perform(put("/privilege/shops/0/adminusers/2/approve").header("authorization", token).content(approve))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 查询所有用户代理关系
     * @author Li Zihan
     **/
    @Test
    public  void approveUser2() throws Exception {
        String token=creatTestToken(1L,0l,100);
        String responseString="";
        String approve=JacksonUtil.toJson(false);
        try
        {
            responseString = this.mvc.perform(put("/privilege/shops/0/adminusers/2/approve").header("authorization", token).content(approve))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 管理员审核用户 用户不存在或用户非新用户
     * @author Li Zihan
     **/
    @Test
    public  void approveUser3() throws Exception {
        String token=creatTestToken(1L,0l,100);
        String responseString="";
        String approve=JacksonUtil.toJson(true);
        try
        {
            responseString = this.mvc.perform(put("/privilege/shops/0/adminusers/3/approve").header("authorization", token).content(approve))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String   expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的用户id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
