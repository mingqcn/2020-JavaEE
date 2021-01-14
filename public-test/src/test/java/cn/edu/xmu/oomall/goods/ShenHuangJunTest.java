package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.UnsupportedEncodingException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

// 两条未通过，一条建议删除

@SpringBootTest(classes = PublicTestApp.class)
public class ShenHuangJunTest {
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

    private String login(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

    /**
     * 买家新增SKU的评论(正常)
     * 使用的网关错误，建议删除。测试用例-1
     * 增加评论应该使用用户登录。orderitem 1对应的是user 1而不是130880admin，且其他人的测试用例已经测试过该功能，因此删除。
     * @author  24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void addComment1() throws Exception{
//        String token = this.login("13088admin", "123456");
//        String requestJson="{\"type\":0 ,\"content\":\"这个真不错\"}";
//        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/orderitems/1/comments")
//                .header("authorization", token)
//                .bodyValue(requestJson);
//
//        byte[] responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.data.id").isNumber()
//                .jsonPath("$.data.customer.id").isNotEmpty()
//                .jsonPath("$.data.customer.userName").isEqualTo("13088admin")
//                .jsonPath("$.data.customer.name").isNotEmpty()
//                .jsonPath("$.data.goodsSkuId").isNumber()
//                .jsonPath("$.data.type").isEqualTo(0)
//                .jsonPath("$.data.content").isEqualTo("这个真不错")
//                .jsonPath("$.data.state").isEqualTo(0)
//                .jsonPath("$.data.gmtCreate").isNotEmpty()
//                .returnResult()
//                .getResponseBodyContent();
    }

    /**
     *管理员审核评论通过（管理员已经登录）
     * @author 24320182203260 Sheng Huangjun
     *   @date 2020/12/16 12:45
     */
    @Test
    public void allowComment1() throws Exception{
        String token = this.login("13088admin", "123456");
        String requestJson="{\"conclusion\":true}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/0/comments/1/confirm")
                .header("authorization", token)
                .bodyValue(requestJson);

        byte[] responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     *管理员审核评论不通过（管理员已经登录）
     * 不存在这样的评论，ID不正确，已经修改，副作用未知
     * @author 24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void allowComment2() throws Exception{
        String token = this.login("13088admin", "123456");
        String requestJson="{\"conclusion\":false}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/0/comments/7/confirm")
                .header("authorization", token)
                .bodyValue(requestJson);

        byte[] responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     *管理员企图审核id不存在的评论（管理员已经登录）
     * @author 24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void allowComment3() throws Exception{
        String token = this.login("13088admin", "123456");
        String requestJson="{\"conclusion\":true}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/0/comments/1000000000/confirm")
                .header("authorization", token)
                .bodyValue(requestJson);

        byte[] responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     *管理员审核评论（管理员未登录）
     * @author 24320182203260 Sheng Huangjun
     *@date 2020/12/16 12:45
     */
    @Test
    public void allowComment4() throws Exception{
        byte[] responseString = manageClient.put().uri("/shops/0/comments/1/confirm")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 查看SKU评价列表(第一页)
     * @author 24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void getAllCommnetOfSku1() throws Exception{
        byte[] responseBuffer=mallClient.get().uri("/skus/273/comments?page=1&pageSize=10").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }

    /**
     * 查看SKU评价列表(第二页)
     * @author 24320182203260 Sheng Huangjun
     * @date 2020/12/16 12:45
     */
    @Test
    public void getAllCommnetOfSku2() throws Exception{
        byte[] responseBuffer = null;
        responseBuffer=mallClient.get().uri("/skus/273/comments?page=2&pageSize=10").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }

    /**
     * 查看不存在的SKU评价列表(第一页)
     * @author 24320182203260 Sheng Huangjun
     * @date 2020/12/16 12:45
     */
    @Test
    public void getAllCommnetOfSku3() throws Exception{
        byte[] responseBuffer = null;
        responseBuffer=mallClient.get().uri("/skus/1000000000000000000/comments?page=1&pageSize=10").exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult().getResponseBody();

    }

    /**
     * 买家查看自己的评价记录，包括评论状态(第一页)
     * 这里使用的是管理员登陆，无法通过用户网关，如果不更改用户token，应该删除
     * @author 24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void getAllCommnetOfUser1() throws Exception{
//        String token = this.login("8606245097", "123456");
//        byte[] responseBuffer=mallClient.get().uri("/comments?page=1&pageSize=10")
//                .header("authorization", token).exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBody();
    }

    /**
     * 买家查看自己的评价记录，包括评论状态(第二页)
     * 这里使用的是管理员登陆，无法通过用户网关，如果不更改用户token，应该删除
     * @author 24320182203260 Sheng Huangjun
     * @date 2020/12/16 12:45
     */
    @Test
    public void getAllCommnetOfUser2() throws Exception{
//        String token = this.login("8606245097", "123456");
//        byte[] responseBuffer=mallClient.get().uri("/comments?page=2&pageSize=10")
//                .header("authorization", token).exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .returnResult().getResponseBody();

    }

    /**
     * 买家未登录查看自己的评价
     * @author 24320182203260 Sheng Huangjun
     * @date 2020/12/16 12:45
     */
    @Test
    public void getAllCommnetOfUser3() throws Exception{
        byte[] responseString = mallClient.get().uri("/comments?page=1&pageSize=10")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 买家企图访问不存在的商品下自己的评论
     * 沈黄隽看错了，删除这个测试，测试-1 By 宋润涵
     * @author 24320182203260 Shen Huangjun
     * @date 2020/12/16 19:40
     */
    @Test
    public void getAllCommnetOfUser4() throws Exception{
//        String token = this.login("13088admin", "123456");
//        byte[] responseBuffer=mallClient.get().uri("/skus/100000000000000/comments?page=1&pageSize=10")
//                .header("authorization", token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
//                .returnResult()
//                .getResponseBodyContent();

    }


    /**
     * 管理员查看未核审评论列表(第一页,管理员已登录)
     * @author 24320182203260 Sheng Huangjun
     * @date 2020/12/16 12:45
     */
    @Test
    public void getAllComment1() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseBuffer=manageClient.get().uri("/shops/0/comments/all?state=0&page=1&pageSize=10")
                .header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }

    /**
     * 管理员查看未核审评论列表(第二页,管理员已登录)
     * @author 24320182203260 Sheng Huangjun
     * @date 2020/12/16 12:45
     */
    @Test
    public void getAllComment2() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseBuffer=manageClient.get().uri("/shops/0/comments/all?state=0&page=2&pageSize=10")
                .header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }

    /**
     * 管理员查看审核通过的评论列表(第一页,管理员已登录)
     * @author 24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void getAllComment5() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseBuffer=manageClient.get().uri("/shops/0/comments/all?state=1&page=1&pageSize=10")
                .header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }

    /**
     * 管理员查看审核通过的评论列表(第二页,管理员已登录)
     * @author 24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void getAllComment6() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseBuffer=manageClient.get().uri("/shops/0/comments/all?state=1&page=2&pageSize=10")
                .header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }


    /**
     * 管理员查看审核不通过的评论列表(第一页,管理员已登录)
     * @author 24320182203260 Sheng Huangjun
     *  @date 2020/12/16 12:45
     */
    @Test
    public void getAllComment7() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseBuffer=manageClient.get().uri("/shops/0/comments/all?state=2&page=1&pageSize=10")
                .header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }

    /**
     * 管理员查看审核不通过的评论列表(第二页,管理员已登录)
     * @author 24320182203260 Sheng Huangjun
     * @date 2020/12/16 12:45
     */
    @Test
    public void getAllComment8() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseBuffer=manageClient.get().uri("/shops/0/comments/all?state=2&page=2&pageSize=10")
                .header("authorization", token).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBody();

    }

    /**
     * 管理员未登录查看评论列表
     * @author 24320182203260 Sheng Huangjun
     *@date 2020/12/16 12:45
     */
    @Test
    public void getAllComment9() throws Exception{
        byte[] responseString = manageClient.get().uri("/shops/0/comments/all?state=0&page=1&pageSize=10")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }

}
