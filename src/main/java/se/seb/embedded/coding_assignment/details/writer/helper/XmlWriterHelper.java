/* (C)2024 */
package se.seb.embedded.coding_assignment.details.writer.helper;

import static java.lang.Boolean.TRUE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.seb.embedded.coding_assignment.details.utils.Utils;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.Document;

/**
 * Helper class dealing with the xml document generation and utility methods associated with it.
 *
 * <p><b>NOTE : </b>Not to be initialized but rather use static methods to generate xml file. XSD schema definition file
 * under ${project.baseDir}/src/main/resources/output.xsd
 */
@Log
@Component
public class XmlWriterHelper {

    private final JAXBContext jaxbContext;
    private final String outDirForXML;

    public XmlWriterHelper(@Value("${payments.payments-processing.xml.write.dir}") String outDirForXML)
            throws JAXBException {
        this.outDirForXML = outDirForXML;
        this.jaxbContext = JAXBContext.newInstance(Document.class);
    }

    /**
     * Uses JAXB functionally and converts java objects to xml string and writes then to the file system location
     *
     * @param fileName generated name of the file
     * @param document {@link Document} object definition as per the provided xml schema file i.e. xsd
     */
    public void writeXmlToFileSystem(String fileName, Document document) {
        // Creating FileWriter object
        Path outputPath = Utils.buildPathToWriteLocation(fileName, outDirForXML);
        log.info(() -> "Output path : " + outputPath);

        // FileWriter extends OutputStreamWriter, which uses a StreamEncoder, which in
        // turn has an internal byte buffer of its own.
        try (Writer fileWriter = new FileWriter(outputPath.toFile(), UTF_8)) {

            // Getting the XMLOutputFactory instance
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

            // Creating XMLStreamWriter object from xmlOutputFactory.
            XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(fileWriter);

            getJaxbDocumentMarshaller().marshal(document, xmlStreamWriter);

        } catch (XMLStreamException e) {
            log.log(SEVERE, e, () -> "Something wend bad while creating XML stream writer.");
            // TODO Could be better handling?
            throw new RuntimeException(e);
        } catch (IOException ex) {
            log.log(SEVERE, ex, () -> "Something wend bad while creating the file writer instance.");
            // TODO Could be better handling?
            throw new RuntimeException(ex);
        } catch (JAXBException e) {
            log.log(SEVERE, e, () -> "Something wend bad while marshalling to xml file.");
            // TODO Could be better handling?
            throw new RuntimeException(e);
        }
    }

    private Marshaller getJaxbDocumentMarshaller() {
        try {
            Marshaller documentMarshaller = jaxbContext.createMarshaller();
            documentMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, TRUE);
            documentMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            documentMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "https://schema.location.schema.xsd");
            return documentMarshaller;

        } catch (JAXBException e) {
            log.log(SEVERE, e, () -> "Something wend bad while creating the file writer instance.");
            // TODO Could be better handling?
            throw new RuntimeException(e);
        }
    }
}
