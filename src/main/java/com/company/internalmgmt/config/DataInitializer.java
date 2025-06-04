// package com.company.internalmgmt.config;

// import com.company.internalmgmt.modules.admin.model.User;
// import com.company.internalmgmt.modules.admin.model.Role;
// import com.company.internalmgmt.modules.admin.model.Permission;
// import com.company.internalmgmt.modules.admin.repository.UserRepository;
// import com.company.internalmgmt.modules.admin.repository.RoleRepository;
// import com.company.internalmgmt.modules.admin.repository.PermissionRepository;
// import com.company.internalmgmt.modules.hrm.model.Employee;
// import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
// import com.company.internalmgmt.modules.contract.model.Contract;
// import com.company.internalmgmt.modules.contract.repository.ContractRepository;
// import com.company.internalmgmt.modules.opportunity.model.Opportunity;
// import com.company.internalmgmt.modules.opportunity.repository.OpportunityRepository;
// import com.company.internalmgmt.modules.margin.model.EmployeeCost;
// import com.company.internalmgmt.modules.margin.model.EmployeeRevenue;
// import com.company.internalmgmt.modules.margin.repository.EmployeeCostRepository;
// import com.company.internalmgmt.modules.margin.repository.EmployeeRevenueRepository;
// import com.company.internalmgmt.modules.contract.repository.ContractEmployeeRepository;
// import com.company.internalmgmt.modules.contract.model.ContractEmployee;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.time.OffsetDateTime;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;
// import java.time.Instant;
// import java.util.Map;
// import java.util.HashMap;
// import java.util.stream.Collectors;

// /**
//  * Data initializer for creating sample data in the database
//  * This class will run on application startup to populate initial data
//  */
// @Component
// public class DataInitializer

//     private final UserRepository userRepository;
//     private final RoleRepository roleRepository;
//     private final PermissionRepository permissionRepository;
//     private final EmployeeRepository employeeRepository;
//     private final ContractRepository contractRepository;
//     private final OpportunityRepository opportunityRepository;
//     private final EmployeeCostRepository employeeCostRepository;
//     private final EmployeeRevenueRepository employeeRevenueRepository;
//     private final ContractEmployeeRepository contractEmployeeRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Autowired
//     public DataInitializer(UserRepository userRepository,
//                           RoleRepository roleRepository,
//                           PermissionRepository permissionRepository,
//                           EmployeeRepository employeeRepository,
//                           ContractRepository contractRepository,
//                           OpportunityRepository opportunityRepository,
//                           EmployeeCostRepository employeeCostRepository,
//                           EmployeeRevenueRepository employeeRevenueRepository,
//                           ContractEmployeeRepository contractEmployeeRepository,
//                           PasswordEncoder passwordEncoder) {
//         this.userRepository = userRepository;
//         this.roleRepository = roleRepository;
//         this.permissionRepository = permissionRepository;
//         this.employeeRepository = employeeRepository;
//         this.contractRepository = contractRepository;
//         this.opportunityRepository = opportunityRepository;
//         this.employeeCostRepository = employeeCostRepository;
//         this.employeeRevenueRepository = employeeRevenueRepository;
//         this.contractEmployeeRepository = contractEmployeeRepository;
//         this.passwordEncoder = passwordEncoder;
//     }

//     @Override
//     @Transactional
//     public void run(String... args) throws Exception {
//         // Initialize roles and permissions definitions
//         initializeRolePermissionsFromDefinition();
//         // Only run if database is empty
//         if (userRepository.count() == 0) {
//             initializeData();
//         }
//     }

//     private void initializeData() {
//         // Create permissions
//         Permission viewEmployees = createPermission("VIEW_EMPLOYEES", "Can view employees");
//         Permission editEmployees = createPermission("EDIT_EMPLOYEES", "Can edit employees");
//         Permission viewContracts = createPermission("VIEW_CONTRACTS", "Can view contracts");
//         Permission editContracts = createPermission("EDIT_CONTRACTS", "Can edit contracts");
//         Permission viewOpportunities = createPermission("VIEW_OPPORTUNITIES", "Can view opportunities");
//         Permission editOpportunities = createPermission("EDIT_OPPORTUNITIES", "Can edit opportunities");
//         Permission viewMargin = createPermission("VIEW_MARGIN", "Can view margin data");
//         Permission editMargin = createPermission("EDIT_MARGIN", "Can edit margin data");

//         // Create roles
//         Role adminRole = createRole("ROLE_ADMIN", "Administrator with full access", 
//             new HashSet<>(Arrays.asList(viewEmployees, editEmployees, viewContracts, 
//                                        editContracts, viewOpportunities, editOpportunities,
//                                        viewMargin, editMargin)));
        
//         Role managerRole = createRole("ROLE_MANAGER", "Department manager", 
//             new HashSet<>(Arrays.asList(viewEmployees, viewContracts, viewOpportunities, viewMargin)));
        
//         Role salesRole = createRole("ROLE_SALES", "Sales representative", 
//             new HashSet<>(Arrays.asList(viewEmployees, viewContracts, viewOpportunities, 
//                                        editOpportunities)));

//         // Create users
//         User adminUser = createUser("admin", "admin@company.com", "Admin User", 
//                                   passwordEncoder.encode("password"), new HashSet<>(List.of(adminRole)));
        
//         User managerUser = createUser("manager", "manager@company.com", "Manager User", 
//                                     passwordEncoder.encode("password"), new HashSet<>(List.of(managerRole)));
        
//         User salesUser = createUser("sales", "sales@company.com", "Sales User", 
//                                   passwordEncoder.encode("password"), new HashSet<>(List.of(salesRole)));
        
//         User regularUser = createUser("user", "user@company.com", "Regular User", 
//                                     passwordEncoder.encode("password"), new HashSet<>());

//         // Create employees
//         Employee admin = createEmployee(adminUser, "EMP001", "Admin", "User", LocalDate.of(1980, 1, 15), 
//                                       LocalDate.of(2020, 1, 1), "admin@company.com", "admin",
//                                       "123 Admin St", "+84123456789", "Emergency: +84987654321",
//                                       "CTO", "Management", null, "Allocated", null);
        
//         Employee manager = createEmployee(managerUser, "EMP002", "Manager", "User", LocalDate.of(1985, 5, 20), 
//                                        LocalDate.of(2020, 3, 15), "manager@company.com", "manager",
//                                        "456 Manager Ave", "+84123456788", "Emergency: +84987654322",
//                                        "Project Manager", "Management", admin.getId(), "Allocated", null);
        
//         Employee sales = createEmployee(salesUser, "EMP003", "Sales", "User", LocalDate.of(1990, 8, 25), 
//                                      LocalDate.of(2021, 1, 10), "sales@company.com", "sales",
//                                      "789 Sales Blvd", "+84123456787", "Emergency: +84987654323",
//                                      "Sales Executive", "Sales", manager.getId(), "Available", null);
        
//         Employee dev1 = createEmployee(regularUser, "EMP004", "Developer", "One", LocalDate.of(1995, 3, 12), 
//                                     LocalDate.of(2021, 6, 1), "dev1@company.com", "dev1",
//                                     "101 Developer Lane", "+84123456786", "Emergency: +84987654324",
//                                     "Senior Developer", "Development", manager.getId(), "Allocated", null);
        
//         Employee dev2 = createEmployee(null, "EMP005", "Developer", "Two", LocalDate.of(1997, 7, 22), 
//                                     LocalDate.of(2022, 1, 15), "dev2@company.com", "dev2",
//                                     "202 Developer Lane", "+84123456785", "Emergency: +84987654325",
//                                     "Developer", "Development", manager.getId(), "Available", null);

//         // Create opportunities
//         Opportunity opp1 = createOpportunity(null, "Website Redesign", "Client A", 
//                                           new BigDecimal("50000"), "USD", "PROPOSAL", 
//                                           "Manual", sales.getUser().getId(), LocalDateTime.now().minusDays(5),
//                                           "Green", false, null);
        
//         Opportunity opp2 = createOpportunity("HUB123", "Mobile App Development", "Client B", 
//                                           new BigDecimal("120000"), "USD", "DISCOVERY", 
//                                           "Hubspot", sales.getUser().getId(), LocalDateTime.now().minusDays(10),
//                                           "Yellow", true, LocalDateTime.now().minusDays(15));

//         // Create contracts
//         Contract contract1 = createContract("CTR001", "Website Redesign Project", "Client A", 
//                                           opp1.getId(), LocalDate.now().minusDays(30), 
//                                           LocalDate.now().minusDays(20), LocalDate.now().plusMonths(6),
//                                           new BigDecimal("50000"), "USD", "FixedPrice", 
//                                           sales.getUser().getId(), "Ongoing", "Full website redesign");
        
//         Contract contract2 = createContract("CTR002", "Mobile App Development", "Client B", 
//                                           opp2.getId(), LocalDate.now().minusDays(15), 
//                                           LocalDate.now().minusDays(10), LocalDate.now().plusMonths(8),
//                                           new BigDecimal("120000"), "USD", "TM", 
//                                           sales.getUser().getId(), "Ongoing", "New mobile app development");
        
//         Contract contract3 = createContract("CTR003", "DevOps Consulting", "Client C", 
//                                           null, LocalDate.now().minusDays(60), 
//                                           LocalDate.now().minusDays(45), LocalDate.now().plusMonths(3),
//                                           new BigDecimal("30000"), "USD", "TM", 
//                                           sales.getUser().getId(), "Ongoing", "DevOps consulting services");

//         // Create contract employee assignments
//         createContractEmployee(contract1.getId(), dev1.getId(), 
//                               LocalDate.now().minusDays(20), LocalDate.now().plusMonths(6),
//                               new BigDecimal("100"), "USD", new BigDecimal("100"));
        
//         createContractEmployee(contract2.getId(), dev1.getId(), 
//                               LocalDate.now().minusDays(10), LocalDate.now().plusMonths(8),
//                               new BigDecimal("50"), "USD", new BigDecimal("50"));
        
//         createContractEmployee(contract2.getId(), dev2.getId(), 
//                               LocalDate.now().minusDays(10), LocalDate.now().plusMonths(8),
//                               new BigDecimal("100"), "USD", new BigDecimal("80"));
        
//         createContractEmployee(contract3.getId(), manager.getId(), 
//                               LocalDate.now().minusDays(45), LocalDate.now().plusMonths(3),
//                               new BigDecimal("25"), "USD", new BigDecimal("100"));

//         // Create employee costs
//         createEmployeeCost(dev1.getId(), 2023, 7, new BigDecimal("1500"), "USD", "Monthly cost");
//         createEmployeeCost(dev1.getId(), 2023, 8, new BigDecimal("1500"), "USD", "Monthly cost");
//         createEmployeeCost(dev2.getId(), 2023, 7, new BigDecimal("1200"), "USD", "Monthly cost");
//         createEmployeeCost(dev2.getId(), 2023, 8, new BigDecimal("1200"), "USD", "Monthly cost");
//         createEmployeeCost(manager.getId(), 2023, 7, new BigDecimal("2500"), "USD", "Monthly cost");
//         createEmployeeCost(manager.getId(), 2023, 8, new BigDecimal("2500"), "USD", "Monthly cost");

//         // Create employee revenues
//         createEmployeeRevenue(dev1.getId(), contract1.getId(), 2023, 7, 
//                              new BigDecimal("35"), new BigDecimal("100"),
//                              new BigDecimal("5425"), "USD");
        
//         createEmployeeRevenue(dev1.getId(), contract1.getId(), 2023, 8, 
//                              new BigDecimal("35"), new BigDecimal("100"),
//                              new BigDecimal("5600"), "USD");
        
//         createEmployeeRevenue(dev1.getId(), contract2.getId(), 2023, 7, 
//                              new BigDecimal("40"), new BigDecimal("50"),
//                              new BigDecimal("3100"), "USD");
        
//         createEmployeeRevenue(dev2.getId(), contract2.getId(), 2023, 7, 
//                              new BigDecimal("30"), new BigDecimal("80"),
//                              new BigDecimal("3720"), "USD");
        
//         createEmployeeRevenue(manager.getId(), contract3.getId(), 2023, 7, 
//                              new BigDecimal("60"), new BigDecimal("25"),
//                              new BigDecimal("2325"), "USD");
//     }

//     private Permission createPermission(String name, String description) {
//         Permission permission = new Permission();
//         permission.setName(name);
//         permission.setDescription(description);
//         return permissionRepository.save(permission);
//     }

//     private Role createRole(String name, String description, Set<Permission> permissions) {
//         Role role = new Role();
//         role.setName(name);
//         role.setDescription(description);
//         role.setPermissions(permissions);
//         return roleRepository.save(role);
//     }

//     private User createUser(String username, String email, String fullName, String passwordHash, Set<Role> roles) {
//         User user = new User();
//         user.setUsername(username);
//         user.setEmail(email);
//         user.setFullName(fullName);
//         user.setPasswordHash(passwordHash);
//         user.setRoles(roles);
//         user.setCreatedAt(LocalDateTime.now());
//         user.setUpdatedAt(LocalDateTime.now());
//         return userRepository.save(user);
//     }

//     private Employee createEmployee(User user, String employeeCode, String firstName, String lastName, 
//                                   LocalDate birthDate, LocalDate hireDate, String companyEmail, 
//                                   String internalAccount, String address, String phoneNumber, 
//                                   String emergencyContact, String position, String team, 
//                                   Long reportingLeaderId, String currentStatus, String profilePictureUrl) {
//         Employee employee = new Employee();
//         if (user != null) {
//             employee.setUser(user);
//         }
//         employee.setEmployeeCode(employeeCode);
//         employee.setFirstName(firstName);
//         employee.setLastName(lastName);
//         employee.setBirthDate(birthDate);
//         employee.setHireDate(hireDate);
//         employee.setCompanyEmail(companyEmail);
//         employee.setInternalAccount(internalAccount);
//         employee.setAddress(address);
//         employee.setPhoneNumber(phoneNumber);
//         employee.setEmergencyContact(emergencyContact);
//         employee.setPosition(position);
//         employee.setTeam(team);
//         if (reportingLeaderId != null) {
//             User leader = userRepository.findById(reportingLeaderId)
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid leader ID: " + reportingLeaderId));
//             employee.setReportingLeader(leader);
//         }
//         employee.setCurrentStatus(currentStatus);
//         employee.setStatusUpdatedAt(OffsetDateTime.now());
//         employee.setProfilePictureUrl(profilePictureUrl);
//         employee.setCreatedAt(OffsetDateTime.now());
//         employee.setUpdatedAt(OffsetDateTime.now());
//         return employeeRepository.save(employee);
//     }

//     private Opportunity createOpportunity(String hubspotId, String name, String clientName, 
//                                         BigDecimal estimatedValue, String currency, String dealStage,
//                                         String source, Long assignedSalesId, LocalDateTime lastInteractionDate,
//                                         String followUpStatus, Boolean onsitePriority, LocalDateTime hubspotCreatedAt) {
//         Opportunity opportunity = new Opportunity();
//         opportunity.setHubspotId(hubspotId);
//         opportunity.setName(name);
//         opportunity.setClientName(clientName);
//         opportunity.setEstimatedValue(estimatedValue);
//         opportunity.setCurrency(currency);
//         opportunity.setDealStage(Opportunity.DealStage.fromValue(dealStage));
//         opportunity.setSource(source);
//         opportunity.setAssignedSalesId(assignedSalesId);
//         opportunity.setLastInteractionDate(lastInteractionDate);
//         opportunity.setFollowUpStatus(followUpStatus);
//         opportunity.setOnsitePriority(onsitePriority);
//         opportunity.setHubspotCreatedAt(hubspotCreatedAt);
//         if (hubspotCreatedAt != null) {
//             opportunity.setHubspotLastUpdatedAt(hubspotCreatedAt.plusDays(1));
//             opportunity.setSyncStatus("Success");
//             opportunity.setLastSyncAt(LocalDateTime.now().minusDays(1));
//         }
//         opportunity.setCreatedAt(LocalDateTime.now());
//         opportunity.setUpdatedAt(LocalDateTime.now());
//         return opportunityRepository.save(opportunity);
//     }

//     private Contract createContract(String contractCode, String name, String clientName, 
//                                    Long opportunityId, LocalDate signDate, LocalDate effectiveDate,
//                                    LocalDate expiryDate, BigDecimal totalValue, String currency,
//                                    String contractType, Long assignedSalesId, String status, String description) {
//         Contract contract = new Contract();
//         contract.setContractCode(contractCode);
//         contract.setName(name);
//         contract.setClientName(clientName);
//         if (opportunityId != null) {
//             Opportunity opportunity = opportunityRepository.findById(opportunityId)
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid opportunity ID: " + opportunityId));
//             contract.setOpportunity(opportunity);
//         }
//         contract.setSignDate(signDate);
//         contract.setEffectiveDate(effectiveDate);
//         contract.setExpiryDate(expiryDate);
//         contract.setTotalValue(totalValue);
//         contract.setCurrency(currency);
//         contract.setContractType(contractType);
//         if (assignedSalesId != null) {
//             User assignedSales = userRepository.findById(assignedSalesId)
//                 .orElseThrow(() -> new IllegalArgumentException("Invalid sales ID: " + assignedSalesId));
//             contract.setAssignedSales(assignedSales);
//         }
//         contract.setStatus(status);
//         contract.setDescription(description);
//         contract.setCreatedAt(LocalDateTime.now());
//         contract.setUpdatedAt(LocalDateTime.now());
//         return contractRepository.save(contract);
//     }

//     private ContractEmployee createContractEmployee(Long contractId, Long employeeId, 
//                                                   LocalDate startDate, LocalDate endDate,
//                                                   BigDecimal allocation, String currency, BigDecimal rate) {
//         ContractEmployee contractEmployee = new ContractEmployee();
//         Contract contract = contractRepository.findById(contractId)
//             .orElseThrow(() -> new IllegalArgumentException("Invalid contract ID: " + contractId));
//         Employee employee = employeeRepository.findById(employeeId)
//             .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + employeeId));
//         contractEmployee.setContract(contract);
//         contractEmployee.setEmployee(employee);
//         contractEmployee.setStartDate(startDate);
//         contractEmployee.setEndDate(endDate);
//         contractEmployee.setAllocationPercentage(allocation);
//         contractEmployee.setCurrency(currency);
//         contractEmployee.setRate(rate);
//         contractEmployee.setCreatedAt(LocalDateTime.now());
//         contractEmployee.setUpdatedAt(LocalDateTime.now());
//         return contractEmployeeRepository.save(contractEmployee);
//     }

//     private EmployeeCost createEmployeeCost(Long employeeId, int year, int month, 
//                                           BigDecimal costAmount, String currency, String description) {
//         EmployeeCost employeeCost = new EmployeeCost();
//         Employee employee = employeeRepository.findById(employeeId)
//             .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + employeeId));
//         employeeCost.setEmployee(employee);
//         employeeCost.setYear(year);
//         employeeCost.setMonth(month);
//         employeeCost.setCostAmount(costAmount);
//         employeeCost.setCurrency(currency);
//         employeeCost.setDescription(description);
//         employeeCost.setCreatedAt(Instant.now());
//         employeeCost.setUpdatedAt(Instant.now());
//         return employeeCostRepository.save(employeeCost);
//     }

//     private EmployeeRevenue createEmployeeRevenue(Long employeeId, Long contractId, int year, int month,
//                                                 BigDecimal billingRate, BigDecimal allocationPercentage,
//                                                 BigDecimal calculatedRevenue, String currency) {
//         EmployeeRevenue employeeRevenue = new EmployeeRevenue();
//         Employee employee = employeeRepository.findById(employeeId)
//             .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + employeeId));
//         Contract contract = contractRepository.findById(contractId)
//             .orElseThrow(() -> new IllegalArgumentException("Invalid contract ID: " + contractId));
//         employeeRevenue.setEmployee(employee);
//         employeeRevenue.setContract(contract);
//         employeeRevenue.setYear(year);
//         employeeRevenue.setMonth(month);
//         employeeRevenue.setBillingRate(billingRate);
//         employeeRevenue.setAllocationPercentage(allocationPercentage);
//         employeeRevenue.setCalculatedRevenue(calculatedRevenue);
//         employeeRevenue.setCurrency(currency);
//         employeeRevenue.setCreatedAt(Instant.now());
//         employeeRevenue.setUpdatedAt(Instant.now());
//         return employeeRevenueRepository.save(employeeRevenue);
//     }

//     // Initialize default roles and permissions based on permissions_definition.md
//     private void initializeRolePermissionsFromDefinition() {
//         // Define roles and their descriptions
//         Map<String, String> roleDescriptions = new HashMap<>();
//         roleDescriptions.put("ROLE_ADMIN", "Quản trị viên hệ thống với toàn quyền");
//         roleDescriptions.put("ROLE_DIVISION_MANAGER", "Quản lý bộ phận/phòng ban");
//         roleDescriptions.put("ROLE_LEADER", "Trưởng nhóm/Team leader");
//         roleDescriptions.put("ROLE_SALES", "Nhân viên kinh doanh");
//         roleDescriptions.put("ROLE_EMPLOYEE", "Nhân viên thông thường");

//         // Define all available permissions based on permissions_definition.md
//         List<String> allPermissions = List.of(
//             "employee:read:all", "employee:read:team", "employee:read:basic", "employee:read:own",
//             "employee:create", "employee:update:all", "employee:update:team", "employee:update:own",
//             "employee:delete", "employee:import", "employee:export",
//             "skill-category:read", "skill-category:create", "skill-category:update", "skill-category:delete",
//             "skill:read", "skill:create", "skill:update", "skill:delete",
//             "employee-skill:read:all", "employee-skill:read:team", "employee-skill:read:own",
//             "employee-skill:create:all", "employee-skill:create:team", "employee-skill:create:own",
//             "employee-skill:update:all", "employee-skill:update:team", "employee-skill:update:own",
//             "employee-skill:delete:all", "employee-skill:delete:team", "employee-skill:delete:own",
//             "employee-skill:evaluate", "employee-suggest:read",
//             "employee-status:read:all", "employee-status:read:team", "employee-status:read:own",
//             "employee-status:update:all", "employee-status:update:team",
//             "project-history:read:all", "project-history:read:team", "project-history:read:own",
//             "utilization:read:all", "utilization:read:team",
//             "employee-alert:read:all", "employee-alert:read:team",
//             "employee-cost:read:all", "employee-cost:read:team", "employee-cost:create", "employee-cost:update:all",
//             "employee-cost:delete", "employee-cost:import",
//             "revenue:read:all", "revenue:read:team",
//             "margin:read:all", "margin:read:team",
//             "margin-summary:read:all", "margin-summary:read:team",
//             "margin-alert:read:all", "margin-alert:read:team", "margin-alert:config",
//             "opportunity:read:all", "opportunity:create", "opportunity:update:all", "opportunity:update:own",
//             "opportunity:update:assigned", "opportunity:delete", "opportunity-sync:read",
//             "opportunity-sync:config", "opportunity-log:read:all", "opportunity-assign:update:all",
//             "opportunity-note:create:all", "opportunity-note:create:assigned",
//             "opportunity-note:read:all", "opportunity-note:read:assigned",
//             "opportunity-followup:read:all", "opportunity-onsite:update:all",
//             "opportunity-onsite:update:assigned", "opportunity-alert:config",
//             "opportunity-alert:read:all", "opportunity-alert:read:assigned",
//             "contract:read:all", "contract:read:own", "contract:read:assigned", "contract:create",
//             "contract:update:all", "contract:update:own", "contract:delete",
//             "contract-link:update:all", "contract-link:update:own", "contract-link:update:assigned",
//             "contract-file:read:all", "contract-file:read:own", "contract-file:read:assigned",
//             "contract-file:create:all", "contract-file:create:own",
//             "contract-file:delete:all", "contract-file:delete:own",
//             "payment-term:read:all", "payment-term:read:own", "payment-term:read:assigned",
//             "payment-term:create:all", "payment-term:create:own",
//             "payment-term:update:all", "payment-term:update:own",
//             "payment-term:delete:all", "payment-term:delete:own",
//             "payment-status:update:all", "payment-status:import",
//             "payment-alert:read:all", "payment-alert:read:own", "payment-alert:read:assigned",
//             "payment-alert:config", "debt-report:read:all", "debt-report:read:own",
//             "sales-kpi:read:all", "sales-kpi:read:own", "sales-kpi:create",
//             "sales-kpi:update", "sales-kpi:delete",
//             "revenue-report:read:all", "revenue-report:read:team", "revenue-report:read:own",
//             "revenue-summary:read:all", "revenue-summary:read:team", "revenue-summary:read:own",
//             "dashboard:read:all", "dashboard:read:team", "dashboard:read:own",
//             "report:read:all", "report:read:team", "report:read:own", "report:export",
//             "user:read", "user:create", "user:update", "user:delete",
//             "role:read", "role:create", "role:update", "role:delete",
//             "permission:read", "permission:assign",
//             "config:read", "config:update",
//             "alert-threshold:read", "alert-threshold:update",
//             "api-connect:read", "api-connect:update",
//             "system-log:read:all", "system-log:read:limited"
//         );

//         // Define permissions for each role
//         Map<String, List<String>> rolePermissions = new HashMap<>();
//         rolePermissions.put("ROLE_ADMIN", allPermissions);
//         rolePermissions.put("ROLE_DIVISION_MANAGER", allPermissions.stream()
//             .filter(p -> p.endsWith(":all") || !p.contains(":"))
//             .collect(Collectors.toList()));
//         rolePermissions.put("ROLE_LEADER", List.of(
//             "employee:read:team", "employee:update:team",
//             "employee-skill:read:team", "employee-skill:evaluate",
//             "opportunity:read:all", "opportunity:update:assigned",
//             "dashboard:read:team", "report:read:team", "report:export"
//         ));
//         rolePermissions.put("ROLE_SALES", List.of(
//             "employee:read:own", "employee:update:own",
//             "opportunity:create", "opportunity:update:own",
//             "contract:read:own", "contract:create", "contract:update:own",
//             "contract-link:update:own", "contract-file:create:own", "payment-term:create:own",
//             "payment-status:update:all", "payment-alert:read:own",
//             "sales-kpi:read:own", "revenue-report:read:own", "revenue-summary:read:own"
//         ));
//         rolePermissions.put("ROLE_EMPLOYEE", List.of(
//             "employee:read:own", "employee:update:own"
//         ));

//         // Create permissions if not exist
//         for (String permName : allPermissions) {
//             permissionRepository.findByName(permName).orElseGet(() -> {
//                 Permission perm = new Permission();
//                 perm.setName(permName);
//                 perm.setDescription(permName);
//                 return permissionRepository.save(perm);
//             });
//         }

//         // Create or update roles with permissions
//         for (Map.Entry<String, List<String>> entry : rolePermissions.entrySet()) {
//             String roleName = entry.getKey();
//             Role role = roleRepository.findByName(roleName).orElseGet(() -> {
//                 Role r = new Role();
//                 r.setName(roleName);
//                 r.setDescription(roleDescriptions.get(roleName));
//                 return r;
//             });
//             Set<Permission> perms = entry.getValue().stream()
//                 .map(name -> permissionRepository.findByName(name)
//                     .orElseThrow(() -> new IllegalStateException("Permission not found: " + name)))
//                 .collect(Collectors.toSet());
//             role.setPermissions(perms);
//             roleRepository.save(role);
//         }
//     }
// } 