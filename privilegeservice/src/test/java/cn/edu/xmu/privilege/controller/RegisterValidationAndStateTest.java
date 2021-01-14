package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 注册验证测试类 该类测试验证方法是否正确
 * @author LiangJi3229
 * @date 2020/11/10 18:39
 */
@SpringBootTest(classes = PrivilegeServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
public class RegisterValidationAndStateTest {
    @Autowired
    MockMvc mvc;

    /**
     * 获取所有状态
     * @throws Exception
     */
    @Test
    public void getAllState() throws Exception {
        String responseString=this.mvc.perform(get("/privilege/adminusers/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 0, \"data\": [ { \"name\": \"新注册\", \"code\": 0 }, { \"name\": \"正常\", \"code\": 1 }, { \"name\": \"封禁\", \"code\": 2 }, { \"name\": \"废弃\", \"code\": 3 } ], \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 正常注册
     * @throws Exception
     */
    @Test
    public void register1() throws Exception {
        String requireJson="{\n    \"userName\": \"anormalusername3\",\n    \"password\": \"123456aBa!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"13888888388\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=this.mvc.perform(post("/privilege/adminusers")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 0, \"data\": { \"id\": 1, \"userName\": \"anormalusername3\", \"mobile\": \"8733C04F80C594827F776CD726B85472\", \"email\": \"5643361C11D3299408C7EA82206AFCC7\", \"name\": \"4A3BE008F8DE844B7EE7042E1B7B8842\", \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\", \"openId\": \"test\", \"departId\": 1,  \"password\": \"2B29D194F1ECBBC839DCC34F572269A3\" }, \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 手机号不正确
     * @throws Exception
     */
    @Test
    public void register2() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"ab6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
                String responseString=this.mvc.perform(post("/privilege/adminusers")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 503, \"errmsg\": \"手机号格式不正确;\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * email不正确
     * @throws Exception
     */
    @Test
    public void register3() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"ABDa123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"13511686886\",\n    \"email\": \"testtest.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=this.mvc.perform(post("/privilege/adminusers")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 503, \"errmsg\": \"email格式不正确;\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 用户名不正确
     * @throws Exception
     */
    @Test
    public void register4() throws Exception {
        String requireJson="{\n    \"userName\": \"13\",\n    \"password\": \"ABDa123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"13411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=this.mvc.perform(post("/privilege/adminusers")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 503, \"errmsg\": \"用户名长度过短;\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 用户名与user表重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister1() throws Exception {
        String requireJson="{\n    \"userName\": \"13088admin\",\n    \"password\": \"ABaAA123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"13888883887\",\n    \"email\": \"tes@test2.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
                String responseString=this.mvc.perform(post("/privilege/adminusers")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":731,\"errmsg\":\"用户名已被注册\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 用户名与newUser表重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister2() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"ABaAA123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"13888883887\",\n    \"email\": \"tes@test2.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        this.mvc.perform(post("/privilege/adminusers")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        String responseString=this.mvc.perform(post("/privilege/adminusers")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":731,\"errmsg\":\"用户名已被注册\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }
}
