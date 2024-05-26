package ma.enset.backend.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentEntryDTO {
    private Long id;
    private String name;
    private LocalDateTime timestamp;
    private byte[] file;
    private List<List<String>> parsedData; // Add this field
}
