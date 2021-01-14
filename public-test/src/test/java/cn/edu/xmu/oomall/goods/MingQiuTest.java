package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MingQiuTest {

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


    //卖家登录
    private String userLogin(String userName, String password){
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(responseString), "data");
    }

    private String adminLogin(String userName, String password){
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
        return JacksonUtil.parseString(new String(ret), "data");
        //endregion
    }

    @Test
    @Order(1)
    public void goodsTest1(){
        String token = this.userLogin("17857289610", "123456");
        byte[] responseString = mallClient.post().uri("/couponactivities/12158/usercoupons")
                .header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        List<String> couponSn = JacksonUtil.parseSubnodeToStringList(new String(responseString), "/data");
        String couponSn12158 = couponSn.get(0);

        responseString = mallClient.post().uri("/couponactivities/12159/usercoupons")
                .header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        couponSn = JacksonUtil.parseSubnodeToStringList(new String(responseString), "/data");
        String couponSn12159_1 = couponSn.get(0);
        String couponSn12159_2 = couponSn.get(1);

        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){

        }

        mallClient.get().uri("/coupons?state=1")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.couponSn == "+couponSn12158 +" )]").exists()
                .jsonPath("$.data.list[?(@.couponSn == "+couponSn12159_1 +" )]").exists()
                .jsonPath("$.data.list[?(@.couponSn == "+couponSn12159_2 +" )]").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(2)
    public void goodsTest2() {
        String token = this.adminLogin("13088admin", "123456");

        manageClient.get().uri("/shops/1/couponactivities/12158")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(12158)
                .jsonPath("$.data.quantity").isEqualTo(99)
                .returnResult()
                .getResponseBodyContent();

        manageClient.get().uri("/shops/1/couponactivities/12159")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(12159)
                .jsonPath("$.data.quantity").isEqualTo(2)
                .returnResult()
                .getResponseBodyContent();

    }

}
