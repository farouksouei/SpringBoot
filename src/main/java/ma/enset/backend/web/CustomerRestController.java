package ma.enset.backend.web;

import lombok.AllArgsConstructor;
import ma.enset.backend.dtos.CustomerDTO;
import ma.enset.backend.dtos.DocumentEntryDTO;
import ma.enset.backend.entities.DocumentEntry;
import ma.enset.backend.entities.User;
import ma.enset.backend.exceptions.CustomerNotFoundException;
import ma.enset.backend.mappers.MeoMapper;
import ma.enset.backend.services.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ma.enset.backend.entities.User;
import ma.enset.backend.entities.Role;
import ma.enset.backend.services.UserService;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ma.enset.backend.repositories.DocumentEntryRepository;

@RestController
@CrossOrigin("*")
@AllArgsConstructor
public class CustomerRestController {
    private final DocumentEntryService documentEntryService;

    @Autowired
    private DocumentEntryRepository documentEntryRepository;

    @Autowired
    private MeoMapper bankMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;





    @PostMapping(path = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DocumentEntryDTO> uploadDocument(
            @RequestParam("name") String name,
            @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Ensure file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        DocumentEntryDTO documentEntryDTO = new DocumentEntryDTO();
        documentEntryDTO.setName(name);
        documentEntryDTO.setTimestamp(timestamp);
        documentEntryDTO.setFile(file.getBytes()); // Convert MultipartFile to byte[]

        DocumentEntryDTO savedDocument = documentEntryService.saveDocumentEntry(documentEntryDTO);
        return ResponseEntity.ok(savedDocument);
    }

    @GetMapping("/documents/{id}")
    @PreAuthorize("isAuthenticated()")
    public DocumentEntryDTO getDocument(@PathVariable String id) {
        System.out.println("id: " + id);
        DocumentEntry documentEntry = documentEntryRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("DocumentEntry not found for id: " + id));

        // Parse the Excel file
        List<List<String>> excelData = parseExcelFile(documentEntry.getFilePath());

        // Assuming you want to include the parsed data in the DTO
        DocumentEntryDTO documentEntryDTO = bankMapper.fromDocumentEntry(documentEntry);
        documentEntryDTO.setParsedData(excelData);

        return documentEntryDTO;
    }

    @GetMapping(path = "/documents")
    @PreAuthorize("isAuthenticated()")
    public List<DocumentEntryDTO> getDocuments() {
        return documentEntryService.viewDocumentEntries();
    }
    private List<List<String>> parseExcelFile(String filePath) {
        List<List<String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    rowData.add(cell.toString());
                }
                data.add(rowData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file", e);
        }

        return data;
    }


    @GetMapping(path ="/api/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path ="/api/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping(path ="/api/users")
    public User createUser(@RequestBody Map<String, Object> payload) {
        User user = new User();
        user.setUsername((String) payload.get("username"));
        user.setPassword((String) payload.get("password"));
        user.setEnabled((Boolean) payload.get("enabled"));

        List<Map<String, String>> rolesPayload = (List<Map<String, String>>) payload.get("roles");
        Set<Role> roles = rolesPayload.stream()
                .map(rolePayload -> {
                    String roleName = rolePayload.get("name");
                    Role role = roleService.findByName(roleName);
                    if (role == null) {
                        throw new RuntimeException("Role not found: " + roleName);
                    }
                    return role;
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return userService.saveUser(user);
    }

    @PutMapping(path ="/api/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new RuntimeException("User not found: " + id);
        }

        user.setUsername((String) payload.get("username"));
        user.setPassword((String) payload.get("password"));
        user.setEnabled((Boolean) payload.get("enabled"));

        List<Map<String, String>> rolesPayload = (List<Map<String, String>>) payload.get("roles");
        Set<Role> roles = rolesPayload.stream()
                .map(rolePayload -> {
                    String roleName = rolePayload.get("name");
                    Role role = roleService.findByName(roleName);
                    if (role == null) {
                        throw new RuntimeException("Role not found: " + roleName);
                    }
                    return role;
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return userService.saveUser(user);
    }

    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
