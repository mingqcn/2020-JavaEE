package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;


/**
 * 其他模块-时间段测试用例
 * @Author 胡佳乐 24320182203198
 * @Created 2020/12/15
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
@SpringBootTest(classes = PublicTestApp.class)
public class HuJialeTest {
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

    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(responseString, "UTF-8"), "data");
    }




    /**
     * 1.平台管理员获取广告时间段列表--page、pageSize为空--成功
     **/
    @Test
    @Order(1)
    public void selectAdTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/advertisement/timesegments").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString,"UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":{\"list\":[" +
                "{\"id\":1},{\"id\":2}," +
                "{\"id\":3},{\"id\":4}," +
                "{\"id\":5},{\"id\":6}," +
                "{\"id\":7},{\"id\":16}," +
                "{\"id\":17},{\"id\":18}" +
                "]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }




    /**
     * 2.平台管理员获取广告时间段列表--page=2&pageSize=5--成功
     **/
    @Test
    @Order(2)
    public void selectAdTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/advertisement/timesegments?page=2&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString,"UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":{\"list\":[" +
                "{\"id\":6},{\"id\":7},{\"id\":16}," +
                "{\"id\":17},{\"id\":18}]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 3.平台管理员新增广告时间段--成功
     */
    @Test
    @Order(3)
    public void insertAdTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 07:30:00");
        body.put("endTime", "2020-12-15 07:40:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString,"UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":28}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

    }


    /**
     * 4.平台管理员新增广告时间段-开始时间为空
     */
    @Test
    @Order(4)
    public void insertAdTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "");
        body.put("endTime", "2020-12-15 22:30:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 5.平台管理员新增广告时间段-结束时间为空
     */
    @Test
    @Order(5)
    public void insertAdTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 20:00:00");
        body.put("endTime", "");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 6.平台管理员新增广告时间段-开始时间大于结束时间
     */
    @Test
    @Order(6)
    public void insertAdTimeTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 22:00:00");
        body.put("endTime", "2020-12-15 20:00:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 7.平台管理员新增广告时间段-时段冲突
     */
    @Test
    @Order(7)
    public void insertAdTimeTest5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 00:00:00");
        body.put("endTime", "2020-12-15 02:00:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/advertisement/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 9.平台管理员删除广告时间段--时间段id不存在
     */
    @Test
    @Order(9)
    public void deleteAdTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/timesegments/40").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 10.平台管理员删除广告时间段--给定的时段id不属于广告时段
     */
    @Test
    @Order(10)
    public void deleteAdTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/timesegments/10").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }





    /**
     * 11.平台管理员获取秒杀时间段列表--page、pageSize为空--成功
     **/
    @Test
    @Order(11)
    public void selectFsTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/flashsale/timesegments").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        log.info(new String(responseString,"UTF-8"));

        String expectedResponse = "{\"errno\":0,\"data\":{\"list\":[" +
                "{\"id\":8},{\"id\":9},{\"id\":10},{\"id\":11}," +
                "{\"id\":12},{\"id\":13},{\"id\":14},{\"id\":15}," +
                "{\"id\":23},{\"id\":24}]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }




    /**
     * 12.平台管理员获取秒杀时间段列表--page=3&pageSize=5--成功
     **/
    @Test
    @Order(12)
    public void selectFsTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/flashsale/timesegments?page=3&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        log.info(new String(responseString,"UTF-8"));

        String expectedResponse = "{\"errno\":0,\"data\":{\"list\":[" +
                "{\"id\":25},{\"id\":26},{\"id\":27}]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 13.平台管理员新增秒杀时间段--成功
     */
    @Test
    @Order(13)
    public void insertFsTimeTest1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 01:00:00");
        body.put("endTime", "2020-12-15 01:01:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments")
                .header("authorization",token)
                .bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString,"UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":29}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


    }


    /**
     * 14.平台管理员新增秒杀时间段-开始时间为空
     */
    @Test
    @Order(14)
    public void insertFsTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "");
        body.put("endTime", "2020-12-15 00:01:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 15.平台管理员新增秒杀时间段-结束时间为空
     */
    @Test
    @Order(15)
    public void insertFsTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 00:00:00");
        body.put("endTime", "");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }


    /**
     * 16.平台管理员新增秒杀时间段-开始时间大于结束时间
     */
    @Test
    @Order(16)
    public void insertFsTimeTest4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("beginTime", "2020-12-15 01:30:00");
        body.put("endTime", "2020-12-15 00:30:00");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/0/flashsale/timesegments").header("authorization", token).bodyValue(requireJson).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();

    }



    /**
     * 19.平台管理员删除秒杀时间段--时间段id不存在
     */
    @Test
    @Order(19)
    public void deleteFsTimeTest2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/flashsale/timesegments/40").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();


    }


    /**
     * 20.平台管理员删除秒杀时间段--给定的时段id不属于秒杀时段
     */
    @Test
    @Order(20)
    public void deleteFsTimeTest3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/flashsale/timesegments/1").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 删除理由：会与秒杀的测试冲突
     */

//    /**
//     * 8.平台管理员删除广告时间段--成功
//     */
//    @Test
//    @Order(8)
//    public void deleteAdTimeTest1() throws Exception {
//        String token = this.adminLogin("13088admin", "123456");
//
//        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/timesegments/5").header("authorization",token).exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//
//        String expectedResponse = "{\n" +
//                "  \"errno\": 0\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//
//        //重新查询进行验证
//        byte[] responseString2 = manageClient.get().uri("/shops/0/advertisement/timesegments?page=1&pageSize=5").header("authorization",token).exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
//                .returnResult()
//                .getResponseBodyContent();
//
//        String expectedResponse2 = "{\n" +
//                "  \"errno\": 0,\n" +
//                "  \"data\": {\n" +
//                "    \"pageSize\": 5,\n" +
//                "    \"page\": 1,\n" +
//                "    \"list\": [\n" +
//                "      {\n" +
//                "        \"id\": 1,\n" +
//                "        \"beginTime\": \"2021-01-01T00:00:00\",\n" +
//                "        \"endTime\": \"2021-01-01T01:00:00\",\n" +
//                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
//                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": 2,\n" +
//                "        \"beginTime\": \"2021-01-02T01:00:00\",\n" +
//                "        \"endTime\": \"2021-01-02T02:00:00\",\n" +
//                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
//                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": 3,\n" +
//                "        \"beginTime\": \"2021-01-03T02:00:00\",\n" +
//                "        \"endTime\": \"2021-01-03T03:00:00\",\n" +
//                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
//                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": 4,\n" +
//                "        \"beginTime\": \"2021-01-04T03:00:00\",\n" +
//                "        \"endTime\": \"2021-01-04T04:00:00\",\n" +
//                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
//                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": 6,\n" +
//                "        \"beginTime\": \"2021-01-06T05:00:00\",\n" +
//                "        \"endTime\": \"2021-01-06T06:00:00\",\n" +
//                "        \"gmtCreate\": \"2020-11-28T21:01:01\",\n" +
//                "        \"gmtModified\": \"2020-11-28T21:01:01\"\n" +
//                "      }\n" +
//                "    ]\n" +
//                "  }\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);
//
//    }

}
