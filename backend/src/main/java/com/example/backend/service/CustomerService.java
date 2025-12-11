package com.example.backend.service;

import com.example.backend.dto.CustomerRequestDTO;
import com.example.backend.dto.CustomerResponseDTO;
import com.example.backend.entity.Customer;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.repository.RentalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 客户信息管理服务类
 * 提供客户信息的业务逻辑处理
 */
@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * 获取所有客户信息
     * @return 客户信息列表
     */
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取所有客户信息
     * @param pageable 分页参数
     * @return 客户信息分页结果
     */
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        List<CustomerResponseDTO> customerDTOs = customerPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(customerDTOs, pageable, customerPage.getTotalElements());
    }

    /**
     * 根据客户号获取客户信息
     * @param cNo 客户号
     * @return 客户信息
     */
    public Optional<CustomerResponseDTO> getCustomerById(String cNo) {
        return customerRepository.findById(cNo)
                .map(this::convertToResponseDTO);
    }

    /**
     * 创建新客户
     * @param customerRequestDTO 客户请求DTO
     * @return 创建的客户信息
     */
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        // 检查客户号是否已存在
        if (customerRepository.existsById(customerRequestDTO.getCNo())) {
            throw new RuntimeException("客户号已存在: " + customerRequestDTO.getCNo());
        }

        Customer customer = convertToEntity(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(savedCustomer);
    }

    /**
     * 更新客户信息
     * @param cNo 客户号
     * @param customerRequestDTO 客户请求DTO
     * @return 更新后的客户信息
     */
    public CustomerResponseDTO updateCustomer(String cNo, CustomerRequestDTO customerRequestDTO) {
        Customer existingCustomer = customerRepository.findById(cNo)
                .orElseThrow(() -> new RuntimeException("客户不存在: " + cNo));

        // 更新客户信息
        if (customerRequestDTO.getCName() != null) {
            existingCustomer.setCName(customerRequestDTO.getCName());
        }
        if (customerRequestDTO.getCompany() != null) {
            existingCustomer.setCompany(customerRequestDTO.getCompany());
        }
        if (customerRequestDTO.getPhone() != null) {
            existingCustomer.setPhone(customerRequestDTO.getPhone());
        }
        if (customerRequestDTO.getAddress() != null) {
            existingCustomer.setAddress(customerRequestDTO.getAddress());
        }
        if (customerRequestDTO.getPostCode() != null) {
            existingCustomer.setPostCode(customerRequestDTO.getPostCode());
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToResponseDTO(updatedCustomer);
    }

    /**
     * 删除客户
     * @param cNo 客户号
     */
    public void deleteCustomer(String cNo) {
        if (!customerRepository.existsById(cNo)) {
            throw new RuntimeException("客户不存在: " + cNo);
        }
        customerRepository.deleteById(cNo);
    }

    /**
     * 根据姓名模糊查询客户
     * @param cName 姓名关键字
     * @return 客户列表
     */
    public List<CustomerResponseDTO> searchCustomersByName(String cName) {
        List<Customer> customers = customerRepository.findByCNameContaining(cName);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据电话号码查询客户
     * @param phone 电话号码
     * @return 客户列表
     */
    public List<CustomerResponseDTO> searchCustomersByPhone(String phone) {
        List<Customer> customers = customerRepository.findByPhone(phone);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据电话号码模糊查询客户
     * @param phone 电话号码关键字
     * @return 客户列表
     */
    public List<CustomerResponseDTO> searchCustomersByPhoneContaining(String phone) {
        List<Customer> customers = customerRepository.findByPhoneContaining(phone);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据公司名称查询客户
     * @param company 公司名称
     * @return 客户列表
     */
    public List<CustomerResponseDTO> getCustomersByCompany(String company) {
        List<Customer> customers = customerRepository.findByCompany(company);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据公司名称模糊查询客户
     * @param company 公司名称关键字
     * @return 客户列表
     */
    public List<CustomerResponseDTO> searchCustomersByCompany(String company) {
        List<Customer> customers = customerRepository.findByCompanyContaining(company);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据地址模糊查询客户
     * @param address 地址关键字
     * @return 客户列表
     */
    public List<CustomerResponseDTO> searchCustomersByAddress(String address) {
        List<Customer> customers = customerRepository.findByAddressContaining(address);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 多条件查询客户
     * @param cName 客户姓名
     * @param phone 电话号码
     * @param company 公司名称
     * @return 客户列表
     */
    public List<CustomerResponseDTO> searchCustomers(String cName, String phone, String company) {
        List<Customer> customers = customerRepository.findByMultipleConditions(cName, phone, company);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据客户号模糊查询客户
     * @param cNo 客户号关键字
     * @return 客户列表
     */
    public List<CustomerResponseDTO> searchCustomersByCNo(String cNo) {
        List<Customer> customers = customerRepository.findByCNoContaining(cNo);
        return customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新客户电话
     * @param cNo 客户号
     * @param phone 新电话号码
     * @return 更新后的客户信息
     */
    public CustomerResponseDTO updateCustomerPhone(String cNo, String phone) {
        Customer customer = customerRepository.findById(cNo)
                .orElseThrow(() -> new RuntimeException("客户不存在: " + cNo));

        customer.setPhone(phone);
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(updatedCustomer);
    }

    /**
     * 更新客户地址
     * @param cNo 客户号
     * @param address 新地址
     * @return 更新后的客户信息
     */
    public CustomerResponseDTO updateCustomerAddress(String cNo, String address) {
        Customer customer = customerRepository.findById(cNo)
                .orElseThrow(() -> new RuntimeException("客户不存在: " + cNo));

        customer.setAddress(address);
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(updatedCustomer);
    }

    /**
     * 获取客户统计信息
     * @return 统计信息Map
     */
    public java.util.Map<String, Object> getCustomerStatistics() {
        long totalCount = customerRepository.count();
        
        // 使用数据库聚合函数统计有公司的客户数量，提高性能
        long withCompanyCount = customerRepository.countWithCompany();
        long individualCount = totalCount - withCompanyCount;

        return java.util.Map.of(
            "total", totalCount,
            "withCompany", withCompanyCount,
            "individual", individualCount
        );
    }

    /**
     * 将Customer实体转换为CustomerResponseDTO
     */
    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setCNo(customer.getCNo());
        dto.setCName(customer.getCName());
        dto.setCompany(customer.getCompany());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setPostCode(customer.getPostCode());

        // 获取租赁次数
        long rentalCount = rentalRepository.countByCNo(customer.getCNo());
        dto.setRentalCount(rentalCount);

        return dto;
    }

    /**
     * 管理员专用：分页多条件搜索客户
     * @param name 姓名关键字
     * @param phone 电话关键字
     * @param company 公司关键字
     * @param pageable 分页参数
     * @return 分页结果
     */
    public Page<CustomerResponseDTO> searchCustomersWithPage(String name, String phone, String company, Pageable pageable) {
        Page<Customer> customerPage = customerRepository.searchCustomers(name, phone, company, pageable);
        List<CustomerResponseDTO> dtoList = customerPage.getContent().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, customerPage.getTotalElements());
    }

    /**
     * 将CustomerRequestDTO转换为Customer实体
     */
    private Customer convertToEntity(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setCNo(dto.getCNo());
        customer.setCName(dto.getCName());
        customer.setCompany(dto.getCompany());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setPostCode(dto.getPostCode());
        return customer;
    }
}