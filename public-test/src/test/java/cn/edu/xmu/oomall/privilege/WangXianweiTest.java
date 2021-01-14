package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author 24320182203282 王显伟
 * created at 11/30/20 12:27 PM
 * @detail cn.edu.xmu.oomall
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
public class WangXianweiTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private String token = "";

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
     * 查看用户的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/47/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"user\": {\n" +
                "                \"userName\": \"2721900002\"\n" +
                "            },\n" +
                "            \"role\": {\n" +
                "                \"id\": 85,\n" +
                "                \"name\": \"总经办\"\n" +
                "            },\n" +
                "            \"creator\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            }" +
                "        },\n" +
                "        {\n" +
                "            \"user\": {\n" +
                "                \"id\": 47,\n" +
                "                \"userName\": \"2721900002\"\n" +
                "            },\n" +
                "            \"role\": {\n" +
                "                \"id\": 84,\n" +
                "                \"name\": \"文案\"\n" +
                "            },\n" +
                "            \"creator\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            }" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看用户的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest2() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/2/adminusers/49/roles").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看用户的角色测试3
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getUserRoleTest3() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/adminusers/100/roles").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看自己的角色测试1
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/adminusers/self/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 76,\n" +
                "            \"user\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            },\n" +
                "            \"role\": {\n" +
                "                \"id\": 23,\n" +
                "                \"name\": \"管理员\"\n" +
                "            },\n" +
                "            \"creator\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            },\n" +
                "            \"gmtCreate\": \"2020-11-01T09:48:24\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 查看自己的角色测试2
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void getSelfUserRoleTest2() throws Exception {
        String token = this.login("8532600003", "123456");
        byte[] responseString = manageClient.get().uri("/adminusers/self/roles").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 79,\n" +
                "            \"user\": {\n" +
                "                \"id\": 48,\n" +
                "                \"userName\": \"8532600003\"\n" +
                "            },\n" +
                "            \"role\": {\n" +
                "                \"id\": 86,\n" +
                "                \"name\": \"库管\"\n" +
                "            },\n" +
                "            \"creator\": {\n" +
                "                \"id\": 1,\n" +
                "                \"userName\": \"13088admin\"\n" +
                "            },\n" +
                "            \"gmtCreate\": \"2020-11-01T09:48:24\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试1
     * @throws Exception
     * @author Xianwei Wang
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
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试2
     * @throws Exception
     * @author Xianwei Wang
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
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试3
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void assignRoleTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/adminusers/47/roles/84").header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"user\": {\n" +
                "            \"id\": 47,\n" +
                "            \"userName\": \"2721900002\"\n" +
                "        },\n" +
                "        \"role\": {\n" +
                "            \"id\": 84,\n" +
                "            \"name\": \"文案\"\n" +
                "        },\n" +
                "        \"creator\": {\n" +
                "            \"id\": 1,\n" +
                "            \"userName\": \"13088admin\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 赋予用户角色测试3
     * @throws Exception
     * @author Xianwei Wang
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
        String expectedResponse = "{\"errno\":737}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 取消用户角色测试1 成功
     * @throws Exception
     * @author Xianwei Wang
     */
    @Test
    public void revokeRoleTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/50/roles/87").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 取消用户角色测试2:角色不存在
     * @throws Exception
     * @author Xianwei Wang
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
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 取消用户角色测试3:用户不存在
     * @throws Exception
     * @author Xianwei Wang
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
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 取消用户角色测试4:角色无该用户
     * @throws Exception
     * @author Xianwei Wang
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
                "    \"errno\": 504\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    private String login(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

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
