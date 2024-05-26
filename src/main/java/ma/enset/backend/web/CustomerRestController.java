package ma.enset.backend.web;

import lombok.AllArgsConstructor;
import ma.enset.backend.dtos.CustomerDTO;
import ma.enset.backend.dtos.DocumentEntryDTO;
import ma.enset.backend.entities.DocumentEntry;
import ma.enset.backend.exceptions.CustomerNotFoundException;
import ma.enset.backend.mappers.BankMapper;
import ma.enset.backend.services.BankService;
import ma.enset.backend.services.DocumentEntryService;
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
    private final BankService bankService;
    private final DocumentEntryService documentEntryService;

    @Autowired
    private DocumentEntryRepository documentEntryRepository;

    @Autowired
    private BankMapper bankMapper;



    @GetMapping(path = "/customers")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<CustomerDTO> getCustomersDTO() {
        return bankService.getCustomersDTO();
    }

    @GetMapping(path = "/customers/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public CustomerDTO getCustomersDTO(@PathVariable Long id) throws CustomerNotFoundException {
        return bankService.getCustomerDTO(id);
    }

    @PostMapping(path = "/add-customer")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CustomerDTO saveCustomerDTO(@RequestBody CustomerDTO customerDTO) {
        return bankService.saveCustomerDTO(customerDTO);
    }

    @PutMapping(path = "/update-customer/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CustomerDTO updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        return bankService.updateCustomerDTO(id, customerDTO);
    }

    @DeleteMapping(path = "/delete-customer/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteCustomer(@PathVariable Long id) {
        bankService.deleteCustomer(id);
    }

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

    @GetMapping("/documents")
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

}
