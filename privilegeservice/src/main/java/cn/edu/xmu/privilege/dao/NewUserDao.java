package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.bloom.BloomFilterHelper;
import cn.edu.xmu.ooad.util.bloom.RedisBloomFilter;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.privilege.mapper.NewUserPoMapper;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.model.bo.User;
import cn.edu.xmu.privilege.model.po.NewUserPo;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.po.UserPoExample;
import cn.edu.xmu.privilege.model.vo.NewUserVo;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
/**
 * 新用户Dao
 * @author LiangJi@3229
 * @date 2020/11/10 18:41
 */
@Repository
public class NewUserDao implements InitializingBean {
    private  static  final Logger logger = LoggerFactory.getLogger(NewUserDao.class);
    @Autowired
    NewUserPoMapper newUserPoMapper;
    @Autowired
    UserPoMapper userPoMapper;
    @Autowired
    RedisTemplate redisTemplate;

    RedisBloomFilter bloomFilter;

    String[] fieldName;
    final String suffixName="BloomFilter";

    /**
     * 通过该参数选择是否清空布隆过滤器
     */
    private boolean reinitialize=true;


    /**
     * 初始化布隆过滤器
     * @throws Exception
     * createdBy: LiangJi3229 2020-11-10 18:41
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        BloomFilterHelper bloomFilterHelper=new BloomFilterHelper<>(Funnels.stringFunnel(Charsets.UTF_8),1000,0.02);
        fieldName=new String[]{"email","mobile","userName"};
        bloomFilter=new RedisBloomFilter(redisTemplate,bloomFilterHelper);
        if(reinitialize){
            for(int i=0;i<fieldName.length;i++){
                redisTemplate.delete(fieldName[i]+suffixName);
            }
        }

    }

    /**
     *
     * @param po
     * @return ReturnObject 错误返回对象
     * createdBy: LiangJi3229 2020-11-10 18:41
     */
    public ReturnObject checkBloomFilter(NewUserPo po){
        if(bloomFilter.includeByBloomFilter("email"+suffixName,po.getEmail())){
            return new ReturnObject(ResponseCode.EMAIL_REGISTERED);
        }
        if(bloomFilter.includeByBloomFilter("mobile"+suffixName,po.getMobile())){
            return new ReturnObject(ResponseCode.MOBILE_REGISTERED);
        }
        if(bloomFilter.includeByBloomFilter("userName"+suffixName,po.getUserName())){
            return new ReturnObject(ResponseCode.USER_NAME_REGISTERED);
        }
        return null;

    }

    /**
     * 由属性名及属性值设置相应布隆过滤器
     * @param name 属性名
     * @param po po对象
     * createdBy: LiangJi3229 2020-11-10 18:41
     */
    public void setBloomFilterByName(String name,NewUserPo po) {
        try {
            Field field = NewUserPo.class.getDeclaredField(name);
            Method method=po.getClass().getMethod("get"+name.substring(0,1).toUpperCase()+name.substring(1));
            logger.debug("add value "+method.invoke(po)+" to "+field.getName()+suffixName);
            bloomFilter.addByBloomFilter(field.getName()+suffixName,method.invoke(po));
        }
        catch (Exception ex){
            logger.error("Exception happened:"+ex.getMessage());
        }
    }

    /**
     * 由vo创建newUser检查重复后插入
     * @param vo vo对象
     * @return ReturnObject
     * createdBy: LiangJi3229 2020-11-10 18:41
     */
    public ReturnObject createNewUserByVo(NewUserVo vo){
        //logger.debug(String.valueOf(bloomFilter.includeByBloomFilter("mobileBloomFilter","FAED5EEF1C8562B02110BCA3F9165CBE")));
        //by default,email/mobile are both needed
        NewUserPo userPo=new NewUserPo();
        ReturnObject returnObject;
        userPo.setEmail(AES.encrypt(vo.getEmail(), User.AESPASS));
        userPo.setMobile(AES.encrypt(vo.getMobile(),User.AESPASS));
        userPo.setUserName(vo.getUserName());
        returnObject=checkBloomFilter(userPo);
        //logger.debug(returnObject.getErrmsg());
        if(returnObject!=null){
            logger.debug("found duplicate in bloomFilter");
            return returnObject;
        }
        //check in user table
        if(isEmailExist(userPo.getEmail())){
            setBloomFilterByName("email",userPo);
            return new ReturnObject(ResponseCode.EMAIL_REGISTERED);
        }
        if(isMobileExist(userPo.getMobile())){
            setBloomFilterByName("mobile",userPo);
            return new ReturnObject(ResponseCode.MOBILE_REGISTERED);
        }
        if(isUserNameExist(userPo.getUserName())){
            setBloomFilterByName("userName",userPo);
            return new ReturnObject(ResponseCode.USER_NAME_REGISTERED);
        }


        userPo.setPassword(AES.encrypt(vo.getPassword(), User.AESPASS));
        userPo.setAvatar(vo.getAvatar());
        userPo.setName(AES.encrypt(vo.getName(), User.AESPASS));
        userPo.setDepartId(vo.getDepartId());
        userPo.setOpenId(vo.getOpenId());
        userPo.setGmtCreate(LocalDateTime.now());
        try{
            newUserPoMapper.insert(userPo);
            returnObject=new ReturnObject<>(userPo);
            logger.debug("success trying to insert newUser");
        }
        //catch exception by unique index

        catch (DuplicateKeyException e){
            logger.debug("failed trying to insert newUser");
            //e.printStackTrace();
            String info=e.getMessage();
            if(info.contains("user_name_uindex")){
                setBloomFilterByName("userName",userPo);
                return new ReturnObject(ResponseCode.USER_NAME_REGISTERED);
            }
            if(info.contains("email_uindex")){
                setBloomFilterByName("email",userPo);
                return new ReturnObject(ResponseCode.EMAIL_REGISTERED);
            }
            if(info.contains("mobile_uindex")){
                setBloomFilterByName("mobile",userPo);
                return new ReturnObject(ResponseCode.MOBILE_REGISTERED);
            }

        }
        catch (Exception e){
            logger.error("Internal error Happened:"+e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR);
        }
        return returnObject;
    }
    /**
     * 检查用户名重复
     * @param userName 需要检查的用户名
     * @return boolean
     * createdBy LiangJi@3229
     */
    public boolean isUserNameExist(String userName){
        logger.debug("is checking userName in user table");
        UserPoExample example=new UserPoExample();
        UserPoExample.Criteria criteria=example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<UserPo> userPos=userPoMapper.selectByExample(example);
        //if(!userPos.isEmpty())logger.debug(userPos.get(0).getEmail()+"-"+AES.decrypt(userPos.get(0).getMobile(),User.AESPASS));
        return !userPos.isEmpty();
    }

    /**
     * 检查邮箱重复
     * @param email
     * @return boolean
     * createdBy LiangJi@3229
     */
    public boolean isEmailExist(String email){
        logger.debug("is checking email in user table");
        UserPoExample example=new UserPoExample();
        UserPoExample.Criteria criteria=example.createCriteria();
        criteria.andEmailEqualTo(email);
        List<UserPo> userPos=userPoMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    /**
     * 检查电话重复
     * @param mobile 电话号码
     * @return boolean
     * createdBy LiangJi@3229
     */
    public boolean isMobileExist(String mobile){
        logger.debug("is checking mobile in user table");
        UserPoExample example=new UserPoExample();
        UserPoExample.Criteria criteria=example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<UserPo> userPos=userPoMapper.selectByExample(example);
        return !userPos.isEmpty();
    }
    /**
     * (物理) 删除新用户
     *
     * @param id 用户 id
     * @return 返回对象 ReturnObj
     * @author 24320182203227 Li Zihan
     */
    public ReturnObject<Object> physicallyDeleteUser(Long id) {
        ReturnObject<Object> retObj;
        try {
            int ret = newUserPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                logger.info("用户不存在或已被删除：id = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("用户 id = " + id + " 已被永久删除");
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e)
        {
            logger.debug("sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * ID获取用户信息
     * @author Li Zihan 24320182203227
     * @param Id
     * @return 用户
     */
    public NewUserPo findNewUserById(Long id) {
        logger.debug("findUserById: Id =" + id);
        NewUserPo newUserPo = newUserPoMapper.selectByPrimaryKey(id);
        if (newUserPo == null) {
            logger.error("getNewUser: 新用户数据库不存在该用户 userid=" + id);
        }
        return newUserPo;
    }

}

