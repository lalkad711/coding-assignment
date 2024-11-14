/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.seb.embedded.coding_assignment.app.v1.api.TransactionReportingV1ApiSpec.REPORT_TRANSACTIONS_PATH;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.SAXException;
import se.seb.embedded.coding_assignment.app.v1.model.TransactionReportingApiResponse;
import se.seb.embedded.coding_assignment.details.writer.helper.XmlWriterHelper;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TransacionReportingV1IntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${payments.payments-processing.xml.write.dir}")
    private String outputDrectory;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private XmlWriterHelper xmlWriterHelper;

    private static HttpHeaders HEADERS;

    private File folder;
    private URL rootFolderUrl;

    @BeforeAll
    public static void init() {
        HEADERS = new HttpHeaders();
        HEADERS.setContentType(APPLICATION_JSON);
    }

    @BeforeEach
    void setUp() throws IOException {
        rootFolderUrl = getClass().getResource("/");
        folder = new File(rootFolderUrl.getPath().concat(File.separator).concat(outputDrectory));
        if (folder.exists()) {
            // clear the directory
            FileUtils.deleteDirectory(folder);
        }
        // Create the folder for writing xml files
        if (!folder.mkdirs()) {
            throw new RuntimeException("Cannot create path to write xml files.");
        }

        // Set the path to write
        ReflectionTestUtils.setField(xmlWriterHelper, "outDirForXML", folder.getAbsolutePath());
    }

    @AfterEach
    void cleanUp() throws IOException {
        // Delete the created files and folder
        FileUtils.deleteDirectory(folder);
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + REPORT_TRANSACTIONS_PATH;
    }

    @Test
    @DisplayName("Integration: Api: Positive flow test. : Happy flow")
    @SneakyThrows
    void testHappyFlowFilesCreated() {

        HttpEntity<String> entity =
                new HttpEntity<>(getJsonContentFromFile("json/valid_reporting_transaction_api_request.json"), HEADERS);
        ResponseEntity<TransactionReportingApiResponse> response =
                restTemplate.exchange(createURLWithPort(), POST, entity, TransactionReportingApiResponse.class);
        TransactionReportingApiResponse apiResponse = response.getBody();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(
                4,
                apiResponse.getFileNames().size(),
                "Actual number of generated xml files do not match the expected count.");
        // schema validation of the generated files
        assertTrue(validateXMLSchema(), "The generated xml files do not comply with the schema definition.");
    }

    @SneakyThrows
    private String getJsonContentFromFile(String fileName) {
        return new ClassPathResource(fileName).getContentAsString(StandardCharsets.UTF_8);
    }

    private boolean validateXMLSchema() {
        boolean isValid = false;
        URI xsdUri = URI.create(rootFolderUrl.toString().concat("output.xsd"));

        for (String fileName : folder.list()) {
            String fileAbsolutePath = folder.getAbsolutePath() + File.separator + fileName;
            try {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(xsdUri.toURL());
                Validator validator = schema.newValidator();
                validator.validate(new StreamSource(new File(fileAbsolutePath)));
            } catch (IOException | SAXException e) {
                System.out.println("Exception: " + e.getMessage());
                System.out.println("Not a valid xml file : " + fileAbsolutePath);
                isValid = false;
            }
            isValid = true;
        }
        return isValid;
    }
}
