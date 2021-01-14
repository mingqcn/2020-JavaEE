package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wc 24320182203277
 * @date Created in 2020/11/9
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest7 {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeControllerTest7.class);


    //200
    @Test
    public void addrolepriv1() throws Exception{
        String token = this.login("13088admin", "123456");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authorization",token);
        MvcResult result = mvc
                .perform(post("/privilege/roles/87/privileges/17")//请求的方式 请求路径
                        .contentType("application/json")  //请求的形式
                        .content("{type:1,platform:1}")	 //参数（使用的是content） 同样也可以使用.param(key,value)方法去设置
                        .headers(httpHeaders))   //设置请求头
                .andReturn();//返回
        String content=result.getResponse().getContentAsString();//返回
        int state = result.getResponse().getStatus();
        logger.info("state: "+state+" addrolepriv1: "+content);
    }

    //资源不存在
    @Test
    public void addrolepriv2() throws Exception{

        String token = this.login("13088admin", "123456");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authorization",token);
        MvcResult result = mvc
                .perform(post("/privilege/roles/23/privileges/3000")  //请求的方式 请求路径
                        .contentType("application/json")  //请求的形式
                        .content("{type:1,platform:1}")	 //参数（使用的是content） 同样也可以使用.param(key,value)方法去设置
                        .headers(httpHeaders))   //设置请求头
                .andReturn();//返回
        String content=result.getResponse().getContentAsString();//返回
        int state = result.getResponse().getStatus();
        logger.info("state: "+state+" addrolepriv2: "+content);
    }

    //新增角色权限已存在
    @Test
    public void addrolepriv3() throws Exception{

        String token = this.login("13088admin", "123456");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("authorization",token);
        MvcResult result = mvc
                .perform(post("/privilege/roles/23/privileges/3")  //请求的方式 请求路径
                        .contentType("application/json")  //请求的形式
                        .content("{type:1,platform:1}")	 //参数（使用的是content） 同样也可以使用.param(key,value)方法去设置
                        .headers(httpHeaders))   //设置请求头
                .andReturn();//返回
        String content=result.getResponse().getContentAsString();//返回
        int state = result.getResponse().getStatus();
        logger.info("state: "+state+" addrolepriv3: "+content);
    }

    @Test
    public void delRolePriv1()throws Exception{
//        login()
        String authorization = this.login("13088admin", "123456");
        String responseString = this.mvc.perform(delete("/privilege/roleprivileges/33").header("authorization",authorization))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.info("delRolePriv1: "+responseString);
    }

    //资源不存在
    @Test
    public void delRolePriv2()throws Exception{
//        login()
        String authorization = this.login("13088admin", "123456");
        String responseString = this.mvc.perform(delete("/privilege/roleprivileges/3300").header("authorization",authorization))
                .andExpect(status().is(200))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.info("delRolePriv2: "+responseString);
    }

    @Test
    public void getRolePrivs1()throws Exception{
        String authorization = this.login("13088admin", "123456");
//
        String responseString = this.mvc.perform(get("/privilege/roles/23/privileges").header("authorization",authorization))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.info("getRolePrivs1: "+responseString);
    }

    //资源不存在
    @Test
    public void getRolePrivs2()throws Exception{
        String authorization = this.login("13088admin", "123456");
//
        String responseString = this.mvc.perform(get("/privilege/roles/10000/privileges").header("authorization",authorization))
                .andExpect(status().is(404))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        logger.info("getRolePrivs2: "+responseString);
    }

    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);

        String requireJson = JacksonUtil.toJson(vo);
        String response = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
        return  JacksonUtil.parseString(response, "data");

    }


}
