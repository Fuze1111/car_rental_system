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

        return new LoginResponse(
            driverLogin.getDNo(),
            driver != null ? driver.getDName() : "司机",
            "driver",
            null,
            driver != null ? driver.getLicenseType() : null,
            "司机登录成功"
        );
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
        customer.setPostCode(null); // 可选字段
        customerRepository.save(customer);

        // 创建登录凭证（密码加密）
        CustomerLogin customerLogin = new CustomerLogin();
        customerLogin.setCNo(cNo);
        customerLogin.setPassword(PasswordUtil.encode(request.getPassword()));
        customerLogin.setCustomer(customer);
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
     * 司机注册
     * @param request 注册请求
     * @return 登录响应
     */
    @Transactional
    public LoginResponse registerDriver(DriverRegisterRequest request) {
        // 生成司机号
        String dNo = generateDriverNo();

        // 创建司机信息
        Driver driver = new Driver();
        driver.setDNo(dNo);
        driver.setDName(request.getName());
        driver.setLicenseType(request.getLicenseType());
        driver.setDStatus("在职"); // 默认在职
        driver.setSalary(null); // 需要管理员设置
        driver.setStartWork(LocalDate.now()); // 当前日期
        driverRepository.save(driver);

        // 创建登录凭证（密码加密）
        DriverLogin driverLogin = new DriverLogin();
        driverLogin.setDNo(dNo);
        driverLogin.setPassword(PasswordUtil.encode(request.getPassword()));
        driverLogin.setDriver(driver);
        driverLoginRepository.save(driverLogin);

        return new LoginResponse(
            dNo,
            driver.getDName(),
            "driver",
            null,
            driver.getLicenseType(),
            "注册成功，请等待管理员审核"
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
     * 生成司机号（D + 7位数字）
     * 依次递增生成
     */
    private String generateDriverNo() {
        String maxDNo = driverRepository.findMaxDNo();
        int nextNum = 1; // 默认从1开始
        
        if (maxDNo != null && maxDNo.startsWith("D")) {
            // 提取现有最大编号的数字部分并加1
            try {
                int currentMax = Integer.parseInt(maxDNo.substring(1));
                nextNum = currentMax + 1;
            } catch (NumberFormatException e) {
                // 如果解析失败，使用默认值1
                nextNum = 1;
            }
        }
        
        return String.format("D%07d", nextNum);
    }
}
