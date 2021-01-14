package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;


/**
 * @author Li Zihan 24320182203227
 * @date Created in 2020/12/9 12:33
 **/
@SpringBootTest(classes = PublicTestApp.class)
@AutoConfigureWebTestClient(timeout = "100000")
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LiZihanTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void LiZihanTest(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    /** 13
     * 查看用户的权限测试3-管理员登录
     * @throws Exception
     * @author Li Zihan
     */
//    @Test
//    public void getUserPrivTest3() throws Exception {
//        String token = this.login("13088admin","123456");
//        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/1/privileges").header("authorization",token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":2,\"name\":\"查看任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":12,\"name\":\"给角色增加权限\",\"url\":\"/roles/{id}/privileges/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:06:03\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":13,\"name\":\"取消角色权限\",\"url\":\"/roleprivileges/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:06:43\",\"gmtModified\":\"2020-11-03T21:30:31\"},{\"id\":14,\"name\":\"修改权限信息\",\"url\":\"/privileges/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:08:18\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":15,\"name\":\"查看所有用户的角色\",\"url\":\"/adminusers/{id}/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:53:38\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":16,\"name\":\"查看所有代理\",\"url\":\"/proxies\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:55:31\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":17,\"name\":\"禁止代理关系\",\"url\":\"/allproxies/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T17:57:45\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":20,\"name\":\"查询角色\",\"url\":\"/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-04T13:10:02\",\"gmtModified\":null},{\"id\":21,\"name\":\"获得用户的权限\",\"url\":\"/adminusers/{id}/privileges\",\"requestType\":0,\"gmtCreate\":\"2020-11-04T13:10:02\",\"gmtModified\":null}],\"errmsg\":\"成功\"}\n";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
//    }

    /**
     * 1
     * 不登录查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies1() throws Exception {

        //String token = this.creatTestToken(1L, 0L, 100);
        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
                //.header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":704,\"errmsg\":\"需要先登录\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

//    /**
//     * 2
//     * 查询所有用户代理关系
//     *
//     * @author 24320182203227 Li Zihan
//     */
//    @Test
//    public void getListProxies2() throws Exception {
//
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"data\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }
//
//    /**
//     * 3
//     * 平台管理员查询自己部门所有用户代理关系
//     *
//     * @author 24320182203227 Li Zihan
//     */
//    @Test
//    public void getListProxies3() throws Exception {
//
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"data\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }
//    /**
//     * 4
//     * 管理员查询自己部门所有用户代理关系
//     *
//     * @author 24320182203227 Li Zihan
//     */
//    @Test
//    public void getListProxies4() throws Exception {
//
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/1/proxies")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"data\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }
//
//    /**
//     * 5
//     * 管理员查询非自己部门用户代理关系
//     *
//     * @author 24320182203227 Li Zihan
//     */
//    @Test
//    public void getListProxies5() throws Exception {
//
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"data\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }
//    /**
//     * 6
//     * 管理员查询非自己部门用户代理关系
//     *
//     * @author 24320182203227 Li Zihan
//     */
//    @Test
//    public void getListProxies6() throws Exception {
//
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/1/proxies")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"data\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }

    /**
     * 7
     * 伪造token查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies7() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":501,\"errmsg\":\"JWT不合法\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 8
     * 使用不存在部门查询所有用户代理关系
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getListProxies8() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":501,\"errmsg\":\"JWT不合法\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 9
     * 管理员查询不存在用户信息
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getUserInfo1() throws Exception {

        String token = this.login("13088admin", "123456");
        boolean approve = true;
        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/2")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 10
     * 未登录查询不存在的用户信息
     *
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getUserInfo2() throws Exception {

        //String token = this.creatTestToken(1L, 0L, 100);
        boolean approve = true;
        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/2")
                //.header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":704,\"errmsg\":\"需要先登录\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /** 11
     * 查看用户的权限测试1-未登录
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void getUserPrivTest1() throws Exception {
        byte[] ret = manageClient.get().uri("/shops/0/adminusers/1/privileges")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }
    /** 12
     * 查看用户的权限测试2-使用伪造token
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void getUserPrivTest2() throws Exception {
        byte[] ret = manageClient.get().uri("/shops/0/adminusers/1/privileges")
                .header("authorization", "12345")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /** 14
     * 查询用户权限测试4 高级查低级
     * 管理员查询其他用户权限
     * @throws Exception
     * @author Li Zihan
     */
//    @Test
//    public void getUserPrivTest4() throws Exception {
//        String token = this.login("13088admin","123456");
//        byte[] responseString = manageClient.get().uri("shops/0/adminusers/46/privileges")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":2,\"name\":\"查看任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":15,\"name\":\"查看所有用户的角色\",\"url\":\"/adminusers/{id}/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:53:38\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":16,\"name\":\"查看所有代理\",\"url\":\"/proxies\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:55:31\",\"gmtModified\":\"2020-11-03T19:48:47\"}],\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }
    /** 15
     * 查询用户权限测试5 低级查高级
     * 非管理员用户查看用户权限
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void getUserPrivTest5() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/1/privileges")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 16
     * 赋予用户角色测试1
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void assignRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/47/roles/1000").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 17
     * 赋予用户角色测试2
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void assignRoleTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/10000/roles/84").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 18
     * Li Zihan
     * 赋予用户角色测试3
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void assignRoleTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/48/roles/86").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":737,\"errmsg\":\"用户已拥有该角色\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 19
     * 取消用户角色测试1 成功
     * @throws Exception
     * @author Li Zihan
     */
    //有重复测试用例
//    @Test
//    public void revokeRoleTest1() throws Exception {
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/50/roles/87").header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//        byte[] responseString1 = manageClient.get().uri("/shops/0/adminusers/50/roles").header("authorization", token)
//                .exchange()
//                .expectHeader()
//                .contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse1, new String(responseString1, "UTF-8"), false);
//    }

    /**
     * 20
     * 取消用户角色测试2:角色不存在
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void revokeRoleTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/50/roles/1000").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 21
     * 取消用户角色测试3:用户不存在
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void revokeRoleTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/10000/roles/87").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 22
     * 取消用户角色测试4:角色无该用户
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void revokeRoleTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/57/roles/84").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 23
     * 平台管理员新增角色角色名重复
     * @author Li Zihan
     */
    @Test
    public void insertRoleTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"管理员\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/roles")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ROLE_REGISTERED.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 24
     * 平台管理员新增角色角色名为空
     * @author Li Zihan
     */
    @Test
    public void insertRoleTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/roles")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                //.jsonPath("$.errmsg").isEqualTo("角色名不能为空;")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 25
     * 未登录新增角色
     * Li Zihan
     */
    @Test
    public void insertRoleTest5() throws Exception {
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/roles")
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 26
     * 查找用户 Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserById1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/46").header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 27
     * 查找不存在的用户 Li Zihan
     * @throws Exception
     */
    @Test
    public void findUserById2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/23")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 28 获得管理员用户的所有状态
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findAdminUserState() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/adminusers/states").header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

//    /**
//     * 29 管理员查看用户拥有的权限
//     * 查找用户 Li ZiHan
//     * @throws Exception
//     */
//    @Test
//    public void findUserPrivs1() throws Exception {
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/1/privileges").header("authorization", token)
//                .exchange()
//                .expectHeader()
//                .contentType("application/json")
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }

//    /**
//     * 30 同部门管理员查看用户拥有的权限
//     * Li ZiHan
//     * @throws Exception
//     */
//    @Test
//    public void findUserPrivs2() throws Exception {
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/1/adminusers/1/privileges").header("authorization", token)
//                .exchange()
//                .expectHeader()
//                .contentType("application/json")
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }

    /**
     * 31 不同部门管理员获得用户拥有的权限
     * Li ZiHan
     * @throws Exception
     */
//    @Test
//    public void findUserPrivs3() throws Exception {
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/1/privileges").header("authorization", token)
//                .exchange()
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }

    /**
     * 32 管理员查看角色拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findRolePrivs1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/roles/23/privileges").header("authorization", token)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 33 同部门管理员查看角色拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findRolePrivs2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops​/1/roles​/87/privileges").header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 34 不同部门管理员获得用户拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findRolePrivs3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/2/adminusers/86/privileges").header("authorization", token)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 35 管理员查看角色拥有的权限
     *  Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserRolePrivs1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/roles/23/privileges").header("authorization", token)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

//    /**
//     * 36 同部门管理员查看角色拥有的权限
//     * Li ZiHan
//     * @throws Exception
//     */
//    @Test
//    public void findUserRolePrivs2() throws Exception {
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/1/roles/87/privileges").header("authorization", token)
//                .exchange()
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }

    /**
     * 37 不同部门管理员获得用户拥有的权限
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserRolePrivs3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/2/adminusers/86/privileges").header("authorization", token)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 38 获取所有权限
     * Li Zihan
     * @throws Exception
     */
//    @Test
//    public void getAllPriv1() throws Exception{
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/privileges").header("authorization",token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBodyContent();
//
//        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":18,\"pages\":2,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"查看任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":0,\"gmtCreate\":\"2020-11-01T09:52:20\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":3,\"name\":\"修改任意用户信息\",\"url\":\"/adminusers/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:53:03\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":4,\"name\":\"删除用户\",\"url\":\"/adminusers/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T09:53:36\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":5,\"name\":\"恢复用户\",\"url\":\"/adminusers/{id}/release\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T09:59:24\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":6,\"name\":\"禁止用户登录\",\"url\":\"/adminusers/{id}/forbid\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:02:32\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":7,\"name\":\"赋予用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:02:35\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":8,\"name\":\"取消用户角色\",\"url\":\"/adminusers/{id}/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:03:16\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":9,\"name\":\"新增角色\",\"url\":\"/roles\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:04:09\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":10,\"name\":\"删除角色\",\"url\":\"/roles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:04:42\",\"gmtModified\":\"2020-11-02T21:51:45\"},{\"id\":11,\"name\":\"修改角色信息\",\"url\":\"/roles/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:05:20\",\"gmtModified\":\"2020-11-02T21:51:45\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
//    }
    /**
     * 39 获取所有权限
     * Li Zihan
     * @throws Exception
     */

    @Test
    public void getAllPriv2() throws Exception {
        manageClient.get().uri("/privileges")
                .exchange()
                .expectStatus().isUnauthorized();

    }

    /**
     * 40 获取所有权限（第二页）
     * Li Zihan
     * @throws Exception
     */
//    @Test
//    public  void getAllPriv3() throws Exception {
//        String token = this.login("13088admin", "123456");
//        // 我只能把query加在路径后面了，找不到一个加query的函数
//        byte[] responseString = manageClient.get().uri("/privileges?page=2&pageSize=8").header("authorization", token).exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo("成功")
//                .jsonPath("$.data.pageSize").isEqualTo(8)
//                .returnResult().getResponseBodyContent();
//
//        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":8,\"page\":2,\"list\":[{\"id\":12,\"name\":\"给角色增加权限\",\"url\":\"/roles/{id}/privileges/{id}\",\"requestType\":1,\"gmtCreate\":\"2020-11-01T10:06:03\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":13,\"name\":\"取消角色权限\",\"url\":\"/roleprivileges/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-01T10:06:43\",\"gmtModified\":\"2020-11-03T21:30:31\"},{\"id\":14,\"name\":\"修改权限信息\",\"url\":\"/privileges/{id}\",\"requestType\":2,\"gmtCreate\":\"2020-11-01T10:08:18\",\"gmtModified\":\"2020-11-02T21:51:46\"},{\"id\":15,\"name\":\"查看所有用户的角色\",\"url\":\"/adminusers/{id}/roles\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:53:38\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":16,\"name\":\"查看所有代理\",\"url\":\"/proxies\",\"requestType\":0,\"gmtCreate\":\"2020-11-03T17:55:31\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":17,\"name\":\"禁止代理关系\",\"url\":\"/allproxies/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T17:57:45\",\"gmtModified\":\"2020-11-03T19:48:47\"},{\"id\":18,\"name\":\"取消任意用户角色\",\"url\":\"/adminuserroles/{id}\",\"requestType\":3,\"gmtCreate\":\"2020-11-03T19:52:04\",\"gmtModified\":\"2020-11-03T19:56:43\"},{\"id\":19,\"name\":\"管理员设置用户代理关系\",\"url\":\"/ausers/{id}/busers/{id}:\",\"requestType\":1,\"gmtCreate\":\"2020-11-04T13:10:02\",\"gmtModified\":null}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//    }


    /**
     * 41 管理员查看所有用户的角色
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserRole1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/1/roles").header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 42
     * @author Li Zihan
     * 没有输入用户名的用户登录
     */
    @Test
    public void login1() throws Exception {
        String requireJson = null;
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;
        requireJson = "{\"userName\":\"8131600001\",\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_USER_FORBIDDEN.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 43
     * @author Li Zihan
     * 没有输入密码（密码空）的用户登录
     */
    @Test
    public void login2() throws Exception {
        String requireJson = null;
        byte[] responseString = null;
        WebTestClient.RequestHeadersSpec res = null;
        requireJson = "{\"userName\":\"8131600001\",\"password\":\"123456\"}";
        res = manageClient.post().uri("/adminusers/login").bodyValue(requireJson);
        responseString = res.exchange().expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_USER_FORBIDDEN.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 43 同部门管理员查看用户的角色
     * Li ZiHan
     * @throws Exception
     */
    @Test
    public void findUserRole2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/1/adminusers/59/roles").header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

//    /**
//     * 45 管理员查询所有角色信息
//     * @author Li Zihan
//     * @throws Exception
//     */
//    @Test
//    public void selectRoleTest3() throws Exception {
//        String token = this.login("13088admin", "123456");
//        byte[] responseString = manageClient.get().uri("/shops/0/roles?page=1&pageSize=2")
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":23,\"name\":\"管理员\",\"desc\":\"超级管理员，所有权限都有\",\"createdBy\":1,\"departId\":0,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"},{\"id\":80,\"name\":\"财务\",\"desc\":null,\"createdBy\":1,\"departId\":0,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"}]},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
//    }

    /**
     * 46
     * 平台管理员查询自己部门所有用户代理关系
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getProxies1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 47
     * 管理员查询自己部门所有用户代理关系
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getProxies2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/1/proxies")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 48
     * 管理员查询非自己部门用户代理关系
     * @author 24320182203227 Li Zihan
     */
    @Test
    public void getProxies3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/proxies")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    @Test
    public void revokeRoleTest7() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/50/roles/1000").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 21
     * 取消用户角色测试3:用户不存在
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void revokeRoleTest8() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/10000/roles/87").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 22
     * 取消用户角色测试4:角色无该用户
     * @throws Exception
     * @author Li Zihan
     */
    @Test
    public void revokeRoleTest9() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/57/roles/84").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504,\n" +
                "    \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


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