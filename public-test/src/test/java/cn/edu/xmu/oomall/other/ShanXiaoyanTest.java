package cn.edu.xmu.oomall.other;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;


/**
 * 请先运行otherdata.sql 测试完需要还原数据库中的region和address
 **/

@Slf4j
@SpringBootTest(classes = PublicTestApp.class)
public class ShanXiaoyanTest {

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
     * 买家登录，获取token
     *
     * @param userName
     * @param password
     * @return token
     */
    private String userLogin(String userName, String password) throws Exception{

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

        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }


    /**
     * 管理员登录，获取token
     *
     * @param userName
     * @param password
     * @return token
     */
    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");

    }


    /**
     * addAddress1
     * 新增地址，参数错误，手机号码为空
     * @throws Exception
     */
    @Test
    public void addAddress0() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses", 1)
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
     * addAddress1
     * 新增地址，参数错误，手机号码错误
     * @throws Exception
     */
    @Test
    public void addAddress1() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"1232323\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses", 1)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**addAddress2
     * 新增地址，参数错误，收件人为空
     * @throws Exception
     */
    @Test
    public void addAddress2() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"\",\n" +
                " \"mobile\":  \"18990897878\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
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
     * addAddress3
     * 新增地址，参数错误，详情为空
     * @throws Exception
     */

    @Test
    public void addAddress3() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": 1,\n" +
                " \"detail\":  \"\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
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
     * addAddress4
     * 新增地址，参数错误，地区id为空
     * @throws Exception
     */


    @Test
    public void addAddress4() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String requireJson = "{\n" +
                " \"regionId\": null,\n" +
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\" \n" +
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
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
     * addAddress5
     * 新增地址，地区已废弃
     * 先废弃地区再测试添加地址
     * @throws Exception
     */

    @Test
    public void addAddress5() throws Exception{


        String token1 = this.adminLogin("13088admin", "123456");
        String token2 = this.userLogin("8606245097", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/0/regions/302")
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


        String requireJson="{\n"+
                " \"regionId\": 302,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token2)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * addAddress6
     * 新增地址，地区不存在
     * @throws Exception
     */
    @Test
    public void addAddress6() throws Exception{

        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": -1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
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
     * addAddress7
     * 新增地址
     * @throws Exception
     */
    @Test
    public void addAddress7() throws Exception{

        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": 1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.post().uri("/addresses")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse= "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"regionId\": 1,\n" +
                "        \"detail\": \"测试地址1\",\n" +
                "        \"consignee\": \"测试\",\n" +
                "        \"mobile\": \"18990897878\",\n" +
                "        \"beDefault\": false\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


    }
    /**
     * 修改地址信息，地址id不是自己的
     */

    @Test
    public void updateAddress1() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": 1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/3")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 修改地址信息，地区不存在
     */

    @Test
    public void updateAddress2() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        String requireJson="{\n"+
                " \"regionId\": -1,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/1")
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
     * 修改地址信息，地区已废弃
     */

    @Test
    public void updateAddress3() throws Exception{


        String token1 = this.adminLogin("13088admin", "123456");
        String token2 = this.userLogin("8606245097", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/0/regions/305")
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String requireJson="{\n"+
                " \"regionId\": 305,\n"+
                " \"detail\":  \"测试地址1\",\n" +
                " \"consignee\":  \"测试\",\n" +
                " \"mobile\":  \"18990897878\"\n"+
                "}";

        byte[] responseString = mallClient.put().uri("/addresses/1")
                .header("authorization", token2)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * addRegion1
     * 新增地区 父地区不存在
     */
    @Test
    public void addRegion1() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.post().uri("/shops/0/regions/-1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**addRegion2
     * 新增地区 父地区已废弃  先废弃id为300的地区，再测试新增地区
     *
     */
    @Test
    public void addRegion2() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/regions/104")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString2 = manageClient.post().uri("/shops/0/regions/104/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addRegion3
     * 增地区
     */
    @Test
    public void addRegion3() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"test\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.post().uri("/shops/0/regions/1/subregions")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * addRegion4
     * 新增地区，地区名字为空
     */
    @Test
    public void addRegion4() throws Exception{


        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.post().uri("/shops/0/regions/1/subregions")
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
     * disableRegion
     * 废弃地区 地区id不存在
     */

    @Test
    public void disableRegion1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/regions/-1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * disableRegion
     * 废弃地区 301
     */

    @Test
    public void disableRegion2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/regions/301")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        //用新增子地区来测试是否将地区成功修改为无效 和addRegion2同理

        String requireJson2="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString2 = manageClient.post().uri("/shops/0/regions/301/subregions")
                .header("authorization", token)
                .bodyValue(requireJson2)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();


    }
    
    



    /**
     * setAsDefault1
     * @throws Exception
     */
    @Test
    public void setAsDefault1() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/1/default").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult().getResponseBodyContent();
    }

    /**
     * 地址id不存在
     * setAsDefault2
     * @throws Exception
     */
    @Test
    public void setAsDefault2() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/-1/default").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBodyContent();

    }

    /**
     * 地址id不是自己的地址
     * setAsDefault3
     * @throws Exception
     */
    @Test
    public void setAsDefault3() throws Exception {
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.put().uri("/addresses/2/default").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult().getResponseBodyContent();

    }


    /**
     * updateRegion1
     * 修改地区，地区不存在
     */
    @Test
    public void updateRegion1() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString = manageClient.put().uri("/shops/0/regions/-1")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**updateRegion2
     * 修改地区，地区已废弃
     *
     */
    @Test
    public void updateRegion2() throws Exception{

        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString1 = manageClient.delete().uri("/shops/0/regions/303")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();


        String requireJson="{\n"+
                " \"name\": \"fujian\",\n" +
                " \"postalCode\":  \"100100\"\n"+
                "}";

        byte[] responseString2 = manageClient.put().uri("/shops/0/regions/303")
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    

    /**
     * getRegion1
     * 查询地区，地区不存在
     */
    @Test
    public void getRegion1() throws Exception{

        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.get().uri("/region/-1/ancestor")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();



    }
    /**
     * getRegion2
     * 查询地区，地区已废弃
     *
     */
    @Test
    public void getRegion2() throws Exception{

        String token1 = this.adminLogin("13088admin", "123456");
        String token2 = this.userLogin("8606245097", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/0/regions/304")
                .header("authorization", token1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();



        byte[] responseString2 = mallClient.get().uri("/region/304/ancestor")
                .header("authorization", token2)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_OBSOLETE.getCode())
                .returnResult()
                .getResponseBodyContent();



    }



}
