package ma.enset.backend.mappers;

import ma.enset.backend.dtos.*;
import ma.enset.backend.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class MeoMapper {

    // Mapping methods for DocumentEntry
    public DocumentEntryDTO fromDocumentEntry(DocumentEntry documentEntry) {
        DocumentEntryDTO documentEntryDTO = new DocumentEntryDTO();
        documentEntryDTO.setName(documentEntry.getName());
        documentEntryDTO.setTimestamp(documentEntry.getTimestamp());
        // set the id
        documentEntryDTO.setId(documentEntry.getId());
        // As we are not transferring file in DTO, we don't set file here
        return documentEntryDTO;
    }

    public DocumentEntry fromDocumentEntryDTO(DocumentEntryDTO documentEntryDTO, String filePath) {
        DocumentEntry documentEntry = new DocumentEntry();
        BeanUtils.copyProperties(documentEntryDTO, documentEntry);
        documentEntry.setFilePath(filePath);
        return documentEntry;
    }

    // toDocumentEntry method
    public static DocumentEntry toDocumentEntry(DocumentEntryDTO documentEntryDTO) {
        DocumentEntry documentEntry = new DocumentEntry();
        documentEntry.setName(documentEntryDTO.getName());
        documentEntry.setTimestamp(documentEntryDTO.getTimestamp());
        documentEntryDTO.setId(documentEntry.getId());
        return documentEntry;
    }

    // toDocumentEntryDTO method
    public static DocumentEntryDTO toDocumentEntryDTO(DocumentEntry documentEntry) {
        DocumentEntryDTO documentEntryDTO = new DocumentEntryDTO();
        documentEntryDTO.setName(documentEntry.getName());
        documentEntryDTO.setTimestamp(documentEntry.getTimestamp());
        return documentEntryDTO;
    }
}
