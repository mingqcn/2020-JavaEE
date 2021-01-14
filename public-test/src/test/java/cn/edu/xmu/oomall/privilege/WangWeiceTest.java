package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 角色公开测试
 * 测试需还原数据库数据
 * @author 王纬策
 * @date 2020/12/01 12:28
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WangWeiceTest {

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

// region 权限测试
    /**
     * 登录
     */
    public String LoginSuccess(String name, String pass) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", name);
        body.put("password", pass);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String token = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
        return token;
    }

    /**
     * 登录失败
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void adminLoginFail() throws Exception {
        String requireJson = "{\"userName\":\"13088admin\",\"password\":\"12345\"}";
        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_ACCOUNT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 未登录查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest1() throws Exception {
        byte[] ret = manageClient.get().uri("/shops/0/roles?page=1&pageSize=2")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 使用伪造token查询角色信息
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest2() throws Exception {
        byte[] ret = manageClient.get().uri("/shops/0/roles?page=1&pageSize=2")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查询所有角色信息，得限制该测试先于增加角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    @Order(0)
    public void selectRoleTest3() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{shopId}/roles?page=1&pageSize=2", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":23,\"name\":\"管理员\",\"desc\":\"超级管理员，所有权限都有\",\"createdBy\":1,\"departId\":0,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"},{\"id\":80,\"name\":\"财务\",\"desc\":null,\"createdBy\":1,\"departId\":0,\"gmtCreate\":\"2020-11-01T09:48:24\",\"gmtModified\":\"2020-11-01T09:48:24\"}]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 查询角色失败 高级查低级
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest4() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{shopId}/roles?page=1&pageSize=2", 1)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 查询角色失败 低级查高级
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void selectRoleTest5() throws Exception {
        String token = LoginSuccess("537300010", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{shopId}/roles?page=1&pageSize=2", 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员新增角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void insertRoleTest1() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        String roleJson = "{\"descr\": \"test\",\"name\": \"test\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/roles")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"data\":{\"name\":\"test\",\"createdBy\":1,\"departId\":0,\"desc\":\"test\",\"gmtModified\":null}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 部门管理员新增角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void insertRoleTest2() throws Exception {
        String token = LoginSuccess("537300010", "123456");
        String roleJson = "{\"descr\": \"test2\",\"name\": \"test2\"}";
        byte[] responseString = manageClient.post().uri("/shops/1/roles")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"data\":{\"name\":\"test2\",\"createdBy\":59,\"departId\":1,\"desc\":\"test2\",\"gmtModified\":null}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 平台管理员新增角色角色名重复
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void insertRoleTest3() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
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
     * 平台管理员新增角色角色名为空
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void insertRoleTest4() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/roles")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 未登录新增角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void insertRoleTest5() throws Exception {
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        byte[] responseString = manageClient.post().uri("/shops/1/roles")
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 伪造token新增角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void insertRoleTest6() throws Exception {
        String roleJson = "{\"descr\": \"管理员test\",\"name\": \"\"}";
        byte[] responseString = manageClient.post().uri("/shops/1/roles")
                .header("authorization", "test")
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest1() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"testU\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/roles/{id}", 0, 80)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色角色名为空
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest2() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        String roleJson = "{\"descr\": \"testU\",\"name\": \"\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/roles/{id}", 0, 87)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色角色名重复
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest3() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        String roleJson = "{\"descr\": \"客服test\",\"name\": \"客服\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/roles/{id}", 0, 83)
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
     * 平台管理员修改角色id不存在
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest4() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/roles/{id}", 0, 0)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员修改角色id与部门id不匹配
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest5() throws Exception {
        String token = LoginSuccess("537300010", "123456");
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/roles/{id}", 0, 85)
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 未登录修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest6() throws Exception {
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/roles/{id}", 0, 85)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 伪造token修改角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void updateRoleTest7() throws Exception {
        String roleJson = "{\"descr\": \"t\",\"name\": \"t\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/roles/{id}", 0, 85)
                .header("authorization", "test")
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 平台管理员删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest1() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{shopId}/roles/{id}", 0, 82)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除角色id不存在
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest2() throws Exception {
        String token = LoginSuccess("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{shopId}/roles/{id}", 0, 0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除角色id与部门id不匹配
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest3() throws Exception {
        String token = LoginSuccess("537300010", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{shopId}/roles/{id}", 0, 86)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 未登录删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest4() throws Exception {
        byte[] responseString = manageClient.delete().uri("/shops/{shopId}/roles/{id}", 0, 0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 伪造token删除角色
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/30 12:46
     * modifiedBy 王纬策 2020/11/30 12:46
     */
    @Test
    public void deleteRoleTest5() throws Exception {
        byte[] responseString = manageClient.delete().uri("/shops/{shopId}/roles/{id}", 0, 0)
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }
// endregion

}
