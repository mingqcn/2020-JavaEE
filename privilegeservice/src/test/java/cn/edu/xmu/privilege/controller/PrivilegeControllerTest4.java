package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.dao.UserDao;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * @author Xianwei Wang
 */
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * @author XQChen
 * @date Created in 2020/11/8 0:33
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest4 {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeControllerTest4.class);;

    private String content;

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

    /**
     * @author XQChen
     * @date Created in 2020/11/8 0:33
     **/
    @Test
    public void findAllUsers1() throws Exception {

        String token = this.login("13088admin", "123456");

        //参数正确的请求
        try {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findAllUsersSuccess.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.debug("content: " + content);

        String responseSuccessString = this.mvc.perform(get("/privilege/adminusers/all?userName=&mobile=&page=2&pagesize=3").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("response: " + responseSuccessString);

        JSONAssert.assertEquals(responseSuccessString, content, true);

    }

    /**
     * @author XQChen
     * @date Created in 2020/11/8 0:33
     **/
    @Test
    public void findAllUsers2() throws Exception {

        String token = this.login("13088admin", "123456");

        //参数错误的请求
        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findAllUsersFail.json")));
        } catch (Exception e) {e.printStackTrace();}

        logger.debug("content: " + content);

        String responseFailString = this.mvc.perform(get("/privilege/adminusers/all?userName=&mobile=&page=1&pagesize=-2").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseFailString, content, true);
    }

    /**
     * @author XQChen
     * @date Created in 2020/11/8 0:33
     **/
    @Test
    public void findUserSelf() throws Exception {

        String token = this.login("13088admin", "123456");

        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findUserSelf.json")));
        } catch (Exception e) {e.printStackTrace();}

        String responseString = this.mvc.perform(get("/privilege/adminusers").header("authorization", token))
                .andExpect(status().isOk())
                //.andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        logger.debug("findUserSeld response:" + responseString + "end");

        JSONAssert.assertEquals(responseString, content, new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("data.lastLoginIp", (o1, o2) -> true),
                new Customization("data.lastLoginTime", (o1, o2) -> true)));
    }

    /**
     * @author XQChen
     * @date Created in 2020/11/8 0:33
     **/
    @Test
    public void findUserById1() throws Exception {
        //ID存在的请求

        String token = this.login("13088admin", "123456");

        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findUserByIdSuccess.json")));
        } catch (Exception e) {e.printStackTrace();}

        String responseSuccessString = this.mvc.perform(get("/privilege/adminusers/46").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseSuccessString, content, true);
    }

    /**
     * @author XQChen
     * @date Created in 2020/11/8 0:33
     **/
    @Test
    public void findUserById2() throws Exception {
        //ID不存在的请求

        String token = this.login("13088admin", "123456");
        logger.debug("token:" + token);

        try
        {
            content = new String(Files.readAllBytes(Paths.get("src/test/resources/findUserByIdFail.json")));
        } catch (Exception e) {e.printStackTrace();}

        String responseFailString = this.mvc.perform(get("/privilege/adminusers/23").header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(responseFailString, content, true);
    }

    /**
     * 创建测试用token
     * @author Xianwei Wang
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     */
    private final String createTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }



    /**
     * 取消用户角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest() throws Exception {
        String token = createTestToken(1L, 0L, 10000);
        String responseString = this.mvc.perform(delete("/privilege/shops/0/adminusers/50/roles/87").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 赋予用户角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void assignRoleTest() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(post("/privilege/shops/0/adminusers/47/roles/84").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":91,\"user\":{\"id\":47,\"userName\":\"2721900002\"},\"role\":{\"id\":84,\"name\":\"文案\"},\"creator\":{\"id\":1,\"userName\":\"13088admin\"},\"gmtCreate\":\"2020-11-20T14:31:58.948174\"},\"errmsg\":\"成功\"}";
        //JSONAssert.assertEquals(expectedResponse, responseString, false);

    }
    /**
     * 查看用户的角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        logger.debug(token);
        String responseString = this.mvc.perform(get("/privilege/shops/0/adminusers/47/roles").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":78,\"user\":{\"id\":47,\"userName\":\"2721900002\"},\"role\":{\"id\":85,\"name\":\"总经办\"},\"creator\":{\"id\":1,\"userName\":\"13088admin\"},\"gmtCreate\":\"2020-11-01T09:48:24\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 查看自己的角色测试
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest() throws Exception {
        String token = createTestToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/privilege/adminusers/self/roles").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":76,\"user\":{\"id\":1,\"userName\":\"13088admin\"},\"role\":{\"id\":23,\"name\":\"管理员\"},\"creator\":{\"id\":1,\"userName\":\"13088admin\"},\"gmtCreate\":\"2020-11-01T09:48:24\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}
