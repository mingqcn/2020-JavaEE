package cn.edu.xmu.ooad.util;

/**
 * 返回的错误码
 * @author Ming Qiu
 */
public enum ResponseCode {
    OK(0,"成功"),
    /***************************************************
     *    系统级错误
     **************************************************/
    INTERNAL_SERVER_ERR(500,"服务器内部错误"),
    //所有需要登录才能访问的API都可能会返回以下错误
    AUTH_INVALID_JWT(501,"JWT不合法"),
    AUTH_JWT_EXPIRED(502,"JWT过期"),

    //以下错误码提示可以自行修改
    //--------------------------------------------
    FIELD_NOTVALID(503,"字段不合法"),
    //所有路径带id的API都可能返回此错误
    RESOURCE_ID_NOTEXIST(504,"操作的资源id不存在"),
    RESOURCE_ID_OUTSCOPE(505,"操作的资源id不是自己的对象"),
    FILE_NO_WRITE_PERMISSION(506,"目录文件夹没有写入的权限"),
    RESOURCE_FALSIFY(507, "信息签名不正确"),
    IMG_FORMAT_ERROR(508,"图片格式不正确"),
    IMG_SIZE_EXCEED(509,"图片大小超限"),
    //--------------------------------------------


    /***************************************************
     *    其他模块错误码
     **************************************************/
    ADDRESS_OUTLIMIT(601,"达到地址簿上限"),
    REGION_OBSOLETE(602,"地区已废弃"),
    ADVERTISEMENT_OUTLIMIT(603,"达到时段广告上限"),
    TIMESEG_CONFLICT(604,"时段冲突"),
    SHAREACT_CONFLICT(605,"分享活动时段冲突"),
    ORDERITEM_NOTSHARED(606,"订单明细无分享记录"),
    FLASHSALE_OUTLIMIT(607,"达到时段秒杀上限"),
    ADVERTISEMENT_STATENOTALLOW(608,"广告状态禁止"),
    AFTERSALE_STATENOTALLOW(609,"售后单状态禁止"),
    Log_Bigger(610,"开始时间大于结束时间"),
    Log_BEGIN_NULL(611,"开始时间不能为空"),
    Log_END_NULL(612,"结束时间不能为空"),
    /***************************************************
     *    权限模块错误码
     **************************************************/
    AUTH_INVALID_ACCOUNT(700, "用户名不存在或者密码错误"),
    AUTH_ID_NOTEXIST(701,"登录用户id不存在"),
    AUTH_USER_FORBIDDEN(702,"用户被禁止登录"),
    AUTH_NEED_LOGIN(704, "需要先登录"),
    AUTH_NOT_ALLOW(705,"无权限访问"),
    USER_NAME_REGISTERED( 731,"用户名已被注册"),
    EMAIL_REGISTERED(732, "邮箱已被注册"),
    MOBILE_REGISTERED(733,"电话已被注册"),
    ROLE_REGISTERED(736, "角色名已存在"),
    USER_ROLE_REGISTERED(737, "用户已拥有该角色"),
    PASSWORD_SAME(741,"不能与旧密码相同"),
    URL_SAME(742,"权限url与RequestType重复"),
    PRIVILEGE_SAME(743,"权限名称重复"),
    PRIVILEGE_BIT_SAME(744,"权限位重复"),
    EMAIL_WRONG(745,"与系统预留的邮箱不一致"),
    MOBILE_WRONG(746,"与系统预留的电话不一致"),
    USERPROXY_CONFLICT(747,"同一时间段有冲突的代理关系"),
    EMAIL_NOTVERIFIED(748,"Email未确认"),
    MOBILE_NOTVERIFIED(749,"电话号码未确认"),
    USERPROXY_BIGGER(750,"开始时间要小于失效时间"),
    USERPROXY_SELF(751,"自己不可以代理自己"),
    USERPROXY_DEPART_CONFLICT(752,"两个代理双方的部门冲突"),
    USERPROXY_DEPART_MANAGER_CONFLICT(753,"管理员无此部门权限"),
    /***************************************************
     *    订单模块错误码
     **************************************************/
    ORDER_STATENOTALLOW(801,"订单状态禁止"),
    FREIGHTNAME_SAME(802,"运费模板名重复"),
    REGION_SAME(803,"运费模板中该地区已经定义"),
    REFUND_MORE(804,"退款金额超过支付金额"),
    REGION_NOT_REACH(805,"该地区不可达"),
    SHOP_ID_NOTEXIST(825,"不存在对应的shopid"),
    DEFAULTMODEL_EXISTED(826,"已经存在对应的默认模板"),
    MODEL_ID_NOTEXIST(827,"shopid不存在对应的模板id"),
    MODEL_TYPE_DISMATCH(827,"模板ype dismatch"),
    /***************************************************
     *    商品模块错误码
     **************************************************/
    SKU_NOTENOUGH(900,"商品规格库存不够"),
    SKUSN_SAME(901,"商品规格重复"),
    SKUPRICE_CONFLICT(902,"商品浮动价格时间冲突"),
    USER_NOTBUY(903,"用户没有购买此商品"),
    COUPONACT_STATENOTALLOW(904,"优惠活动状态禁止"),
    COUPON_STATENOTALLOW(905,"优惠卷状态禁止"),
    PRESALE_STATENOTALLOW(906,"预售活动状态禁止"),
    GROUPON_STATENOTALLOW(907,"团购活动状态禁止"),
    USER_HASSHOP(908,"用户已经有店铺"),
    COUPON_NOTBEGIN(909,"未到优惠卷领取时间"),
    COUPON_FINISH(910,"优惠卷领罄"),
    COUPON_END(911,"优惠卷活动终止"),
    STATE_NOCHANGE(920,"状态未改变"),
    CATEALTER_INVALID(921,"对SPU类别操作无效"),
    BRANDALTER_INVALID(922,"对SPU品牌操作无效"),
    ACTIVITYALTER_INVALID(923,"对活动的无效操作"),
    ACTIVITY_NOTFOUND(924,"无符合条件的优惠活动"),
    SHOP_NOTOPERABLE(925,"不可对该商铺进行操作"),
    SPU_NOTOPERABLE(926,"失效的SPU"),
    DELETE_ONLINE_NOTALLOW(931, "不允许删除已上线状态的活动"),
    COMMENT_EXISTED(941,"该订单条目已评论"),
    BRAND_NAME_SAME(990,"品牌名称已存在"),
    CATEGORY_NAME_SAME(991, "类目名称已存在"),
    SHOP_STATENOTALLOW(980, "当前店铺状态不允许进行此类操作");

    private int code;
    private String message;
    ResponseCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }

}
