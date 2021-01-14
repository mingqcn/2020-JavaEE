package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/4 0:33
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 获取所有权限（第一页）
     * @throws Exception
     */
    @Test
    public void getAllPriv1() throws Exception{
        String token = this.login("13088admin", "123456");
        String responseString = this.mvc.perform(get("/privilege/privileges").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":18,\"pages\":2,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"查看任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void getAllPriv2() throws Exception {
        String responseString = this.mvc.perform(get("/privilege/privileges"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 获取所有权限（第二页）
     * @throws Exception
     */
    @Test
    public  void getAllPriv3() throws Exception {
        String token = this.login("13088admin", "123456");
        String responseString = this.mvc.perform(get("/privilege/privileges").header("authorization", token).
                queryParam("page", "2").queryParam("pageSize","10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":18,\"pages\":2,\"pageSize\":10,\"page\":2,\"list\":[{\"id\":12,\"name\":\"给角色增加权限\",\"url\":\"/roles/{id}/privileges/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:06:03\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":13,\"name\":\"取消角色权限\",\"url\":\"/roleprivileges/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:06:43\",\"gmtModified\":\"2020-11-03T21:30:31\"},{\"id\":14,\"name\":\"修改权限信息\",\"url\":\"/privileges/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:08:18\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":15,\"name\":\"查看所有用户的角色\",\"url\":\"/adminusers/{id}/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:53:38\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":16,\"name\":\"查看所有代理\",\"url\":\"/proxies\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:55:31\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":17,\"name\":\"禁止代理关系\",\"url\":\"/allproxies/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T17:57:45\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":18,\"name\":\"取消任意用户角色\",\"url\":\"/adminuserroles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T19:52:04\",\"gmtModified\":\"2020-11-03T19:56:43\"},{\"id\":19,\"name\":\"管理员设置用户代理关系\",\"url\":\"/ausers/{id}/busers/{id}:\",\"requestType\":1,\"gmtCreate\":\"2020-11-04T13:10:02\",\"gmtModified\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login1() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        //region Email未确认用户登录
        requireJson = "{\"userName\":\"5264500009\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.EMAIL_NOTVERIFIED.getCode()))
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login2() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        //region 密码错误的用户登录
        requireJson = "{\"userName\":\"13088admin\",\"password\":\"000000\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_INVALID_ACCOUNT.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login3() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        //region 用户名错误的用户登录
        requireJson = "{\"userName\":\"NotExist\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_INVALID_ACCOUNT.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login4() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        //region 没有输入用户名的用户登录
        requireJson = "{\"password\":\"123456\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andExpect(jsonPath("$.errmsg").value("必须输入用户名;"))
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login5() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        //region 没有输入密码（密码空）的用户登录
        requireJson = "{\"userName\":\"537300010\",\"password\":\"\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andExpect(jsonPath("$.errmsg").value("必须输入密码;"))
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login6() throws Exception {
        String requireJson = null;
        String response = null;
        ResultActions res = null;

        //region 用户重复登录
        requireJson = "{\"userName\":\"13088admin\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        response = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data").isString())
                .andReturn().getResponse().getContentAsString();

        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        String response1 = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();

        String jwt = JacksonUtil.parseString(response, "data");
        String jwt1 = JacksonUtil.parseString(response1, "data");
        assertNotEquals(jwt, jwt1);
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login7() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;
        //region 当前状态不可登录的用户登录
        requireJson = "{\"userName\":\"8884810086\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_USER_FORBIDDEN.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login8() throws Exception {
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data").isString())
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4/ 16:00
     */
    @Test
    public void logout() throws  Exception{
        String requireJson = null;
        String responseString = null;
        ResultActions res = null;

        requireJson = "{\"userName\":\"13088admin\",\"password\":\"123456\"}";
        res = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data").isString())
                .andReturn().getResponse().getContentAsString();
        String json = JacksonUtil.parseString(responseString,"data");

        //region 用户正常登出
        res = this.mvc.perform(get("/privilege/privileges/logout")
                .contentType("application/json;charset=UTF-8")
                .header("authorization",json));
        responseString = res.andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
        //endregion
    }

    /**
     * @author 24320182203266
     * 正常修改权限
     * @throws Exception
     */
    @Test
    public void changePriv1() throws Exception{
        PrivilegeVo vo = new PrivilegeVo();
        vo.setName("车市");
        String json = "{\"name\":\"车市\", \"url\": \"/adminusers/{id}/abcd\", \"requestType\": \"3\"}";

        String token = login("13088admin","123456");
        String responseString = this.mvc.perform(put("/privilege/privileges/2").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", responseString, true);

        responseString = this.mvc.perform(get("/privilege/privileges").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data.list[0].id").value("2"))
                .andExpect(jsonPath("$.data.list[0].name").value("车市"))
                .andExpect(jsonPath("$.data.list[0].url").value("/adminusers/{id}/abcd"))
                .andExpect(jsonPath("$.data.list[0].requestType").value("3"))
                .andReturn().getResponse().getContentAsString();

//        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":18,\"pages\":2,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"车市\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-17T23:08:58\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * @author 24320182203266
     * 修改权限时提供的信息与原有的重复
     * @throws Exception
     */
    @Test
    public void changePriv2() throws Exception{
        PrivilegeVo vo = new PrivilegeVo();
        vo.setName("车市");
        String json = "{\"name\":\"查看任意用户信息\", \"url\": \"/adminusers/{id}\", \"requestType\": \"0\"}";

        String token = login("13088admin","123456");
        String responseString = this.mvc.perform(put("/privilege/privileges/2").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals("{\"errno\":742,\"errmsg\":\"URL和RequestType不得与已有的数据重复\"}", responseString, true);
        responseString = this.mvc.perform(get("/privilege/privileges").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data.list[0].id").value("2"))
                .andExpect(jsonPath("$.data.list[0].name").value("查看任意用户信息"))
                .andExpect(jsonPath("$.data.list[0].url").value("/adminusers/{id}"))
                .andReturn().getResponse().getContentAsString();

//        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":18,\"pages\":2,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"车市\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-17T23:08:58\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * @author 24320182203266
     * 参数错误
     * @throws Exception
     */
    @Test
    public void changePriv3() throws Exception{
        PrivilegeVo vo = new PrivilegeVo();
        vo.setName("车市");
        String json = "{\"name\":\"查看任意用户信息\", \"url\": \"/adminusers/{id}\", \"requestType\": \"120\"}";

        String token = login("13088admin","123456");
        String responseString = this.mvc.perform(put("/privilege/privileges/2").header("authorization",token).contentType("application/json;charset=UTF-8").content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
                .andExpect(jsonPath("$.errmsg").value("错误的requestType数值;"))
                .andReturn().getResponse().getContentAsString();
        responseString = this.mvc.perform(get("/privilege/privileges").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andExpect(jsonPath("$.data.list[0].id").value("2"))
                .andExpect(jsonPath("$.data.list[0].name").value("查看任意用户信息"))
                .andExpect(jsonPath("$.data.list[0].url").value("/adminusers/{id}"))
                .andReturn().getResponse().getContentAsString();

//        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":18,\"pages\":2,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"车市\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-17T23:08:58\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, responseString, true);

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
