package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * 其他模块测试
 * @author 王琛 24320182203277
 * @date
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class WangChenTest {

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
     * 新增用户用于测试
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(1)
    public void userTest1() throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", "12457487789");
        body.put("realName", "1888888454");
        body.put("password", "1a2B3_");
        body.put("birthday", "2020-12-09");
        body.put("mobile", "11154874631");
        body.put("email", "4324432@kkkk.com");
        body.put("gender", 0);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        log.debug("尝试登录");
        userLogin("12457487789","1a2B3_");
    }

    /**
     * PUT /shops/{shopid}/shareactivities/{id}/online测试,后面测试上线300成功
     * @return void
     * @author Wc
     * created 
     */
    @Test
    @Order(2)
    public void onlineShareActivity1() throws Exception {
        String token = adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/304402/online").header("authorization", token)
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
     * PUT /shops/{shopid}/shareactivities/{id}/online测试,后面测试上线400成功
     * @return void
     * @author Wc
     * created at 
     */
    @Test
    @Order(3)
    public void onlineShareActivity2() throws Exception {
        String token = adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/1/shareactivities/305687/online").header("authorization", token)
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
     * /skus/{id}/shares测试
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(4)
    public void addSharesTest1() throws Exception {
        String token = userLogin("12457487789","1a2B3_");
        byte[] responseString = mallClient.post().uri("skus/300/shares").header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"quantity\":0,\"sku\":{\"originalPrice\":68000,\"price\":68000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621fe110292.jpg\",\"name\":\"+\",\"disable\":false,\"id\":300,\"inventory\":1}}";

        JSONObject json = JSONObject.parseObject(new String(responseString));
        json = json.getJSONObject("data");
        json.remove("gmtCreate");

        JSONAssert.assertEquals(expectedResponse, json.toJSONString(), false);
    }

    @Test
    @Order(5)
    public void addSharesTest2() throws Exception {
        String token = userLogin("12457487789","1a2B3_");
        byte[] responseString = mallClient.post().uri("skus/400/shares").header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"quantity\":0,\"sku\":{\"originalPrice\":12000,\"price\":12000,\"imageUrl\":\"http://47.52.88.176/file/images/201702/file_58a10d2f39fd6.jpg\",\"name\":\"+\",\"disable\":false,\"id\":400,\"inventory\":1}}";

        JSONObject json = JSONObject.parseObject(new String(responseString));
        json = json.getJSONObject("data");
        json.remove("gmtCreate");

        JSONAssert.assertEquals(expectedResponse, json.toJSONString(), false);
    }





    /**
     * shares测试获取order(2),order(3)新增分享
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(6)
    public void usergetshares1() throws Exception {
        String token = userLogin("12457487789","1a2B3_");
        byte[] responseString = mallClient.get().uri("shares").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "[{\"quantity\":0,\"sku\":{\"originalPrice\":68000,\"price\":68000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621fe110292.jpg\",\"name\":\"+\",\"disable\":false,\"id\":300,\"inventory\":1}},{\"quantity\":0,\"sku\":{\"originalPrice\":12000,\"price\":12000,\"imageUrl\":\"http://47.52.88.176/file/images/201702/file_58a10d2f39fd6.jpg\",\"name\":\"+\",\"disable\":false,\"id\":400,\"inventory\":1}}]";

        JSONObject json = JSONObject.parseObject(new String(responseString));
        JSONArray jsonArray = json.getJSONObject("data").getJSONArray("list");

        json = jsonArray.getJSONObject(0);
        json.remove("gmtCreate");
        json.remove("sharerId");
        json.remove("id");
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.fluentAdd(json);
        json = jsonArray.getJSONObject(1);
        json.remove("gmtCreate");
        json.remove("sharerId");
        json.remove("id");
        jsonArray1.fluentAdd(json);
        JSONAssert.assertEquals(expectedResponse, jsonArray1.toJSONString(), false);

    }

    /**
     * shares测试用户没有分享，返回空
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(7)
    public void usergetshares2() throws Exception {
        String token = userLogin("14902184265","123456");
        byte[] responseString = mallClient.get().uri("shares").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 新增分享 /skus/{id}/shares,id不存在
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(8)
    public void addShares() throws Exception {
        String token = userLogin("45420889878","123456");
        byte[] responseString = mallClient.post().uri("/skus/1111111111111/shares").header("authorization", token)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 504\n" +
                "}";
        JSONObject b = JSON.parseObject(new String(responseString));
        b.remove("errmsg");
        JSONAssert.assertEquals(expectedResponse, b.toJSONString(), false);
    }

    /**
     * shares测试用户获取分享
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(9)
    public void usergetshares3() throws Exception {
        String token = userLogin("45420889878","123456");

        byte[] responseString = mallClient.get().uri("shares").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 4,\n" +
                "        \"pages\": 1,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442597,\n" +
                "                \"sharerId\": 2487,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 615,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b568ca098076.png\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 398,\n" +
                "                    \"price\": 398,\n" +
                "                    \"disable\": false\n" +
                "                },\n" +
                "                \"quantity\": 258,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 442693,\n" +
                "                \"sharerId\": 2487,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 503,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
//                "                    \"imageUrl\": \"\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 0,\n" +
                "                    \"price\": 0,\n" +
                "                    \"disable\": false\n" +
                "                },\n" +
                "                \"quantity\": 201,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 442695,\n" +
                "                \"sharerId\": 2487,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 543,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_5a10e72d97524.jpg\",\n" +
                "                    \"inventory\": 1000,\n" +
                "                    \"originalPrice\": 1799,\n" +
                "                    \"price\": 1799,\n" +
                "                    \"disable\": false\n" +
                "                },\n" +
                "                \"quantity\": 586,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 442867,\n" +
                "                \"sharerId\": 2487,\n" +
                "                \"sku\": {\n" +
                "                    \"id\": 402,\n" +
                "                    \"name\": \"+\",\n" +
                "                    \"skuSn\": null,\n" +
                "                    \"imageUrl\": \"http://47.52.88.176/file/images/201702/file_58a16e1a6f15b.JPG\",\n" +
                "                    \"inventory\": 1,\n" +
                "                    \"originalPrice\": 38000,\n" +
                "                    \"price\": 38000,\n" +
                "                    \"disable\": false\n" +
                "                },\n" +
                "                \"quantity\": 731,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * shares测试用户获取分享, 开始时间和结束时间格式错误,返回错误码503
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(10)
    public void usergetshares4() throws Exception {
        String token = userLogin("45420889878","123456");
        byte[] responseString = mallClient.get().uri("shares?beginTime=2020-12-22 1:2&endTime=2012-12-44 1:2:1 ").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * shares测试用户获取分享, 开始时间在结束时间之后，返回空
     * @return void
     * @author Wc
     * created at
     */
    @Test
    @Order(11)
    public void usergetshares5() throws Exception {
        String token = userLogin("45420889878","123456");
        byte[] responseString = mallClient.get().uri("shares?beginTime=2021-12-07 22:00:00&endTime=2010-12-07 22:00:00").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 0,\n" +
                "        \"pages\": 0,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }





    public String userLogin(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{
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
