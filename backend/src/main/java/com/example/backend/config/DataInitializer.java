package com.example.backend.config;

import com.example.backend.entity.AdminLogin;
import com.example.backend.entity.CustomerLogin;
import com.example.backend.entity.DriverLogin;
import com.example.backend.repository.AdminLoginRepository;
import com.example.backend.repository.CustomerLoginRepository;
import com.example.backend.repository.DriverLoginRepository;
import com.example.backend.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 数据初始化器
 * 在应用启动时，将明文密码转换为加密密码
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminLoginRepository adminLoginRepository;

    @Autowired
    private CustomerLoginRepository customerLoginRepository;

    @Autowired
    private DriverLoginRepository driverLoginRepository;

    @Override
    public void run(String... args) throws Exception {
        // 更新管理员密码
        updateAdminPasswords();
        
        // 更新客户密码
        updateCustomerPasswords();
        
        // 更新司机密码
        updateDriverPasswords();
        
        System.out.println("密码加密初始化完成！");
    }

    /**
     * 更新管理员密码
     */
    private void updateAdminPasswords() {
        Optional<AdminLogin> adminOpt = adminLoginRepository.findByUsername("admin");
        if (adminOpt.isPresent()) {
            AdminLogin admin = adminOpt.get();
            // 检查密码是否已经是加密的（BCrypt哈希长度为60）
            if (admin.getPassword().length() < 60) {
                admin.setPassword(PasswordUtil.encode(admin.getPassword()));
                adminLoginRepository.save(admin);
                System.out.println("管理员密码已加密");
            }
        }
    }

    /**
     * 更新客户密码
     */
    private void updateCustomerPasswords() {
        customerLoginRepository.findAll().forEach(customerLogin -> {
            // 检查密码是否已经是加密的
            if (customerLogin.getPassword().length() < 60) {
                customerLogin.setPassword(PasswordUtil.encode(customerLogin.getPassword()));
                customerLoginRepository.save(customerLogin);
                System.out.println("客户 " + customerLogin.getCNo() + " 密码已加密");
            }
        });
    }

    /**
     * 更新司机密码
     */
    private void updateDriverPasswords() {
        driverLoginRepository.findAll().forEach(driverLogin -> {
            // 检查密码是否已经是加密的
            if (driverLogin.getPassword().length() < 60) {
                driverLogin.setPassword(PasswordUtil.encode(driverLogin.getPassword()));
                driverLoginRepository.save(driverLogin);
                System.out.println("司机 " + driverLogin.getDNo() + " 密码已加密");
            }
        });
    }
}
