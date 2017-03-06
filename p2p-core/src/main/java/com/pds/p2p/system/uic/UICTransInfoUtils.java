package com.pds.p2p.system.uic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.pds.p2p.core.j2ee.context.SpringContextHolder;
import com.pds.p2p.core.j2ee.service.ServiceException;
import com.pds.p2p.system.uic.dto.UICDepartmentDTO;
import com.pds.p2p.system.uic.dto.UICUserInfoDto;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.baidu.uic.ws.dto.DepartmentDTO;
import com.baidu.uic.ws.dto.UserDTO;
import com.baidu.uic.ws.interfaces.IDepartmentRemoteService;
import com.baidu.uic.ws.interfaces.IUserRemoteService;

/**
 * ******************************* 百度供应商平台 功能模块：UIC统一取数工具
 * ********************************
 *
 * @author v_lianghua
 * @version 1.0.0
 *          ********************************
 *          修改项 修改人 修改时间
 *          <p>
 *          ********************************
 * @date 2014-12-29 上午11:33:25
 * @copyright baidu.com 2014
 */
public class UICTransInfoUtils {

    private static final Logger logger = LogManager.getLogger(UICTransInfoUtils.class);

    /**
     * 递归参数
     */
    private static final ThreadLocal<AtomicInteger> beginContext = new ThreadLocal<AtomicInteger>();

    /**
     * UIC取数失败后最大训话取得次数
     */
    private static final int LAST_GET = 5;

    /**
     * 私有化实例
     */
    private static UICTransInfoUtils instance = null;

    /**
     * 注入用户信息远程接口
     */
    private static IUserRemoteService uicUserRemoteService;

    /**
     * 注入部门信息远程接口
     */
    private static IDepartmentRemoteService uicDepartmentRemoteService;

    /**
     * 不允许外部新建实例
     */
    private UICTransInfoUtils() {
    }

    /**
     * 获取到实例对象
     */
    public static synchronized UICTransInfoUtils getInstance() {

        if (instance == null) {
            instance = new UICTransInfoUtils();
        }
        if (uicUserRemoteService == null) {
            uicUserRemoteService = SpringContextHolder.getBean(IUserRemoteService.class);
        }
        if (uicDepartmentRemoteService == null) {
            uicDepartmentRemoteService = SpringContextHolder.getBean(IDepartmentRemoteService.class);
        }

        return instance;
    }

    /**
     * 根据用户名从UIC获取用户数据
     */
    public UICUserInfoDto getUserByUserName(String userName) {

        // 传入的参数
        Object obj = new Object[] {"userName=" + userName};

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                // 写入公共类
                return null;
            }
            userDTO = realGetUserByUserName(userName, obj);
        }
        //如果为空，从本地获取
        if (userDTO == null) {
          /* SmgSysUser localUser=sysUserManagerService.getUserByUserName(userName);
           userDTO=new UserDTO();
           if(localUser!=null){
               userDTO.setUsername(userName);
               userDTO.setName(localUser.getName());
               userDTO.setDepartmentName(localUser.getDepartmentName());
               userDTO.setDepartmentCode(localUser.getDepartmentCode());
           }*/
        }
        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetUserByUserName(String userName, Object obj) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getUserByUsername(userName);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), obj);
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据用户名递归获取主管信息
     */
    public UICUserInfoDto getUserManage(String userName, long[] grade) {

        // 传入的参数
        Object obj = new Object[] {"userName=" + userName, "grade=" + grade};

        try {
            beginCreament();
            // UIC远程获取用户数据并转换
            UICUserInfoDto returnDto = getUserByUserName(userName);
            if (returnDto == null) {
                refreshCreament();
                return returnDto;
            } else {
                UICUserInfoDto supDto = getUserByUserName(returnDto.getSuperiorUsername());
                if (ArrayUtils.indexOf(grade, supDto.getGrade()) >= 0) {
                    refreshCreament();
                    return supDto;
                } else {
                    return getUserManage(supDto.getUsername(), grade);
                }
            }
        } catch (ServiceException e) {
            this.debugLog(e.getMessage(), obj);
            refreshCreament();
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            this.debugLog(e.getMessage(), obj);
            refreshCreament();
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 重置递归参数
     */
    public void beginCreament() throws ServiceException {

        if (beginContext.get() == null) {
            beginContext.set(new AtomicInteger(0));
        }
        if (beginContext.get().addAndGet(1) > 26) {
            beginContext.get().set(0);
            throw new ServiceException("主管链递归获取超过允许循环次数");

        }
    }

    /**
     * 清空递归参数
     */
    public void refreshCreament() {

        beginContext.get().set(0);
    }

    /**
     * 根据ID从UIC获取用户数据
     */
    public UICUserInfoDto getUserByUserId(Long userId) {

        // 传入的参数
        Object obj = new Object[] {"userId=" + userId};

        // 传入参数为空，返回
        if (userId == null) {
            // 写入公共类
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetUserByUserId(userId);
        }

        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetUserByUserId(Long userId) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getUserByUserId(userId);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"userId=" + userId});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据用户userEmail从UIC获取用户数据
     */
    public UICUserInfoDto getUserByUserEmail(String userEmail) {

        // 传入的参数
        Object obj = new Object[] {"userEmail=" + userEmail};

        // 传入参数为空，返回
        if (userEmail == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetUserByUserEmail(userEmail);
        }

        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetUserByUserEmail(String userEmail) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getUserByUserEmail(userEmail);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"userEmail=" + userEmail});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据输入的字符串模糊查询邮箱
     */
    public List<UICUserInfoDto> getAllUsersByBlurEmail(String blurEmail) {

        // 传入的参数
        Object obj = new Object[] {"blurEmail=" + blurEmail};

        // 传入参数为空，返回
        if (blurEmail == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersByBlurEmail(blurEmail);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);
        return returnList;
    }

    private List<UserDTO> realGetAllUsersByBlurEmail(String blurEmail) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicUserRemoteService.getAllUsersByBlurEmail(blurEmail);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"blurEmail=" + blurEmail});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据名称模糊查找用户
     */
    public List<UICUserInfoDto> getAllUsersByBlurName(String blurName) {
        // 传入的参数
        Object obj = new Object[] {"blurName=" + blurName};

        // 传入参数为空，返回
        if (blurName == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersByBlurName(blurName);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersByBlurName(String blurName) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicUserRemoteService.getAllUsersByBlurName(blurName);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"blurName=" + blurName});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据用户联系信息搜索用户
     */
    public List<UICUserInfoDto> getAllUsersByContact(String contact) {
        // 传入的参数
        Object obj = new Object[] {"contact=" + contact};

        // 传入参数为空，返回
        if (contact == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersByContact(contact);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersByContact(String contact) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicUserRemoteService.getAllUsersByContact(contact);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"contact=" + contact});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据用户ID获取指定用户本人直到第deep级上级的信息
     */
    public List<UICUserInfoDto> getAllSuperiorsById(Long userId, int deep) {

        // 传入的参数
        Object obj = new Object[] {"userId=" + userId, "deep=" + deep};

        // 传入参数为空，返回
        if (userId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllSuperiorsById(userId, deep);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllSuperiorsById(Long userId, int deep) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicUserRemoteService.getAllSuperiorsById(userId, deep);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"userId=" + userId, "deep=" + deep});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据上级ID获取其下deep级的用户信息
     */
    public List<UICUserInfoDto> getAllUsersBySuperiorId(Long superiorId, int deep) {

        // 传入的参数
        Object obj = new Object[] {"superiorId=" + superiorId, "deep=" + deep};

        // 传入参数为空，返回
        if (superiorId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersBySuperiorId(superiorId, deep);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersBySuperiorId(Long superiorId, int deep) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicUserRemoteService.getAllUsersBySuperiorId(superiorId, deep);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"superiorId=" + superiorId, "deep=" + deep});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据上级用户名获取其下deep级的用户信息
     */
    public List<UICUserInfoDto> getAllUsersBySuperiorUsername(String superiorUsername, int deep) {

        // 传入的参数
        Object obj = new Object[] {"superiorUsername=" + superiorUsername, "deep=" + deep};

        // 传入参数为空，返回
        if (superiorUsername == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersBySuperiorUsername(superiorUsername, deep);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersBySuperiorUsername(String superiorUsername, int deep) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicUserRemoteService.getAllUsersBySuperiorUsername(superiorUsername, deep);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"superiorUsername=" + superiorUsername, "deep=" + deep});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据用户ID获取指定用户的第deep级上级
     */
    public UICUserInfoDto getSuperiorById(Long userId, int deep) {

        // 传入的参数
        Object obj = new Object[] {"userId=" + userId, "deep=" + deep};

        // 传入参数为空，返回
        if (userId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetSuperiorById(userId, deep);
        }
        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetSuperiorById(Long userId, int deep) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getSuperiorById(userId, deep);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"userId=" + userId, "deep=" + deep});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据用户名获取指定用户的第deep级上级
     */
    public UICUserInfoDto getSuperiorByUsername(String username, int deep) {

        // 传入的参数
        Object obj = new Object[] {"username=" + username, "deep=" + deep};

        // 传入参数为空，返回
        if (username == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetSuperiorByUsername(username, deep);
        }
        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetSuperiorByUsername(String username, int deep) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getSuperiorByUsername(username, deep);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"username=" + username, "deep=" + deep});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据员工编号查询用户
     */
    public UICUserInfoDto getUserByEmployeeId(Long employeeId) {

        // 传入的参数
        Object obj = new Object[] {"employeeId=" + employeeId};

        // 传入参数为空，返回
        if (employeeId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetUserByEmployeeId(employeeId);
        }
        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetUserByEmployeeId(Long employeeId) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getUserByEmployeeId(employeeId);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"employeeId=" + employeeId});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据员工号查询用户
     */
    public UICUserInfoDto getUserByEmployeeNumber(String employeeNumber) {

        // 传入的参数
        Object obj = new Object[] {"employeeNumber=" + employeeNumber};

        // 传入参数为空，返回
        if (employeeNumber == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetUserByEmployeeNumber(employeeNumber);
        }
        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetUserByEmployeeNumber(String employeeNumber) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getUserByEmployeeNumber(employeeNumber);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"employeeNumber=" + employeeNumber});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据Hi号查询用户
     */
    public UICUserInfoDto getUserByHiNumber(String hiNumber) {

        // 传入的参数
        Object obj = new Object[] {"hiNumber=" + hiNumber};

        // 传入参数为空，返回
        if (hiNumber == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetUserByHiNumber(hiNumber);
        }

        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetUserByHiNumber(String hiNumber) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getUserByHiNumber(hiNumber);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"hiNumber=" + hiNumber});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据身份证号查询用户
     */
    public UICUserInfoDto getUserByIdCardNumber(String idCardNumber) {

        // 传入的参数
        Object obj = new Object[] {"idCardNumber=" + idCardNumber};

        // 传入参数为空，返回
        if (idCardNumber == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetUserByIdCardNumber(idCardNumber);
        }
        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetUserByIdCardNumber(String idCardNumber) {

        UserDTO userDto = null;
        try {
            userDto = uicUserRemoteService.getUserByIdCardNumber(idCardNumber);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"idCardNumber=" + idCardNumber});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据部门名称模糊查询部门
     */
    public List<UICDepartmentDTO> getAllDepartmentsByBlurName(String blurName) {

        // 传入的参数
        Object obj = new Object[] {"blurName=" + blurName};

        // 传入参数为空，返回
        if (blurName == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<DepartmentDTO> departmentDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (departmentDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            departmentDTOList = realGetAllDepartmentsByBlurName(blurName);
        }
        // 设置返回数据
        List<UICDepartmentDTO> returnList = new ArrayList<UICDepartmentDTO>();
        // 部门数据并转换
        this.copyProperties(returnList, departmentDTOList);

        return returnList;
    }

    private List<DepartmentDTO> realGetAllDepartmentsByBlurName(String blurName) {

        List<DepartmentDTO> departmentDTOList = null;
        try {
            departmentDTOList = uicDepartmentRemoteService.getAllDepartmentsByBlurName(blurName);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"blurName=" + blurName});
            e.printStackTrace();
        }

        return departmentDTOList;
    }

    /**
     * 根据编码获取一个部门
     */
    public List<UICDepartmentDTO> getAllDepartmentsByCode(String departmentCode) {

        // 传入的参数
        Object obj = new Object[] {"departmentCode=" + departmentCode};

        // 传入参数为空，返回
        if (departmentCode == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<DepartmentDTO> departmentDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (departmentDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            departmentDTOList = realGetAllDepartmentsByCode(departmentCode);
        }
        // 设置返回数据
        List<UICDepartmentDTO> returnList = new ArrayList<UICDepartmentDTO>();
        // 部门数据并转换
        this.copyProperties(returnList, departmentDTOList);

        return returnList;
    }

    private List<DepartmentDTO> realGetAllDepartmentsByCode(String departmentCode) {

        List<DepartmentDTO> departmentDTOList = null;
        try {
            departmentDTOList = uicDepartmentRemoteService.getAllDepartmentsByCode(departmentCode);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentCode=" + departmentCode});
            e.printStackTrace();
        }

        return departmentDTOList;
    }

    /**
     * 根据部门类型查询部门
     */
    public List<UICDepartmentDTO> getAllDepartmentsByType(String departmentType) {

        // 传入的参数
        Object obj = new Object[] {"departmentType=" + departmentType};

        // 传入参数为空，返回
        if (departmentType == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<DepartmentDTO> departmentDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (departmentDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            departmentDTOList = realGetAllDepartmentsByType(departmentType);
        }
        // 设置返回数据
        List<UICDepartmentDTO> returnList = new ArrayList<UICDepartmentDTO>();
        // 部门数据并转换
        this.copyProperties(returnList, departmentDTOList);

        return returnList;
    }

    private List<DepartmentDTO> realGetAllDepartmentsByType(String departmentType) {

        List<DepartmentDTO> departmentDTOList = null;
        try {
            departmentDTOList = uicDepartmentRemoteService.getAllDepartmentsByType(departmentType);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentType=" + departmentType});
            e.printStackTrace();
        }

        return departmentDTOList;
    }

    /**
     * 获取部门负责人
     */
    public UICUserInfoDto getDirectorByDepartmentId(Long departmentId) {

        // 传入的参数
        Object obj = new Object[] {"departmentId=" + departmentId};

        // 传入参数为空，返回
        if (departmentId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetDirectorByDepartmentId(departmentId);
        }

        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetDirectorByDepartmentId(Long departmentId) {

        UserDTO userDto = null;
        try {
            userDto = uicDepartmentRemoteService.getDirectorByDepartmentId(departmentId);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentId=" + departmentId});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 获取部门的HRBP
     */
    public UICUserInfoDto getHRBPByDepartmentId(Long departmentId) {

        // 传入的参数
        Object obj = new Object[] {"departmentId=" + departmentId};

        // 传入参数为空，返回
        if (departmentId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        UserDTO userDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTO = realGetHRBPByDepartmentId(departmentId);
        }
        // 用户数据并转换
        return (UICUserInfoDto) this.copyProperties(userDTO);
    }

    private UserDTO realGetHRBPByDepartmentId(Long departmentId) {

        UserDTO userDto = null;
        try {
            userDto = uicDepartmentRemoteService.getHRBPByDepartmentId(departmentId);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentId=" + departmentId});
            e.printStackTrace();
        }

        return userDto;
    }

    /**
     * 根据ID获取一个部门
     */
    public UICDepartmentDTO getDepartmentById(Long departmentId) {

        // 传入的参数
        Object obj = new Object[] {"departmentId=" + departmentId};

        // 传入参数为空，返回
        if (departmentId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        DepartmentDTO departmentDTO = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (departmentDTO == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            departmentDTO = realGetDepartmentById(departmentId);
        }
        // 用户数据并转换
        return (UICDepartmentDTO) this.copyProperties(departmentDTO);
    }

    private DepartmentDTO realGetDepartmentById(Long departmentId) {

        DepartmentDTO departmentDTO = null;
        try {
            departmentDTO = uicDepartmentRemoteService.getDepartmentById(departmentId);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentId=" + departmentId});
            e.printStackTrace();
        }

        return departmentDTO;
    }

    /**
     * 根据ID获取部门下的所有用户列表
     */
    public List<UICUserInfoDto> getAllUsersByDepartmentId(Long departmentId) {

        // 传入的参数
        Object obj = new Object[] {"departmentId=" + departmentId};

        // 传入参数为空，返回
        if (departmentId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersByDepartmentId(departmentId);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersByDepartmentId(Long departmentId) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicDepartmentRemoteService.getAllUsersByDepartmentId(departmentId);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentId=" + departmentId});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据Code获取部门下的所有用户列表
     */
    public List<UICUserInfoDto> getAllUsersByDepartmentCode(String departmentCode) {

        // 传入的参数
        Object obj = new Object[] {"departmentCode=" + departmentCode};

        // 传入参数为空，返回
        if (departmentCode == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersByDepartmentCode(departmentCode);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersByDepartmentCode(String departmentCode) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicDepartmentRemoteService.getAllUsersByDepartmentCode(departmentCode);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentCode=" + departmentCode});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 返回指定部门内级别最高的用户
     */
    public List<UICUserInfoDto> getAllTopGradeUsersInDepartment(Long departmentId) {

        // 传入的参数
        Object obj = new Object[] {"departmentId=" + departmentId};

        // 传入参数为空，返回
        if (departmentId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllTopGradeUsersInDepartment(departmentId);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllTopGradeUsersInDepartment(Long departmentId) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicDepartmentRemoteService.getAllTopGradeUsersInDepartment(departmentId);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentId=" + departmentId});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据部门ID获取级别大于等于指定等级的用户
     */
    public List<UICUserInfoDto> getAllUsersAboveGradeInDepartment(Long departmentId, Integer grade) {

        // 传入的参数
        Object obj = new Object[] {"departmentId=" + departmentId, "grade=" + grade};

        // 传入参数为空，返回
        if (departmentId == null) {
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                return null;
            }
            userDTOList = realGetAllUsersAboveGradeInDepartment(departmentId, grade);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersAboveGradeInDepartment(Long departmentId, Integer grade) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicDepartmentRemoteService.getAllUsersAboveGradeInDepartment(departmentId, grade);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentId=" + departmentId, "grade=" + grade});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 根据部门部门ID和等级获取部门中指定等级的用户
     */
    public List<UICUserInfoDto> getAllUsersByDepartmentIdAndGrade(Long departmentId, Integer grade) {

        // 传入的参数
        Object obj = new Object[] {"departmentId=" + departmentId, "grade=" + grade};

        // 传入参数为空，返回
        if (departmentId == null) {
            // 写入公共类
            return null;
        }

        AtomicInteger atoInt = new AtomicInteger();
        List<UserDTO> userDTOList = null;
        // UIC取得的数据为空，并且没有大于最大的获取次数
        while (userDTOList == null) {
            if (atoInt.getAndAdd(1) > LAST_GET) {
                this.debugLog("UIC获取数据失败", obj);
                // 写入公共类
                return null;
            }
            userDTOList = realGetAllUsersByDepartmentIdAndGrade(departmentId, grade);
        }
        // 设置返回数据
        List<UICUserInfoDto> returnList = new ArrayList<UICUserInfoDto>();
        // 用户数据并转换
        this.copyProperties(returnList, userDTOList);

        return returnList;
    }

    private List<UserDTO> realGetAllUsersByDepartmentIdAndGrade(Long departmentId, Integer grade) {

        List<UserDTO> userDtoList = null;
        try {
            userDtoList = uicDepartmentRemoteService.getAllUsersByDepartmentIdAndGrade(departmentId, grade);
        } catch (Exception e) {
            this.debugLog(e.getMessage(), new Object[] {"departmentId=" + departmentId, "grade=" + grade});
            e.printStackTrace();
        }

        return userDtoList;
    }

    /**
     * 多条数据统一复制
     */
    private void copyProperties(List dest, List orig) {

        // 无数据时候直接返回
        if (orig == null || orig.size() == 0) {
            return;
        }

        for (Object obj : orig) {
            if (obj != null) {
                dest.add(copyProperties(obj));
            }
        }

    }

    /**
     * 数据单条数据统一复制，防止UIC返回数据有改动，造成系统大量修改
     */
    private Object copyProperties(Object obj) {

        // 复制用户信息
        if (obj instanceof UserDTO) {
            // 复制
            UICUserInfoDto commonInfoDto = new UICUserInfoDto();
            UserDTO userDto = (UserDTO) obj;
            commonInfoDto.setId(userDto.getId());
            commonInfoDto.setUsername(userDto.getUsername());
            commonInfoDto.setName(userDto.getName());
            commonInfoDto.setEmployeeId(userDto.getEmployeeId());
            commonInfoDto.setEmployeeNumber(userDto.getEmployeeNumber());
            commonInfoDto.setEmployeeType(userDto.getEmployeeType());
            commonInfoDto.setEmail(userDto.getEmail());
            commonInfoDto.setServiceStartDate(userDto.getServiceStartDate());
            commonInfoDto.setServiceEndDate(userDto.getServiceEndDate());
            commonInfoDto.setWorkStartDate(userDto.getWorkStartDate());
            commonInfoDto.setRegularStartDate(userDto.getRegularStartDate());
            commonInfoDto.setDepartmentId(userDto.getDepartmentId());
            commonInfoDto.setDepartmentCode(userDto.getDepartmentCode());
            commonInfoDto.setDepartmentName(userDto.getDepartmentName());
            commonInfoDto.setCompanyId(userDto.getCompanyId());
            commonInfoDto.setCompanyCode(userDto.getCompanyCode());
            commonInfoDto.setCompanyName(userDto.getCompanyName());
            commonInfoDto.setEnglishName(userDto.getEnglishName());
            commonInfoDto.setPhoneNumber(userDto.getPhoneNumber());
            commonInfoDto.setHiNumber(userDto.getHiNumber());
            commonInfoDto.setPositionId(userDto.getPositionId());
            commonInfoDto.setPositionName(userDto.getPositionName());
            commonInfoDto.setGrade(userDto.getGrade());
            commonInfoDto.setGradeName(userDto.getGradeName());
            commonInfoDto.setJobDescription(userDto.getJobDescription());
            commonInfoDto.setEntryLocation(userDto.getEntryLocation());
            commonInfoDto.setInHq(userDto.getInHq());
            commonInfoDto.setExternalPosition(userDto.getExternalPosition());
            commonInfoDto.setSuperiorId(userDto.getSuperiorId());
            commonInfoDto.setSuperiorUsername(userDto.getSuperiorUsername());
            commonInfoDto.setSuperiorEmail(userDto.getSuperiorEmail());
            commonInfoDto.setSuperiorName(userDto.getSuperiorName());
            commonInfoDto.setDepartmentType(userDto.getDepartmentType());
            commonInfoDto.setDepartmentAbbreviation(userDto.getDepartmentAbbreviation());
            commonInfoDto.setWorkGroup(userDto.getWorkGroup());
            commonInfoDto.setIdCardNumber(userDto.getIdCardNumber());
            commonInfoDto.setNationalCode(userDto.getNationalCode());
            commonInfoDto.setDialingCode(userDto.getDialingCode());
            commonInfoDto.setCompleteTelNumber(userDto.getCompleteTelNumber());
            commonInfoDto.setStationNumber(userDto.getStationNumber());
            commonInfoDto.setEducation(userDto.getEducation());
            commonInfoDto.setAge(userDto.getAge());
            commonInfoDto.setCompanyGroup(userDto.getCompanyGroup());
            commonInfoDto.setEmployeeBlack(userDto.getEmployeeBlack());
            commonInfoDto.setPositionCode(userDto.getPositionCode());
            commonInfoDto.setPositionMajorSeq(userDto.getPositionMajorSeq());
            commonInfoDto.setPositionMinorSeq(userDto.getPositionMinorSeq());
            commonInfoDto.setPositionMicroSeq(userDto.getPositionMicroSeq());
            commonInfoDto.setPositionMajorLevel(userDto.getPositionMajorLevel());
            commonInfoDto.setPositionMinorLevel(userDto.getPositionMinorLevel());
            commonInfoDto.setEntrySheetNumber(userDto.getEntrySheetNumber());
            commonInfoDto.setCardNumber(userDto.getCardNumber());
            commonInfoDto.setCardHex(userDto.getCardHex());
            commonInfoDto.setLocation(userDto.getLocation());
            commonInfoDto.setBusinessGroupId(userDto.getBusinessGroupId());
            commonInfoDto.setBusinessGroupName(userDto.getBusinessGroupName());
            commonInfoDto.setSex(userDto.getSex());
            commonInfoDto.setBirthday(userDto.getBirthday());
            commonInfoDto.setMobileNumber(userDto.getMobileNumber());
            commonInfoDto.setPayrollName(userDto.getPayrollName());

            // String type =
            // ConfigrationManager.getValue(ConfigConstants.APP_TYPE,
            // GlobalConstants.EMPTY);
            // if (ConfigConstants.APP_TYPE_DEBUG.equals(type)) {
            //
            // if (commonInfoDto.getUsername().equals("zhouhan02")) {
            // commonInfoDto.setSuperiorUsername("tujie");
            // }
            // if (commonInfoDto.getUsername().equals("tujie")) {
            // commonInfoDto.setGrade(7);
            // commonInfoDto.setSuperiorUsername("gongxuefeng");
            // }
            // if (commonInfoDto.getUsername().equals("gongxuefeng")) {
            // commonInfoDto.setGrade(9);
            // }
            //
            // if (commonInfoDto.getUsername().equals("anpingping")) {
            // commonInfoDto.setSuperiorUsername("lizhenyu");
            // }
            // if (commonInfoDto.getUsername().equals("lizhenyu")) {
            // commonInfoDto.setGrade(7);
            // commonInfoDto.setSuperiorUsername("jingwang");
            // }
            // if (commonInfoDto.getUsername().equals("jingwang")) {
            // commonInfoDto.setGrade(9);
            // }
            // }

            return commonInfoDto;
        }
        // 复制部门信息
        else if (obj instanceof DepartmentDTO) {
            // 复制
            UICDepartmentDTO commonInfoDto = new UICDepartmentDTO();
            DepartmentDTO departmentDTO = (DepartmentDTO) obj;
            commonInfoDto.setId(departmentDTO.getId());
            commonInfoDto.setCode(departmentDTO.getCode());
            commonInfoDto.setName(departmentDTO.getName());
            commonInfoDto.setType(departmentDTO.getType());
            commonInfoDto.setEnglishName(departmentDTO.getEnglishName());
            commonInfoDto.setAbbreviation(departmentDTO.getAbbreviation());
            commonInfoDto.setHrbpId(departmentDTO.getHrbpId());
            commonInfoDto.setHrbpUsername(departmentDTO.getHrbpUsername());
            commonInfoDto.setHrbpEmail(departmentDTO.getHrbpEmail());
            commonInfoDto.setHrbpName(departmentDTO.getHrbpName());
//            commonInfoDto.setEstaffId(departmentDTO.getEstaffId());
            commonInfoDto.setEstaffUsername(departmentDTO.getEstaffUsername());
            commonInfoDto.setDirectorId(departmentDTO.getDirectorId());
            commonInfoDto.setDirectorUsername(departmentDTO.getDirectorUsername());
            commonInfoDto.setDirectorEmail(departmentDTO.getDirectorEmail());
            commonInfoDto.setDirectorName(departmentDTO.getDirectorName());
            commonInfoDto.setParentName(departmentDTO.getParentName());
            commonInfoDto.setBusinessGroupId(departmentDTO.getBusinessGroupId());

            return commonInfoDto;
        }

        return null;
    }

    /**
     * 打印LOG
     */
    private void debugLog(String message, Object params) {

        logger.debug("*****************************UIC取数问题LOG开始***********************************");
        logger.debug("参数：" + JSONObject.toJSONString(params));
        logger.debug(message);
        logger.debug("*****************************UIC取数问题LOG结束***********************************");
    }

}
