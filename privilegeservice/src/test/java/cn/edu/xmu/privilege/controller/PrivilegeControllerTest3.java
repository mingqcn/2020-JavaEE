package cn.edu.xmu.privilege.controller;


import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 *
 * @author 3218
 * @date Created in 2020/11/7 18:49
 **/

@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrivilegeControllerTest3 {

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeControllerTest3.class);
    /*
     * 上传成功
     */
    @Test
    public void uploadFileSutccess() throws Exception{
        String token = creatTestToken(1L,0L,100);
        File file = new File("."+File.separator + "src" + File.separator + "test" + File.separator+"resources" + File.separator + "img" + File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/privilege/adminusers/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /*
     * 上传失败（id不存在）
     */
    @Test
    public void UploadFileFail1() throws Exception{
        String token = creatTestToken(1111L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/privilege/adminusers/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /*
     * 上传失败（文件格式错误）
     */
    @Test
    public void UploadFileFail2() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"文本文件.txt");
        MockMultipartFile firstFile = new MockMultipartFile("img", "文本文件.txt" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/privilege/adminusers/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /*
     * 上传失败（文件格式错误，伪装成图片）
     */
    @Test
    public void UploadFileFail3() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"伪装的图片.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "伪装的图片.png" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/privilege/adminusers/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":508,\"errmsg\":\"图片格式不正确\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /*
     * 上传失败（文件格式错误）
     */
    @Test
    public void UploadFileFail4() throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        File file = new File("."+File.separator + "src" + File.separator +  "test" + File.separator + "resources" + File.separator + "img" +File.separator+"大小超限图片.jpg");
        MockMultipartFile firstFile = new MockMultipartFile("img", "大小超限图片.jpg" , "multipart/form-data", new FileInputStream(file));
        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/privilege/adminusers/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":509,\"errmsg\":\"图片大小超限\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 创建测试用token
     *
     * @author 3218
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 3218 2020/11/09 16:54
     * modifiedBy 3218 2020/11/09 16:54
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug("token: " + token);
        return token;
    }
}
