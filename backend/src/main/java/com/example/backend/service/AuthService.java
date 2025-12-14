package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import com.example.backend.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 认证服务
 * 处理登录和注册逻辑
 */
@Service
public class AuthService {

    @Autowired
    private AdminLoginRepository adminLoginRepository;

    @Autowired
    private CustomerLoginRepository customerLoginRepository;

    @Autowired
    private DriverLoginRepository driverLoginRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    /**
     * 登录
     * @param request 登录请求
     * @return 登录响应
     */
    public LoginResponse login(LoginRequest request) {
        String role = request.getRole();
        String username = request.getUsername();
        String password = request.getPassword();

        if (role == null || username == null || password == null) {
            throw new IllegalArgumentException("角色、用户名和密码不能为空");
        }

        switch (role.toLowerCase()) {
            case "admin":
                return loginAdmin(username, password);
            case "customer":
                return loginCustomer(username, password);
            case "driver":
                return loginDriver(username, password);
            default:
                throw new IllegalArgumentException("未知的角色类型: " + role);
        }
    }

    /**
     * 管理员登录
     */
    private LoginResponse loginAdmin(String username, String password) {
        Optional<AdminLogin> adminLoginOpt = adminLoginRepository.findByUsername(username);
        
        if (!adminLoginOpt.isPresent()) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        AdminLogin adminLogin = adminLoginOpt.get();
        
        if (!PasswordUtil.matches(password, adminLogin.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        return new LoginResponse(
            adminLogin.getAdminId(),
            adminLogin.getUsername(),
            "admin",
            null,
            null,
            "管理员登录成功"
        );
    }

    /**
     * 客户登录
     */
    private LoginResponse loginCustomer(String identifier, String password) {
        Optional<CustomerLogin> customerLoginOpt = customerLoginRepository.findByIdentifier(identifier);
        
        if (!customerLoginOpt.isPresent()) {
            throw new IllegalArgumentException("账号或密码错误");
        }

        CustomerLogin customerLogin = customerLoginOpt.get();
        
        if (!PasswordUtil.matches(password, customerLogin.getPassword())) {
            throw new IllegalArgumentException("账号或密码错误");
        }

        Customer customer = customerLogin.getCustomer();
        if (customer == null) {
            customer = customerRepository.findById(customerLogin.getCNo()).orElse(null);
        }

        return new LoginResponse(
            customerLogin.getCNo(),
            customer != null ? customer.getCName() : "客户",
            "customer",
            customer != null ? customer.getPhone() : null,
            customer != null ? customer.getCompany() : null,
            "客户登录成功"
        );
    }

    /**
     * 司机登录
     */
    private LoginResponse loginDriver(String identifier, String password) {
        Optional<DriverLogin> driverLoginOpt = driverLoginRepository.findByIdentifier(identifier);
        
        if (!driverLoginOpt.isPresent()) {
            throw new IllegalArgumentException("工号或密码错误");
        }

        DriverLogin driverLogin = driverLoginOpt.get();
        
        if (!PasswordUtil.matches(password, driverLogin.getPassword())) {
            throw new IllegalArgumentException("工号或密码错误");
        }

        Driver driver = driverLogin.getDriver();
        if (driver == null) {
            driver = driverRepository.findById(driverLogin.getDNo()).orElse(null);
        }

        // 检测是否使用默认密码123456
        boolean isDefaultPassword = PasswordUtil.matches("123456", driverLogin.getPassword());
        
        LoginResponse response = new LoginResponse(
            driverLogin.getDNo(),
            driver != null ? driver.getDName() : "司机",
            "driver",
            null,
            driver != null ? driver.getLicenseType() : null,
            isDefaultPassword ? "登录成功，您正在使用默认密码，请尽快修改密码" : "司机登录成功"
        );
        response.setNeedChangePassword(isDefaultPassword);
        
        return response;
    }

    /**
     * 客户注册
     * @param request 注册请求
     * @return 登录响应
     */
    @Transactional
    public LoginResponse registerCustomer(CustomerRegisterRequest request) {
        // 验证手机号是否已存在
        List<Customer> existingCustomers = customerRepository.findByPhone(request.getPhone());
        if (!existingCustomers.isEmpty()) {
            throw new IllegalArgumentException("该手机号已被注册");
        }

        // 生成客户号
        String cNo = generateCustomerNo();

        // 创建客户信息
        Customer customer = new Customer();
        customer.setCNo(cNo);
        customer.setCName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setCompany(request.getCompany());
        customer.setAddress(request.getAddress());
        customer.setPostCode(request.getPostCode());
        customerRepository.save(customer);

        // 创建登录凭证（密码加密）
        CustomerLogin customerLogin = new CustomerLogin();
        customerLogin.setCNo(cNo);
        customerLogin.setPhone(request.getPhone());
        customerLogin.setPassword(PasswordUtil.encode(request.getPassword()));
        customerLoginRepository.save(customerLogin);

        return new LoginResponse(
            cNo,
            customer.getCName(),
            "customer",
            customer.getPhone(),
            customer.getCompany(),
            "注册成功"
        );
    }

    /**
     * 生成客户号（C + 7位数字）
     * 依次递增生成
     */
    private String generateCustomerNo() {
        String maxCNo = customerRepository.findMaxCNo();
        int nextNum = 1; // 默认从1开始
        
        if (maxCNo != null && maxCNo.startsWith("C")) {
            // 提取现有最大编号的数字部分并加1
            try {
                int currentMax = Integer.parseInt(maxCNo.substring(1));
                nextNum = currentMax + 1;
            } catch (NumberFormatException e) {
                // 如果解析失败，使用默认值1
                nextNum = 1;
            }
        }
        
        return String.format("C%07d", nextNum);
    }

    /**
     * 司机修改密码
     * @param request 修改密码请求
     * @return 成功消息
     */
    @Transactional
    public String changeDriverPassword(ChangePasswordRequest request) {
        // 查找司机登录信息
        Optional<DriverLogin> driverLoginOpt = driverLoginRepository.findById(request.getUserId());
        if (!driverLoginOpt.isPresent()) {
            throw new IllegalArgumentException("司机不存在");
        }
        
        DriverLogin driverLogin = driverLoginOpt.get();
        
        // 验证当前密码
        if (!PasswordUtil.matches(request.getCurrentPassword(), driverLogin.getPassword())) {
            throw new IllegalArgumentException("当前密码不正确");
        }
        
        // 验证新密码不能与当前密码相同
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("新密码不能与当前密码相同");
        }
        
        // 更新密码
        driverLogin.setPassword(PasswordUtil.encode(request.getNewPassword()));
        driverLoginRepository.save(driverLogin);
        
        return "密码修改成功";
    }

}
