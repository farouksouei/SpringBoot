package ma.enset.backend.services;

import ma.enset.backend.dtos.DocumentEntryDTO;
import ma.enset.backend.entities.DocumentEntry;
import ma.enset.backend.mappers.BankMapper;
import ma.enset.backend.repositories.DocumentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentEntryService {

    @Autowired
    private DocumentEntryRepository documentEntryRepository;

    @Autowired
    private BankMapper bankMapper;

    // Directory where files will be saved
    private static final String FILE_DIRECTORY = "C:/Users/farou/Downloads/";

    public DocumentEntryDTO saveDocumentEntry(DocumentEntryDTO documentEntryDTO) throws IOException {
        // Validate the DTO fields
        if (documentEntryDTO.getName() == null || documentEntryDTO.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (documentEntryDTO.getTimestamp() == null) {
            documentEntryDTO.setTimestamp(LocalDateTime.now()); // Set the current time if timestamp is not provided
        }
        if (documentEntryDTO.getFile() == null || documentEntryDTO.getFile().length == 0) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Save the file to the specified directory
        String fileName = "document_" + documentEntryDTO.getName() + ".xlsx"; // Assuming the file is a PDF
        String filePath = FILE_DIRECTORY + fileName;
        File destinationFile = new File(filePath);
        Files.write(Paths.get(filePath), documentEntryDTO.getFile());

        // Convert DTO to entity and set the file path
        DocumentEntry documentEntry = BankMapper.toDocumentEntry(documentEntryDTO);
        documentEntry.setFilePath(filePath); // Assuming the entity has a filePath field

        // Save the entity to the database
        DocumentEntry savedDocumentEntry = documentEntryRepository.save(documentEntry);

        // Convert the saved entity back to DTO
        DocumentEntryDTO savedDocumentEntryDTO = BankMapper.toDocumentEntryDTO(savedDocumentEntry);

        return savedDocumentEntryDTO;
    }

    public DocumentEntryDTO getDocumentEntry(Long id) {
        DocumentEntry documentEntry = documentEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DocumentEntry not found for id: " + id));

        // Parsing xlsx document

        return bankMapper.fromDocumentEntry(documentEntry);
    }

    // deleteDocumentEntry method
    public void deleteDocumentEntry(Long id) {
        DocumentEntry documentEntry = documentEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DocumentEntry not found for id: " + id));
        documentEntryRepository.delete(documentEntry);
    }

    // view document entries return list of
    public List<DocumentEntryDTO> viewDocumentEntries() {
        List<DocumentEntry> documentEntries = documentEntryRepository.findAll();
        return documentEntries.stream()
                .map(bankMapper::fromDocumentEntry)
                .collect(Collectors.toList());
    }

    // view a single document by id

}
