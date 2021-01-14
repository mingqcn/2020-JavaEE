package cn.edu.xmu.ooad.util;

import cn.edu.xmu.ooad.model.VoObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletResponse;
import java.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 通用工具类
 * @author Ming Qiu
 **/
public class Common {


    /** The FieldPosition. */
    private static final FieldPosition HELPER_POSITION = new FieldPosition(0);

    private static Logger logger = LoggerFactory.getLogger(Common.class);

    /**
     * 生成八位数序号
     * @return 序号
     */
    public static String genSeqNum(){
        int  maxNum = 36;
        int i;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssS");
        LocalDateTime localDateTime = LocalDateTime.now();
        String strDate = localDateTime.format(dtf);
        StringBuffer sb = new StringBuffer(strDate);

        int count = 0;
        char[] str = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        Random r = new Random();
        while(count < 2){
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                sb.append(str[i]);
                count ++;
            }
        }
        return sb.toString();
    }

    /**
     * 处理BindingResult的错误
     * @param bindingResult
     * @return
     */
    public static Object processFieldErrors(BindingResult bindingResult, HttpServletResponse response) {
        Object retObj = null;
        if (bindingResult.hasErrors()){
            StringBuffer msg = new StringBuffer();
            //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
            for (FieldError error : bindingResult.getFieldErrors()) {
                msg.append(error.getDefaultMessage());
                msg.append(";");
            }
            logger.debug("processFieldErrors: msg = "+ msg.toString());
            retObj = ResponseUtil.fail(ResponseCode.FIELD_NOTVALID, msg.toString());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return retObj;
    }

    /**
     * 处理返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getRetObject(ReturnObject<VoObject> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:
                VoObject data = returnObject.getData();
                if (data != null){
                    Object voObj = data.createVo();
                    return ResponseUtil.ok(voObj);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 处理返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getListRetObject(ReturnObject<List> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:
                List objs = returnObject.getData();
                if (objs != null){
                    List<Object> ret = new ArrayList<>(objs.size());
                    for (Object data : objs) {
                        if (data instanceof VoObject) {
                            ret.add(((VoObject)data).createVo());
                        }
                    }
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }


    /**
     * 处理分页返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getPageRetObject(ReturnObject<PageInfo<VoObject>> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:

                PageInfo<VoObject> objs = returnObject.getData();
                if (objs != null){
                    List<Object> voObjs = new ArrayList<>(objs.getList().size());
                    for (Object data : objs.getList()) {
                        if (data instanceof VoObject) {
                            voObjs.add(((VoObject)data).createVo());
                        }
                    }

                    Map<String, Object> ret = new HashMap<>();
                    ret.put("list", voObjs);
                    ret.put("total", objs.getTotal());
                    ret.put("page", objs.getPageNum());
                    ret.put("pageSize", objs.getPageSize());
                    ret.put("pages", objs.getPages());
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }




    public static Object getNullRetObj(ReturnObject<Object> returnObject, HttpServletResponse httpServletResponse) {
        ResponseCode code = returnObject.getCode();
        switch (code) {
            case RESOURCE_ID_NOTEXIST:
                httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                return ResponseUtil.fail(returnObject.getCode());
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 根据 errCode 修饰 API 返回对象的 HTTP Status
     * @param returnObject 原返回 Object
     * @return 修饰后的返回 Object
     */
    public static Object decorateReturnObject(ReturnObject returnObject) {
        switch (returnObject.getCode()) {
            case RESOURCE_ID_NOTEXIST:
                // 404：资源不存在
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.NOT_FOUND);
            case INTERNAL_SERVER_ERR:
                // 500：数据库或其他严重错误
                return new ResponseEntity(
                        ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            case OK:
                // 200: 无错误
                Object data = returnObject.getData();
                if (data != null){
                    return ResponseUtil.ok(data);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 动态拼接字符串
     * @param sep 分隔符
     * @param fields 拼接的字符串
     * @return StringBuilder
     * createdBy: Ming Qiu 2020-11-02 11:44
     */
    public static StringBuilder concatString(String sep, String... fields){
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i< fields.length; i++){
            if (i > 0){
                ret.append(sep);
            }
            ret.append(fields[i]);
        }
        return ret;
    }

    /**
     * 增加20%以内的随机时间
     * 如果timeout <0 则会返回60s+随机时间
     * @param timeout 时间
     * @return 增加后的随机时间
     */
    public static long addRandomTime(long timeout) {
        if (timeout <= 0) {
            timeout = 60;
        }
        //增加随机数，防止雪崩
        timeout += (long) new Random().nextDouble() * (timeout / 5 - 1);
        return timeout;
    }

}
