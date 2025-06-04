package com.company.internalmgmt.config;

import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.model.Role;
import com.company.internalmgmt.modules.admin.model.Permission;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import com.company.internalmgmt.modules.admin.repository.RoleRepository;
import com.company.internalmgmt.modules.admin.repository.PermissionRepository;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.model.OpportunityNote;
import com.company.internalmgmt.modules.opportunity.model.OpportunityAssignment;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityRepository;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityNoteRepository;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityAssignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.UUID;
import java.util.Random;

/**
 * Test data initializer for creating sample data in the database.
 * This class runs only in test and development environments.
 */
@Component
@Slf4j
public class TestDataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityNoteRepository opportunityNoteRepository;
    private final OpportunityAssignmentRepository opportunityAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestDataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            OpportunityRepository opportunityRepository,
            OpportunityNoteRepository opportunityNoteRepository,
            OpportunityAssignmentRepository opportunityAssignmentRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityNoteRepository = opportunityNoteRepository;
        this.opportunityAssignmentRepository = opportunityAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // @Override
    // @Transactional
    // public void run(String... args) {
    //     // Only run if the database is empty (no users)
    //     try {
    //         long userCount = userRepository.count();
    //         if (userCount == 0) {
    //             log.info("Initializing test data...");
    //             initializeTestData();
    //             log.info("Test data initialization completed successfully");
    //         } else {
    //             log.info("Skipping test data initialization as database already contains users");
    //         }
    //     } catch (Exception e) {
    //         log.error("Error initializing test data", e);
    //     }
    // }

    @Transactional
    public void initializeTestData() {
        log.info("Bắt đầu khởi tạo dữ liệu test...");
        
        // Create basic permissions
        Permission viewOpportunities = createPermission("opportunity:read:all", "Can view all opportunities");
        Permission createOpportunities = createPermission("opportunity:create", "Can create opportunities");
        Permission updateOpportunities = createPermission("opportunity:update:all", "Can update all opportunities");
        Permission updateOwnOpportunities = createPermission("opportunity:update:own", "Can update own opportunities");
        Permission updateAssignedOpportunities = createPermission("opportunity:update:assigned", "Can update assigned opportunities");
        Permission deleteOpportunities = createPermission("opportunity:delete", "Can delete opportunities");
        
        // Note permissions
        Permission viewNotes = createPermission("opportunity-note:read:all", "Can view all opportunity notes");
        Permission createNotes = createPermission("opportunity-note:create:all", "Can create opportunity notes");
        Permission updateNotes = createPermission("opportunity-note:update:all", "Can update all opportunity notes");
        Permission viewAssignedNotes = createPermission("opportunity-note:read:assigned", "Can view notes for assigned opportunities");
        Permission createAssignedNotes = createPermission("opportunity-note:create:assigned", "Can create notes for assigned opportunities");
        Permission updateAssignedNotes = createPermission("opportunity-note:update:assigned", "Can update notes for assigned opportunities");
        
        // Onsite priority permissions
        Permission updateOnsitePriority = createPermission("opportunity-onsite:update:all", "Can update onsite priority for all opportunities");
        Permission updateAssignedOnsitePriority = createPermission("opportunity-onsite:update:assigned", "Can update onsite priority for assigned opportunities");
        
        // Sync permissions
        Permission syncOpportunities = createPermission("opportunity:sync", "Can synchronize opportunities with Hubspot");
        Permission viewSyncLogs = createPermission("opportunity-log:read:all", "Can view opportunity sync logs");
        
        // Opportunity assignment permissions
        Permission assignLeaders = createPermission("opportunity-assign:update:all", "Can assign leaders to opportunities");
        
        log.info("Đã tạo xong các permission.");
        
        // Create roles
        Role adminRole = createRole("ROLE_ADMIN", "Administrator with full access", 
            new HashSet<>(Arrays.asList(
                viewOpportunities, createOpportunities, updateOpportunities, deleteOpportunities,
                viewNotes, createNotes, updateNotes,
                updateOnsitePriority, syncOpportunities, viewSyncLogs, assignLeaders
            )));
        
        Role salesRole = createRole("ROLE_SALES", "Sales representative", 
            new HashSet<>(Arrays.asList(
                viewOpportunities, createOpportunities, updateOwnOpportunities,
                viewNotes, createNotes, updateNotes
            )));
        
        Role leaderRole = createRole("ROLE_LEADER", "Team leader", 
            new HashSet<>(Arrays.asList(
                viewOpportunities, updateAssignedOpportunities,
                viewAssignedNotes, createAssignedNotes, updateAssignedNotes,
                updateAssignedOnsitePriority
            )));
            
        log.info("Đã tạo xong các role.");

        // Create users
        User adminUser = createUser("admin", "admin@company.com", "Admin User", 
                                  passwordEncoder.encode("password"), new HashSet<>(List.of(adminRole)));
        
        User salesUser = createUser("sales", "sales@company.com", "Sales User", 
                                  passwordEncoder.encode("password"), new HashSet<>(List.of(salesRole)));
        
        User leaderUser = createUser("leader", "leader@company.com", "Leader User", 
                                   passwordEncoder.encode("password"), new HashSet<>(List.of(leaderRole)));
                                   
        User salesManager = createUser("salesmanager", "salesmanager@company.com", "Sales Manager", 
                                   passwordEncoder.encode("password"), new HashSet<>(Arrays.asList(salesRole, leaderRole)));
                                   
        log.info("Đã tạo xong các user.");

        // Create opportunities
        Opportunity opp1 = createOpportunity(
            null, 
            "Website Redesign Project", 
            "Công ty ABC", 
            new BigDecimal("500000000"), 
            "VND", 
            "PROPOSAL", 
            "Manual", 
            salesUser, 
            LocalDateTime.now().minusDays(5),
            "Green", 
            false
        );
        
        Opportunity opp2 = createOpportunity(
            "HUB-001", 
            "Mobile App Development", 
            "Tập đoàn XYZ", 
            new BigDecimal("1200000000"), 
            "VND", 
            "DISCOVERY", 
            "Hubspot", 
            salesUser, 
            LocalDateTime.now().minusDays(10),
            "Yellow", 
            true
        );
        
        Opportunity opp3 = createOpportunity(
            null, 
            "Cloud Migration Services", 
            "Công ty Công nghệ DEF", 
            new BigDecimal("2500000000"), 
            "VND", 
            "CLOSED_WON", 
            "Manual", 
            salesUser, 
            LocalDateTime.now().minusDays(15),
            "Green", 
            true
        );
        
        Opportunity opp4 = createOpportunity(
            "HUB-002", 
            "ERP Implementation", 
            "Tập đoàn KLM", 
            new BigDecimal("3500000000"), 
            "VND", 
            "NEGOTIATION", 
            "Hubspot", 
            salesUser, 
            LocalDateTime.now().minusDays(3),
            "Red", 
            true
        );
        
        Opportunity opp5 = createOpportunity(
            null, 
            "Cybersecurity Assessment", 
            "Ngân hàng STU", 
            new BigDecimal("150000000"), 
            "VND", 
            "PROPOSAL", 
            "Manual", 
            salesUser, 
            LocalDateTime.now().minusDays(7),
            "Yellow", 
            false
        );
        
        Opportunity opp6 = createOpportunity(
            "HUB-003", 
            "Digital Transformation", 
            "Tổng công ty Viễn thông VN", 
            new BigDecimal("5000000000"), 
            "VND", 
            "INITIAL_CONTACT", 
            "Hubspot", 
            salesManager, 
            LocalDateTime.now().minusDays(2),
            "Yellow", 
            true
        );
        
        Opportunity opp7 = createOpportunity(
            null, 
            "AI Integration Project", 
            "Công ty Thương mại Điện tử QWE", 
            new BigDecimal("800000000"), 
            "VND", 
            "NEGOTIATION", 
            "Manual", 
            salesManager, 
            LocalDateTime.now().minusDays(4),
            "Green", 
            true
        );
        
        log.info("Đã tạo xong {} cơ hội kinh doanh.", 7);

        // Create opportunity assignments
        createOpportunityAssignment(opp1, leaderUser, "Technical Lead");
        createOpportunityAssignment(opp2, leaderUser, "Project Oversight");
        createOpportunityAssignment(opp3, leaderUser, "Delivery Manager");
        createOpportunityAssignment(opp4, adminUser, "Executive Sponsor");
        createOpportunityAssignment(opp6, salesManager, "Sales Manager");
        createOpportunityAssignment(opp7, salesManager, "Sales Manager");
        
        log.info("Đã tạo xong các phân công leader.");
        
        // Create opportunity notes
        createOpportunityNote(opp1, salesUser, "Buổi gặp ban đầu với khách hàng đã hoàn thành. Khách hàng quan tâm đến thiết kế hiện đại.");
        createOpportunityNote(opp1, leaderUser, "Yêu cầu kỹ thuật có vẻ khả thi với đội ngũ hiện tại của chúng ta.");
        createOpportunityNote(opp2, salesUser, "Khách hàng cần cả ứng dụng iOS và Android.");
        createOpportunityNote(opp3, salesUser, "Hợp đồng đã được ký kết. Dự án bắt đầu vào tháng tới.");
        createOpportunityNote(opp4, salesUser, "Khách hàng yêu cầu kế hoạch triển khai chi tiết.");
        createOpportunityNote(opp4, leaderUser, "Chúng ta cần chuẩn bị phân bổ nguồn lực cho dự án này.");
        createOpportunityNote(opp5, salesUser, "Khách hàng có mối quan tâm về bảo mật đối với hệ thống cũ của họ.");
        createOpportunityNote(opp6, salesManager, "Buổi gặp đầu tiên đã diễn ra tốt đẹp. Khách hàng tỏ ra rất hứng thú với giải pháp của chúng ta.");
        createOpportunityNote(opp7, salesManager, "Đã gửi báo giá cho khách hàng và đang chờ phản hồi. Dự kiến đàm phán vào tuần sau.");
        
        // Tạo các ghi chú với loại khác nhau
        createOpportunityNote(opp1, salesUser, "Cuộc họp bàn về chi tiết kỹ thuật với team kỹ thuật của khách hàng.", 
                            "meeting", LocalDateTime.now().minusDays(2), false);
        
        createOpportunityNote(opp2, salesUser, "Call với khách hàng về vấn đề cấu hình máy chủ.", 
                            "call", LocalDateTime.now().minusDays(1), false);
        
        createOpportunityNote(opp3, leaderUser, "Bổ sung nguồn lực cho dự án: 2 senior developer, 1 designer, 1 tester.", 
                            "note", null, true);
        
        createOpportunityNote(opp4, adminUser, "Chuẩn bị tài liệu hợp đồng chính thức để ký kết vào tuần sau.", 
                            "task", LocalDateTime.now().plusDays(7), false);
        
        createOpportunityNote(opp6, salesManager, "Buổi demo sản phẩm với khách hàng.", 
                            "meeting", LocalDateTime.now().plusDays(3), false);
        
        log.info("Đã tạo xong các ghi chú cho cơ hội.");
        log.info("Khởi tạo dữ liệu test hoàn tất!");
    }

    private Permission createPermission(String name, String description) {
        return permissionRepository.findByName(name).orElseGet(new Supplier<Permission>() {
            @Override
            public Permission get() {
                Permission permission = new Permission();
                permission.setName(name);
                permission.setDescription(description);
                return permissionRepository.save(permission);
            }
        });
    }

    private Role createRole(String name, String description, Set<Permission> permissions) {
        return roleRepository.findByName(name).orElseGet(new Supplier<Role>() {
            @Override
            public Role get() {
                Role role = new Role();
                role.setName(name);
                role.setDescription(description);
                role.setPermissions(permissions);
                return roleRepository.save(role);
            }
        });
    }

    private User createUser(String username, String email, String fullName, String passwordHash, Set<Role> roles) {
        return userRepository.findByUsername(username).orElseGet(new Supplier<User>() {
            @Override
            public User get() {
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPassword(passwordHash);
                user.setRoles(roles);
                user.setEnabled(true);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
        });
    }

    private Opportunity createOpportunity(String hubspotId, String name, String clientName, 
                                        BigDecimal estimatedValue, String currency, String dealStage,
                                        String source, User assignedSales, LocalDateTime lastInteractionDate,
                                        String followUpStatus, Boolean onsitePriority) {
        // Tạo ID ngẫu nhiên cho Hubspot ID để tránh trùng lặp
        if (hubspotId != null && hubspotId.startsWith("HUB-")) {
            hubspotId = hubspotId + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        // Tạo mã code
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String code = "OPP-" + datePrefix + "-" + String.format("%05d", new Random().nextInt(99999) + 1);
        
        Opportunity opportunity = new Opportunity();
        opportunity.setHubspotId(hubspotId);
        opportunity.setCode(code); // Thêm mã code
        opportunity.setName(name);
        opportunity.setClientName(clientName);
        opportunity.setAmount(estimatedValue); // Sử dụng amount thay vì estimatedValue
        opportunity.setCurrency(currency);
        opportunity.setStatus(dealStage); // Sử dụng status thay vì dealStage
        opportunity.setSource(source);
        opportunity.setAssignedTo(assignedSales); // Sử dụng assignedTo thay vì assignedSales
        opportunity.setLastInteractionDate(lastInteractionDate);
        opportunity.setFollowUpStatus(followUpStatus);
        opportunity.setPriority(onsitePriority); // Sử dụng priority thay vì onsitePriority
        opportunity.setDealSize(getDealSizeCategory(estimatedValue)); // Tính toán dealSize dựa trên amount
        
        // Thêm thông tin mô tả và liên hệ khách hàng
        opportunity.setDescription("Đây là cơ hội " + name + " với " + clientName);
        opportunity.setClientContact(generateRandomContact(clientName));
        opportunity.setClientEmail(generateRandomEmail(clientName));
        opportunity.setClientPhone(generateRandomPhone());
        
        // Set audit fields
        opportunity.setCreatedAt(LocalDateTime.now().minusDays(20));
        opportunity.setUpdatedAt(LocalDateTime.now().minusDays(5));
        
        // Set Hubspot sync fields if applicable
        if (hubspotId != null) {
            opportunity.setHubspotCreatedAt(LocalDateTime.now().minusDays(30));
            opportunity.setHubspotLastUpdatedAt(LocalDateTime.now().minusDays(10));
            opportunity.setSyncStatus("Success");
            opportunity.setLastSyncAt(LocalDateTime.now().minusDays(2));
        }
        
        return opportunityRepository.save(opportunity);
    }
    
    /**
     * Xác định kích thước deal dựa trên số tiền
     */
    private String getDealSizeCategory(BigDecimal amount) {
        if (amount == null) {
            return "small";
        }
        
        if (amount.compareTo(new BigDecimal("1000000000")) >= 0) {
            return "extra_large";  // >= 1 tỷ
        } else if (amount.compareTo(new BigDecimal("500000000")) >= 0) {
            return "large";        // >= 500 triệu
        } else if (amount.compareTo(new BigDecimal("100000000")) >= 0) {
            return "medium";       // >= 100 triệu
        } else {
            return "small";        // < 100 triệu
        }
    }
    
    /**
     * Tạo thông tin liên hệ ngẫu nhiên
     */
    private String generateRandomContact(String clientName) {
        String[] firstNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng"};
        String[] lastNames = {"Văn", "Thị", "Đức", "Minh", "Quang", "Thanh", "Hồng", "Tuấn", "Mai", "Ngọc"};
        String[] middleNames = {"An", "Bình", "Cường", "Dũng", "Hà", "Hải", "Hùng", "Long", "Phương", "Tâm"};
        
        String firstName = firstNames[new Random().nextInt(firstNames.length)];
        String lastName = lastNames[new Random().nextInt(lastNames.length)];
        String middleName = middleNames[new Random().nextInt(middleNames.length)];
        
        return firstName + " " + lastName + " " + middleName + " (" + clientName + ")";
    }
    
    /**
     * Tạo email ngẫu nhiên
     */
    private String generateRandomEmail(String clientName) {
        String[] domains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "company.com"};
        String sanitizedName = clientName.toLowerCase().replaceAll("[^a-z0-9]", "");
        
        String domain = domains[new Random().nextInt(domains.length)];
        return "contact." + sanitizedName + "@" + domain;
    }
    
    /**
     * Tạo số điện thoại ngẫu nhiên
     */
    private String generateRandomPhone() {
        String[] prefixes = {"09", "08", "07", "03", "05"};
        String prefix = prefixes[new Random().nextInt(prefixes.length)];
        
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            sb.append(new Random().nextInt(10));
        }
        
        return sb.toString();
    }

    private OpportunityAssignment createOpportunityAssignment(Opportunity opportunity, User leader, String assignmentRole) {
        // Cần chuyển đổi User sang Employee trước khi tạo OpportunityAssignment
        // Giả định rằng có một phương thức để lấy Employee từ User
        Employee employee = findEmployeeFromUser(leader);
        
        OpportunityAssignment assignment = new OpportunityAssignment();
        assignment.setOpportunity(opportunity);
        assignment.setEmployee(employee);
        assignment.setAssignedAt(LocalDateTime.now().minusDays(5));
        assignment.setCreatedAt(LocalDateTime.now().minusDays(5));
        assignment.setUpdatedAt(LocalDateTime.now().minusDays(5));
        return opportunityAssignmentRepository.save(assignment);
    }


    private OpportunityNote createOpportunityNote(Opportunity opportunity, User author, String content) {
        return createOpportunityNote(opportunity, author, content, "note", null, false);
    }
    
    private OpportunityNote createOpportunityNote(Opportunity opportunity, User author, String content, 
                                                String activityType, LocalDateTime meetingDate, Boolean isPrivate) {
        OpportunityNote note = new OpportunityNote();
        note.setOpportunity(opportunity);
        note.setAuthor(author);
        note.setContent(content);
        note.setActivityType(activityType);
        note.setMeetingDate(meetingDate);
        note.setIsPrivate(isPrivate);
        note.setCreatedAt(LocalDateTime.now().minusDays(3));
        note.setUpdatedAt(LocalDateTime.now().minusDays(3));
        return opportunityNoteRepository.save(note);
    }

    // Phương thức để tìm Employee từ User
    private Employee findEmployeeFromUser(User user) {
        // Cần phải thực hiện logic tìm kiếm employee dựa trên user
        // Ví dụ: Có thể employee có liên kết với user thông qua userId hoặc email
        
        // Tìm theo userId
        return employeeRepository.findByUserId(user.getId())
            .orElse(null);
        
        // Hoặc có thể tìm theo email
        // return employeeRepository.findByCompanyEmail(user.getEmail())
        //    .orElse(null);
    }
} 