package cn.edu.xmu.privilegeservice.client;

/**
 * @Author: Yifei Wang 24320182203286
 * @Date: 2020/12/10 9:43
 */
public interface IUserService {
    boolean changeUserDepart(Long userId, Long departId);

    String getUserName(Long userId);
}
