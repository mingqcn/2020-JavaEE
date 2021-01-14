package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 修改用户资料、状态测试类
 *
 * @author 19720182203919 李涵
 * Created at 4/11/2020 23:55
 * Modified at 7/11/2020 21:30
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest1 {

    @Autowired
    private MockMvc mvc;

    /* auth009 测试用例 */

    @Autowired
    private UserPoMapper userPoMapper;

    /**
     * 测试更新用户资料
     *
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified at 7/11/2020 21:30
     */
    @Test
    public void modifyUserNoExceptions() throws Exception {
        String token = testUserLogin();

        String contentJson = "{\n" +
                "    \"name\": \"张小绿\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/59")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的改变
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(AES.decrypt(updatedPo.getName(), User.AESPASS).equals("张小绿"), "用户名不相等！");
        Assert.state(AES.decrypt(updatedPo.getEmail(), User.AESPASS).equals("han@han-li.cn"), "Email 不相等！");
        Assert.state(AES.decrypt(updatedPo.getMobile(), User.AESPASS).equals("13906008040"), "电讯号码不相等！");
    }

    /**
     * 测试更新用户资料 (未登入)
     *
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 8/11/2020 10:19
     */
    @Test
    public void modifyUserNotLoggedIn() throws Exception {

        String contentJson = "{\n" +
                "    \"name\": \"张小绿\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/59")
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isUnauthorized()) // 未登入 401 错误
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":704,\"errmsg\":\"需要先登录\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的改变
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(!AES.decrypt(updatedPo.getName(), User.AESPASS).equals("张小绿"), "用户名在没登录的情况下被修改了！");
        Assert.state(!AES.decrypt(updatedPo.getEmail(), User.AESPASS).equals("han@han-li.cn"), "Email 在没登录的情况下被修改了！！");
        Assert.state(!AES.decrypt(updatedPo.getMobile(), User.AESPASS).equals("13906008040"), "电讯号码在没登录的情况下被修改了！！");
    }

    /**
     * 测试更新用户资料 (登入状态 JWT 已过期)
     *
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 8/11/2020 10:19
     */
    @Test
    public void modifyUserLogInExpired() throws Exception {
        String token = genTestToken(0, true);
        Thread.sleep(1500);

        String contentJson = "{\n" +
                "    \"name\": \"张小绿\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/59")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(contentJson))
                .andExpect(status().isUnauthorized()) // 未登入 401 错误
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":501,\"errmsg\":\"JWT不合法\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的改变
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(!AES.decrypt(updatedPo.getName(), User.AESPASS).equals("张小绿"), "用户名在没登录的情况下被修改了！");
        Assert.state(!AES.decrypt(updatedPo.getEmail(), User.AESPASS).equals("han@han-li.cn"), "Email 在没登录的情况下被修改了！！");
        Assert.state(!AES.decrypt(updatedPo.getMobile(), User.AESPASS).equals("13906008040"), "电讯号码在没登录的情况下被修改了！！");
    }

    /**
     * 测试更新用户资料 (无效 JWT)
     *
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 8/11/2020 10:39
     */
    @Test
    public void modifyUserJWTInvalid() throws Exception {
        String token = genTestToken(1, false);

        String contentJson = "{\n" +
                "    \"name\": \"张小绿\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/59")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(contentJson))
                .andExpect(status().isUnauthorized()) // 未登入 401 错误
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":502,\"errmsg\":\"JWT过期\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的改变
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(!AES.decrypt(updatedPo.getName(), User.AESPASS).equals("张小绿"), "用户名在没登录的情况下被修改了！");
        Assert.state(!AES.decrypt(updatedPo.getEmail(), User.AESPASS).equals("han@han-li.cn"), "Email 在没登录的情况下被修改了！！");
        Assert.state(!AES.decrypt(updatedPo.getMobile(), User.AESPASS).equals("13906008040"), "电讯号码在没登录的情况下被修改了！！");
    }

    /**
     * 测试更新用户资料 (Email 重复)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified at 7/11/2020 21:30
     */
    @Test
    public void modifyUserDuplicateEmail() throws Exception {
        String token = testUserLogin();

        String contentJson = "{\n" +
                "    \"name\": \"祝大米\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"112452463123\"\n" +
                "}";

        this.mvc.perform(
                put("/privilege/adminusers/58")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        contentJson = "{\n" +
                "    \"name\": \"叶小朵\",\n" +
                "    \"email\": \"han@han-li.cn\",\n" +
                "    \"mobile\": \"13807710771\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/57")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":732,\"errmsg\":\"邮箱已被注册\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新用户资料 (Email 不符合规定)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 8/11/2020 0:40
     */
    @Test
    public void modifyUserIncorrectEmail() throws Exception {
        String token = testUserLogin();

        String contentJson = "{\n" +
                "    \"name\": \"橘左京\",\n" +
                "    \"email\": \"hanhan@hanhan\",\n" +
                "    \"mobile\": \"112452463123\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/57")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(contentJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":503,\"errmsg\":\"Email 格式不正确;\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新用户资料 (电话重复)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void modifyUserDuplicateTel() throws Exception {
        String token = testUserLogin();

        String contentJson = "{\n" +
                "    \"name\": \"猪小花\",\n" +
                "    \"email\": \"jajaja@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        this.mvc.perform(
                put("/privilege/adminusers/58")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        contentJson = "{\n" +
                "    \"name\": \"李大力\",\n" +
                "    \"email\": \"kakaka@han-li.cn\",\n" +
                "    \"mobile\": \"13906008040\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/57")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(contentJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":733,\"errmsg\":\"电话已被注册\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新用户资料 (电话及邮箱格式不正确)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void modifyUserIncorrectEmailAndTel() throws Exception {
        String token = testUserLogin();

        String contentJson = "{\n" +
                "    \"name\": \"李黑\",\n" +
                "    \"email\": \"hanhan@hanhan\",\n" +
                "    \"mobile\": \"fasfsfahsfuuy\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/57")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(contentJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        Integer errNo = JacksonUtil.parseInteger(responseString, "errno");
        String errMsg = JacksonUtil.parseString(responseString, "errmsg");
        assert errNo != null;
        assert errMsg != null;
        Assert.isTrue(errNo.equals(503), "返回 errno 不正确！");
        Assert.isTrue(errMsg.equals("Email 格式不正确;手机号码格式不正确;") || errMsg.equals("手机号码格式不正确;Email 格式不正确;"),
                "返回 errmsg 不正确！");
    }

    /**
     * 测试更新用户资料 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void modifyNilUser() throws Exception {
        String token = testUserLogin();

        String contentJson = "{\n" +
                "    \"name\": \"李大力\",\n" +
                "    \"email\": \"ooad@han-li.cn\",\n" +
                "    \"mobile\": \"12345678\"\n" +
                "}";

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/99")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试更新用户资料 (用户已被删除)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void modifyDeletedUser() throws Exception {
        String token = testUserLogin();

        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/privilege/adminusers/58")
                        .header("authorization", token)
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试硬更新
        String contentJson = "{\n" +
                "    \"name\": \"李大力\",\n" +
                "    \"email\": \"ooad@han-li.cn\",\n" +
                "    \"mobile\": \"12345678\"\n" +
                "}";

        responseString = this.mvc.perform(
                put("/privilege/adminusers/58")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试 (逻辑) 删除用户
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void deleteUser() throws Exception {
        String token = testUserLogin();

        String responseString = this.mvc.perform(
                delete("/privilege/adminusers/58")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关数据是否有真的删除
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(58L);
        Assert.state(updatedPo.getState() == (byte) User.State.DELETE.getCode().intValue(), "这个用户并未被删除！");
    }

    /**
     * 测试删除用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void deleteNilUser() throws Exception {
        String token = testUserLogin();

        String responseString = this.mvc.perform(
                delete("/privilege/adminusers/120")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试删除用户 (用户已被删除过)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void deleteDeletedUser() throws Exception {
        String token = testUserLogin();

        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/privilege/adminusers/47")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试还能否被删除
        responseString = this.mvc.perform(
                delete("/privilege/adminusers/47")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试封禁用户
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void forbidUser() throws Exception {
        String token = testUserLogin();

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/59/forbid")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关用户是否有真的被解封
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(updatedPo.getState() == (byte) User.State.FORBID.getCode().intValue(), "这个用户并未被封禁！");
    }

    /**
     * 测试封禁用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void forbidNilUser() throws Exception {
        String token = testUserLogin();

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/90/forbid")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试封禁用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void forbidDeletedUser() throws Exception {
        String token = testUserLogin();
        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/privilege/adminusers/48")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试硬更改
        responseString = this.mvc.perform(
                put("/privilege/adminusers/48/forbid")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试解封用户
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void releaseUser() throws Exception {
        String token = testUserLogin();

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/59/release")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试有关用户是否有真的被解封
        UserPo updatedPo = userPoMapper.selectByPrimaryKey(59L);
        Assert.state(updatedPo.getState() == (byte) User.State.NORM.getCode().intValue(), "这个用户并未被解禁！");
    }

    /**
     * 测试解封用户 (查无此用户)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void releaseNilUser() throws Exception {
        String token = testUserLogin();

        String responseString = this.mvc.perform(
                put("/privilege/adminusers/321/release")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 测试解封用户 (已被删除的用户)
     * @throws Exception Assert 或 HTTP 错误
     * @author 19720182203919 李涵
     * Created at 4/11/2020 23:55
     * Modified by 19720182203919 李涵 at 7/11/2020 21:30
     */
    @Test
    public void releaseDeletedUser() throws Exception {
        String token = testUserLogin();
        // 逻辑删除
        String responseString = this.mvc.perform(
                delete("/privilege/adminusers/46")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        // 测试硬解封
        responseString = this.mvc.perform(
                put("/privilege/adminusers/46/release")
                        .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /* auth009 测试用例结束 */

    /**
     * 测试用户登录例程
     *
     * @return 测试用户登录 token
     * @throws Exception 登录失败或其他因素
     * @author 19720182203919 李涵
     * Created at 7/11/2020 21:54
     */
    private String testUserLogin() throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName("13088admin");
        vo.setPassword("123456");

        String requireJson = JacksonUtil.toJson(vo);
        assert requireJson != null;
        String response = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
        return  JacksonUtil.parseString(response, "data");
    }

    /**
     * 测试 token 生成例程 (指定时间)
     *
     * This method is no longer being used in token retrieval because mock login has been engaged;
     * the method, however, could afterwards be used temporarily as an alternative token retrieval approach
     * whenever the Redis server is not applicable and/or an expire time of such a token must be specified.
     * @param expiredAfterSeconds 在该秒数之后此 token 会过期
     * @param isValidToken 如果为 true，则生成无效 token
     * @author 19720182203919 李涵
     * Created at 7/11/2020 21:54
     * @return 生成的 token
     */
    private String genTestToken(int expiredAfterSeconds, boolean isValidToken) {
        if (isValidToken) {
            return new JwtHelper().createToken(1L, 0L, expiredAfterSeconds);
        } else {
            // 这是一个乱写的 token (长度都是 271)
            return "xxyyeXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.haFuckHAJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMTA4MTA0MjQ5OUkyIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjowLCJleHAiOjE2MDQ4MDMzNzAsInVzZXJJZCI6MSwiaWF0IjoxNjA0ODAzMzY5fQgg.12345678945621HAHAN_AXftnZLIvLW1gSHXQQtxixi";
        }
    }
}
