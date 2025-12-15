package com.example.backend.controller;

import com.example.backend.dto.CustomerRequestDTO;
import com.example.backend.dto.CustomerResponseDTO;
import com.example.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户信息管理控制器
 * 提供客户信息管理的RESTful API接口
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取所有客户信息
     * GET /api/customers
     * @return 客户列表
     */
    @GetMapping
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cNo") String[] sortBy) {
        if (page < 0 || size <= 0) {
            List<CustomerResponseDTO> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } else {
            // 解析排序参数，支持多字段排序和方向控制
            Sort sort = Sort.unsorted();
            for (String sortParam : sortBy) {
                String[] parts = sortParam.split(",");
                String field = parts[0];
                Sort.Direction direction = Sort.Direction.ASC;
                if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) {
                    direction = Sort.Direction.DESC;
                }
                sort = sort.and(Sort.by(direction, field));
            }
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<CustomerResponseDTO> customerPage = customerService.getAllCustomers(pageable);
            return ResponseEntity.ok(customerPage);
        }
    }

    /**
     * 根据客户号获取客户信息
     * GET /api/customers/{cNo}
     * @param cNo 客户号
     * @return 客户信息
     */
    @GetMapping("/{cNo}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable String cNo) {
        return customerService.getCustomerById(cNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新客户
     * POST /api/customers
     * @param customerRequestDTO 客户请求DTO
     * @return 创建的客户信息
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        System.out.println("========== Controller 接收到的数据 ==========");
        System.out.println("RequestBody DTO: " + customerRequestDTO);
        System.out.println("DTO.getCName(): " + customerRequestDTO.getCName());
        System.out.println("DTO.getCNo(): " + customerRequestDTO.getCNo());
        System.out.println("==========================================");
        
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    /**
     * 更新客户信息
     * PUT /api/customers/{cNo}
     * @param cNo 客户号
     * @param customerRequestDTO 客户请求DTO
     * @return 更新后的客户信息
     */
    @PutMapping("/{cNo}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable String cNo,
            @RequestBody CustomerRequestDTO customerRequestDTO) {
        System.out.println("========== Controller 接收到的数据 ==========");
        System.out.println("PathVariable cNo: " + cNo);
        System.out.println("RequestBody DTO: " + customerRequestDTO);
        System.out.println("DTO.getCName(): " + customerRequestDTO.getCName());
        System.out.println("DTO.getCNo(): " + customerRequestDTO.getCNo());
        System.out.println("==========================================");
        
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(cNo, customerRequestDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * 删除客户
     * DELETE /api/customers/{cNo}
     * @param cNo 客户号
     * @return 无内容
     */
    @DeleteMapping("/{cNo}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String cNo) {
        customerService.deleteCustomer(cNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据姓名模糊查询客户
     * GET /api/customers/search/name?cName=xxx
     * @param cName 姓名关键字
     * @return 客户列表
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomersByName(@RequestParam String cName) {
        List<CustomerResponseDTO> customers = customerService.searchCustomersByName(cName);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据电话号码查询客户
     * GET /api/customers/search/phone?phone=xxx
     * @param phone 电话号码
     * @return 客户列表
     */
    @GetMapping("/search/phone")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomersByPhone(@RequestParam String phone) {
        List<CustomerResponseDTO> customers = customerService.searchCustomersByPhone(phone);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据公司名称查询客户（精确匹配）
     * GET /api/customers/company/{company}
     * @param company 公司名称
     * @return 客户列表
     */
    @GetMapping("/company/{company}")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByCompany(@PathVariable String company) {
        List<CustomerResponseDTO> customers = customerService.getCustomersByCompany(company);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据公司名称模糊查询客户
     * GET /api/customers/search/company?company=xxx
     * @param company 公司名称关键字
     * @return 客户列表
     */
    @GetMapping("/search/company")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomersByCompany(@RequestParam String company) {
        List<CustomerResponseDTO> customers = customerService.searchCustomersByCompany(company);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据地址模糊查询客户
     * GET /api/customers/search/address?address=xxx
     * @param address 地址关键字
     * @return 客户列表
     */
    @GetMapping("/search/address")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomersByAddress(@RequestParam String address) {
        List<CustomerResponseDTO> customers = customerService.searchCustomersByAddress(address);
        return ResponseEntity.ok(customers);
    }

    /**
     * 多条件查询客户
     * GET /api/customers/search?cName=xxx&phone=xxx&company=xxx
     * @param cName 客户姓名（可选）
     * @param phone 电话号码（可选）
     * @param company 公司名称（可选）
     * @return 客户列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(
            @RequestParam(required = false) String cName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String company) {
        List<CustomerResponseDTO> customers = customerService.searchCustomers(cName, phone, company);
        return ResponseEntity.ok(customers);
    }

    /**
     * 根据客户号模糊查询客户
     * GET /api/customers/search/cno?cno=xxx
     * @param cNo 客户号关键字
     * @return 客户列表
     */
    @GetMapping("/search/cno")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomersByCNo(@RequestParam String cNo) {
        List<CustomerResponseDTO> customers = customerService.searchCustomersByCNo(cNo);
        return ResponseEntity.ok(customers);
    }

    /**
     * 更新客户电话
     * PATCH /api/customers/{cNo}/phone
     * @param cNo 客户号
     * @param phoneRequest 包含新电话的请求体
     * @return 更新后的客户信息
     */
    @PatchMapping("/{cNo}/phone")
    public ResponseEntity<CustomerResponseDTO> updateCustomerPhone(
            @PathVariable String cNo,
            @RequestBody Map<String, String> phoneRequest) {
        String newPhone = phoneRequest.get("phone");
        CustomerResponseDTO updatedCustomer = customerService.updateCustomerPhone(cNo, newPhone);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * 更新客户地址
     * PATCH /api/customers/{cNo}/address
     * @param cNo 客户号
     * @param addressRequest 包含新地址的请求体
     * @return 更新后的客户信息
     */
    @PatchMapping("/{cNo}/address")
    public ResponseEntity<CustomerResponseDTO> updateCustomerAddress(
            @PathVariable String cNo,
            @RequestBody Map<String, String> addressRequest) {
        String newAddress = addressRequest.get("address");
        CustomerResponseDTO updatedCustomer = customerService.updateCustomerAddress(cNo, newAddress);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * 获取客户统计信息
     * GET /api/customers/statistics
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCustomerStatistics() {
        Map<String, Object> statistics = customerService.getCustomerStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 管理员专用：分页多条件搜索客户
     * POST /api/customers/admin/search
     * @param searchParams 搜索参数（name, phone, company, page, size）
     * @return 分页客户列表
     */
    @PostMapping("/admin/search")
    public ResponseEntity<Page<CustomerResponseDTO>> adminSearchCustomers(@RequestBody Map<String, Object> searchParams) {
        String name = (String) searchParams.get("name");
        String phone = (String) searchParams.get("phone");
        String company = (String) searchParams.get("company");
        int page = searchParams.get("page") != null ? (Integer) searchParams.get("page") : 0;
        int size = searchParams.get("size") != null ? (Integer) searchParams.get("size") : 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by("cNo").ascending());
        Page<CustomerResponseDTO> customerPage = customerService.searchCustomersWithPage(name, phone, company, pageable);
        return ResponseEntity.ok(customerPage);
    }
}