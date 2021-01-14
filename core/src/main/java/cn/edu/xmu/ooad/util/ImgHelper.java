package cn.edu.xmu.ooad.util;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.util.UUID;

public class ImgHelper {

    private static Logger logger = LoggerFactory.getLogger(Common.class);

    /**
     * 保存单个图片并限制大小在远程服务器，直接以multipartFile形式
     *
     * @param multipartFile
     * @param size         文件最大大小
     * @param username     dav服务器用户名
     * @param password     dav服务器密码
     * @param baseUrl      dav地址及其目录
     * @author 3218
     * @return
     */
    public static ReturnObject remoteSaveImg(MultipartFile multipartFile,
            int size, String username,String password,String baseUrl) throws IOException {

        //判断是否是图片
        if(!isImg(multipartFile))
            return new ReturnObject(ResponseCode.IMG_FORMAT_ERROR);

        //判断文件大小是否符合要求
        if(multipartFile.getSize()>size*1024*1024){
            return new ReturnObject(ResponseCode.IMG_SIZE_EXCEED);
        }

        Sardine sardine = SardineFactory.begin(username,password);

        //没有权限创建则抛出IOException
        if(!sardine.exists(baseUrl)){
            sardine.createDirectory(baseUrl);
        }

        InputStream fileInputStream = multipartFile.getInputStream();

        String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        sardine.put(baseUrl+fileName,fileInputStream);

        sardine.shutdown();
        return new ReturnObject(fileName);
    }


    /**
     * 删除远程服务器文件
     *
     * @param filename 文件名
     * @param username     dav服务器用户名
     * @param password     dav服务器密码
     * @param baseUrl      dav地址及其目录
     */
    public static void deleteRemoteImg(String filename, String username,
                                       String password,String baseUrl){
        Sardine sardine = SardineFactory.begin(username,password);
        try{
            sardine.delete(baseUrl+filename);
            sardine.shutdown();
        }
        catch(IOException e){
            logger.error("delete fail: "+ filename);
        }
        return;
    }

    /**
     * 判断文件是否是图片
     *
     * @param multipartFile 文件
     *
     */
    public static boolean isImg(MultipartFile multipartFile) throws IOException{
        BufferedImage bi = ImageIO.read(multipartFile.getInputStream());
        if(bi == null){
            return false;
        }
        return true;
    }

}
