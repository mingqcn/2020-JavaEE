package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 其他模块测试 收藏与地址
 * @author ：Zeyao Feng 21620172203301
 * @date ：Created in 2020/12/15 15:31
 *  modified in 2020/12/17 1:52
 */
@SpringBootTest(classes = Application.class)
public class FengZeyaoTest {
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

    private String userLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/privileges/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }


    /**
     * 1. 买家查看收藏，分页信息错误 由pageHelper处理错误，无需额外检测
     * @author: Zeyao Feng
     * @date: Created in 2020/12/15 15:41
     * modified in 2020/12/17 1:58
     */
    @Test
    @Order(1)
    public void getFavorites1() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=-1&pageSize=-1").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": []\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }



    /**
     * 2. 买家查看收藏，不输入分页信息（采用默认分页page=1，pageSize=10）
     * @author: Zeyao Feng
     * @date: Created in 2020/12/15 15:41
     */
    @Test
    @Order(2)
    public void getFavorites2() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/favorites").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 15,\n" +
                "    \"pages\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3735464,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58622084a52a4.jpg\",\n" +
                "          \"originalPrice\": 250000,\n" +
                "          \"inventory\": 1,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 250000,\n" +
                "          \"id\": 301\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3768231,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3b1bd57832c.png\",\n" +
                "          \"originalPrice\": 8800,\n" +
                "          \"inventory\": 1000,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 8800,\n" +
                "          \"id\": 609\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3800998,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_59ff1c1a717c4.png\",\n" +
                "          \"originalPrice\": 269,\n" +
                "          \"inventory\": 1214,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 269,\n" +
                "          \"id\": 526\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833754,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "          \"originalPrice\": 980000,\n" +
                "          \"inventory\": 1,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 980000,\n" +
                "          \"id\": 273\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833755,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\n" +
                "          \"originalPrice\": 850,\n" +
                "          \"inventory\": 99,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 850,\n" +
                "          \"id\": 274\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833756,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\n" +
                "          \"originalPrice\": 4028,\n" +
                "          \"inventory\": 10,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 4028,\n" +
                "          \"id\": 275\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833757,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\n" +
                "          \"originalPrice\": 6225,\n" +
                "          \"inventory\": 10,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 6225,\n" +
                "          \"id\": 276\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833758,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\n" +
                "          \"originalPrice\": 16200,\n" +
                "          \"inventory\": 10,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 16200,\n" +
                "          \"id\": 277\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833759,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\n" +
                "          \"originalPrice\": 1199,\n" +
                "          \"inventory\": 46100,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 1199,\n" +
                "          \"id\": 278\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3833760,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\n" +
                "          \"originalPrice\": 1199,\n" +
                "          \"inventory\": 500,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 1199,\n" +
                "          \"id\": 279\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 3. 买家查看收藏，成功返回三条数据
     * @author: Zeyao Feng
     * @date: Created in 2020/12/15 16:46
     * modified in 2020/12/17 2:03
     */
    @Test
    @Order(3)
    public void getFavorites3() throws Exception {

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 3,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3735465,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_597080ddb672a.jpg\",\n" +
                "          \"originalPrice\": 3000,\n" +
                "          \"inventory\": 1,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 3000,\n" +
                "          \"id\": 470\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3768232,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201903/file_5c789d915ae53.jpg\",\n" +
                "          \"originalPrice\": 1299,\n" +
                "          \"inventory\": 30,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 1299,\n" +
                "          \"id\": 670\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3800999,\n" +
                "        \"goodsSku\": {\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201811/file_5bf12117c0815.jpg\",\n" +
                "          \"originalPrice\": 1199,\n" +
                "          \"inventory\": 19987,\n" +
                "          \"disable\": false,\n" +
                "          \"price\": 1199,\n" +
                "          \"id\": 661\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 4. 买家重复收藏，返回他当前收藏的商品
     * @author: Zeyao Feng
     * @date: Created in 2020/12/15 16:50
     * modified in 2020/12/17 2:05
     */
    @Test
    @Order(4)
    public void createFavorites1() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/273").header("authorization",token).exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"id\": 3833754,\n" +
                "    \"goodsSku\": {\n" +
                "      \"name\": \"+\",\n" +
                "      \"skuSn\": null,\n" +
                "      \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\n" +
                "      \"originalPrice\": 980000,\n" +
                "      \"inventory\": 1,\n" +
                "      \"disable\": false,\n" +
                "      \"price\": 980000,\n" +
                "      \"id\": 273\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 5. 买家收藏的商品不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/15 16:52
     */
    @Test
    @Order(5)
    public void createFavorites2() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/12345678").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 6. 新增地址，买家地址已经达到上限
     * @author: Zeyao Feng
     * @date: Created in 2020/12/15 17:08
     * modified in 2020/12/17 2:08
     */
    @Test
    @Order(6)
    public void addAddress1() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": 2\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADDRESS_OUTLIMIT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }



    /**
     * 7. 修改地址 地址Id不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 3:55
     * modified in 2020/12/17 2:12
     */
    @Test
    @Order(7)
    public void updateAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": 1\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 8. 修改地址 手机号格式错误
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:02
     */
    @Test
    @Order(8)
    public void updateAddress2() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"123456\",\n" +
                "  \"regionId\": 1\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 9. 修改地址 地区id不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:06
     */
    @Test
    @Order(9)
    public void updateAddress3() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        String requireJson="{\n" +
                "  \"consignee\": \"test\",\n" +
                "  \"detail\": \"test\",\n" +
                "  \"mobile\": \"12345678910\",\n" +
                "  \"regionId\": -1\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/20000")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 10. 删除地址，地址Id不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:12
     */
    @Test
    @Order(10)
    public void deleteAddress1() throws Exception{

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.delete().uri("/addresses/10000").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 11. 查询某个地区的所有上级地区，该地区为顶级地区（中国）,pid=0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:20
     * modified in 2020/12/17 2:13
     */
    @Test
    @Order(11)
    public void selectAncestorRegion1() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/1/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": []\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 12. 查询某个地区的所有上级地区，该地区为1级地区（例如福建省）,pid>0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:24
     * modified in 2020/12/17 2:17
     */
    @Test
    @Order(12)
    public void selectAncestorRegion2() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/14/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"中国\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 13. 查询某个地区的所有上级地区，该地区为2级地区（例如厦门市）,pid>0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:27
     * modified in 2020/12/17 2:17
     */
    @Test
    @Order(13)
    public void selectAncestorRegion3() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/151/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 14,\n" +
                "      \"pid\": 1,\n" +
                "      \"name\": \"福建省\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"中国\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }



    /**
     * 14. 查询某个地区的所有上级地区，该地区不存在
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:32
     */
    @Test
    @Order(14)
    public void selectAncestorRegion4() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/140700/ancestor").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }



    /**
     * 15. 删除地址，成功，28号买家仅有一条地址,删除后无地址
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:18
     * modified in 2020/12/17 2:23
     */
    @Test
    @Order(15)
    public void deleteAddress2() throws Exception{

        //uid=28
        String token = this.userLogin("20137712098", "123456");

        byte[] responseString = mallClient.delete().uri("/addresses/77583").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        //再次查询该买家的地址
        byte[] responseString2 = mallClient.get().uri("/addresses").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 0,\n" +
                "    \"pages\": 0,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": []\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString2, StandardCharsets.UTF_8), false);
    }


    /**
     * 16. 买家查询地址 第一页
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:54
     */
    @Test
    @Order(16)
    public void selectAddress1() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/addresses?page=1&pageSize=7").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 7,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 77563,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77564,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77565,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77566,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77567,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77568,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77569,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 17. 买家查询地址，第二页
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:56
     */
    @Test
    @Order(17)
    public void selectAddress2() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/addresses?page=2&pageSize=3").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 7,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 77566,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77567,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77568,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 18. 买家查询地址 第三页显示 未完全显示
     * @author: Zeyao Feng
     * @date: Created in 2020/12/16 4:59
     */
    @Test
    @Order(18)
    public void selectAddress3() throws Exception{

        //uid=27
        String token = this.userLogin("89972149478", "123456");

        byte[] responseString = mallClient.get().uri("/addresses?page=4&pageSize=6").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 20,\n" +
                "    \"pages\": 4,\n" +
                "    \"pageSize\": 6,\n" +
                "    \"page\": 4,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 77581,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 77582,\n" +
                "        \"regionId\": 2,\n" +
                "        \"detail\": \"test\",\n" +
                "        \"consignee\": \"test\",\n" +
                "        \"mobile\": \"12345678910\",\n" +
                "        \"beDefault\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 19. 查询某个地区的所有上级地区，该地区为3级地区（例如思明区）,pid>0
     * @author: Zeyao Feng
     * @date: Created in 2020/12/17 2:20
     */
    @Test
    @Order(19)
    public void selectAncestorRegion5() throws Exception {

        //uid=26
        String token = this.userLogin("9925906183", "123456");

        byte[] responseString = mallClient.get().uri("/region/1599/ancestor").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 151,\n" +
                "      \"pid\": 14,\n" +
                "      \"name\": \"厦门市\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 14,\n" +
                "      \"pid\": 1,\n" +
                "      \"name\": \"福建省\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"中国\",\n" +
                "      \"postalCode\": null,\n" +
                "      \"state\": 0\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }









}
