package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
public class SongRunhanTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;


    @BeforeEach
    public void setUp(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login1() throws Exception {
        String requireJson = null;
        byte[] responseString = null;

        WebTestClient.RequestHeadersSpec res = null;

        //region Email未确认用户登录
        requireJson = "{\"userName\":\"5264500009\",\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.EMAIL_NOTVERIFIED.getCode())
                .returnResult()
                .getResponseBodyContent();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login2() throws Exception {
        String requireJson = null;
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;

        //region 密码错误的用户登录
        requireJson = "{\"userName\":\"13088admin\",\"password\":\"000000\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult().getResponseBodyContent();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login3() throws Exception {
        String requireJson = null;
        byte[] responseString = null;

        WebTestClient.RequestHeadersSpec res = null;

        //region 用户名错误的用户登录
        requireJson = "{\"userName\":\"NotExist\",\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult().getResponseBodyContent();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login4() throws Exception {
        String requireJson = null;
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;

        //region 没有输入用户名的用户登录
        requireJson = "{\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange().expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo("必须输入用户名;")
                .returnResult().getResponseBodyContent();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login5() throws Exception {
        String requireJson = null;
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;

        //region 没有输入密码（密码空）的用户登录
        requireJson = "{\"userName\":\"537300010\",\"password\":\"\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange().expectStatus().isBadRequest()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.errmsg").isEqualTo("必须输入密码;")
                .returnResult().getResponseBodyContent();
        //endregion
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login6() throws Exception {
        String requireJson = null;
        byte[] response = null;
        WebTestClient.RequestHeadersSpec res = null;

        //region 用户重复登录
        requireJson = "{\"userName\":\"13088admin\",\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        response = res.exchange().expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").exists()
                .returnResult().getResponseBodyContent();

        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);

        byte[] response1 = res.exchange().expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult().getResponseBodyContent();

        String jwt = JacksonUtil.parseString(new String(response,"UTF-8"), "data");
        String jwt1 = JacksonUtil.parseString(new String(response1,"UTF-8"), "data");
        assertNotEquals(jwt, jwt1);
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4 16:00
     */
    @Test
    public void login7() throws Exception {
        String requireJson = null;
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;
        //region 当前状态不可登录的用户登录
        requireJson = "{\"userName\":\"8884810086\",\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);

        responseString = res.exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_USER_FORBIDDEN.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult().getResponseBodyContent();
        //endregion
    }

    /**
     * 这个是邱老师写的，不要算数！！！
     * 这个是邱老师写的，不要算数！！！
     * 这个是邱老师写的，不要算数！！！
     * @throws Exception
     */
    @Test
    public void login8() throws Exception {
        String requireJson = null;

        //正常用户登录
        requireJson = "{\"userName\":\"13088admin\",\"password\":\"123456\"}";
        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        //endregion
    }

    /**
     * 获取所有权限（第一页）
     * @throws Exception
     */
    @Test
    public void getAllPriv1() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/privileges").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.list").isArray()
                .returnResult().getResponseBodyContent();
        //String expectedResponse = "{\"errno\":0,\"data\":{\"total\":114,\"pages\":12,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"查看任意用户信息\",\"url\":\"/shops/{id}/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/shops/{id}/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/shops/{id}/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/shops/{id}/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/shops/{id}/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/shops/{id}/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/shops/{id}/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/shops/{id}/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/shops/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/shops/{id}/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"}]},\"errmsg\":\"成功\"}";
        //JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 这个是邱老师写的，不要算数！！！
     * 这个是邱老师写的，不要算数！！！
     * @throws Exception
     */
    @Test
    public void getAllPriv2() throws Exception {
        manageClient.get().uri("/privileges")
                .exchange()
                .expectStatus().isUnauthorized();
//                .expectHeader().contentType("application/json;charset=UTF-8");

    }

    /**
     * 获取所有权限（第二页）
     * @throws Exception
     */
    @Test
    public void getAllPriv3() throws Exception {
        String token = this.login("13088admin", "123456");
        // 我只能把query加在路径后面了，找不到一个加query的函数
        byte[] responseString = manageClient.get().uri("/privileges?page=2&pageSize=10").header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list").isArray()
                .returnResult().getResponseBodyContent();
        var x = new String(responseString, "UTF-8");
//        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":114,\"pages\":12,\"pageSize\":10,\"page\":2,\"list\":[{\"id\":12,\"name\":\"给角色增加权限\",\"url\":\"/shops/{id}/roles/{id}/privileges/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:06:03\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":13,\"name\":\"取消角色权限\",\"url\":\"/shops/{id}/roleprivileges/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:06:43\",\"gmtModified\":\"2020-11-03T21:30:31\"},{\"id\":14,\"name\":\"修改权限信息\",\"url\":\"/privileges/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:08:18\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":17,\"name\":\"禁止代理关系\",\"url\":\"/shops/{id}/proxies/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T17:57:45\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":18,\"name\":\"取消任意用户角色\",\"url\":\"/shops/{id}/adminuserroles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T19:52:04\",\"gmtModified\":\"2020-11-03T19:56:43\"},{\"id\":19,\"name\":\"管理员设置用户代理关系\",\"url\":\"/shops/{id}/ausers/{id}/busers/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-04T13:10:02\",\"gmtModified\":null},{\"id\":21,\"name\":\"管理员对SPU修改团购活动\",\"url\":\"/shops/{id}/groupons/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-12-12T14:28:52\",\"gmtModified\":null},{\"id\":22,\"name\":\"管理员删除SPU团购活动\",\"url\":\"/shops/{id}/groupons/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-12-12T14:28:55\",\"gmtModified\":null},{\"id\":28,\"name\":\"管理员审核店铺\",\"url\":\"/shops/{id}/newshops/{id}/audit\",\"requestType\":2,\"gmtCreate\":\"2020-12-12T14:29:01\",\"gmtModified\":null},{\"id\":29,\"name\":\"店家上线店铺\",\"url\":\"/shops/{id}/onshelves\",\"requestType\":2,\"gmtCreate\":\"2020-12-12T14:29:02\",\"gmtModified\":null}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * @author Song Runhan
     * @date Created in 2020/11/4/ 16:00
     */
    @Test
    public void logout() throws  Exception{
        String requireJson = null;
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;

        requireJson = "{\"userName\":\"13088admin\",\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange().expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").exists()
                .returnResult().getResponseBodyContent();
        String json = JacksonUtil.parseString(new String(responseString, "UTF-8"),"data");

        //region 用户正常登出
        res = manageClient.get().uri("/adminusers/logout").header("authorization",json);
        responseString = res.exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult().getResponseBodyContent();
        //endregion
    }

    /**
     * 这个不是测试用例，不要算数！！！
     * 这个不是测试用例，不要算数！！！
     * 这个不是测试用例，不要算数！！！
     * @param userName 登录的用户名
     * @param password 登录的密码
     * @return token
     * @throws Exception
     */
    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }
}
