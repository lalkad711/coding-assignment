/* (C)2024 */
package se.seb.embedded.coding_assignment.details.writer.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.Document;

@ActiveProfiles("test")
@Tag("unit")
@ExtendWith(SpringExtension.class)
public class XmlWriterHelperTest {

    @Mock
    private JAXBContext jaxbContext;

    @Mock
    private Marshaller marshaller;

    @Mock
    private XMLOutputFactory xmlOutputFactory;

    @Mock
    private XMLStreamWriter xmlStreamWriter;

    private File file = new File(getClass().getResource("/").getPath());

    private XmlWriterHelper xmlWriterHelper;

    @BeforeAll
    static void staticMock() {
        mockStatic(XMLOutputFactory.class);
    }

    @BeforeEach
    void setUp() throws IOException, JAXBException {
        xmlWriterHelper = new XmlWriterHelper(file.getAbsolutePath());
        // Set the path to write
        ReflectionTestUtils.setField(xmlWriterHelper, "jaxbContext", jaxbContext);
    }

    // To be improved
    @Test
    @DisplayName("Junit: Details: Positive flow test. : Happy flow")
    @SneakyThrows
    void testWriteXmlToFileSystem() {

        Document document = new Document();

        when(jaxbContext.createMarshaller()).thenReturn(marshaller);
        doNothing().when(marshaller).setProperty(anyString(), any());
        doNothing().when(marshaller).marshal(any(Document.class), any(XMLStreamWriter.class));

        when(XMLOutputFactory.newInstance()).thenReturn(xmlOutputFactory);
        when(xmlOutputFactory.createXMLStreamWriter(any(FileWriter.class))).thenReturn(xmlStreamWriter);

        xmlWriterHelper.writeXmlToFileSystem("demo_name", document);

        verify(xmlOutputFactory).createXMLStreamWriter(any(FileWriter.class));
        verify(jaxbContext).createMarshaller();
        verify(marshaller, times(3)).setProperty(anyString(), any());
        verify(marshaller).marshal(any(Document.class), any(XMLStreamWriter.class));
    }

    @Test
    @DisplayName("Junit: Details: Negative flow test. : File writer throws IO exception")
    @SneakyThrows
    void testWriteXmlToFileSystemIOEx() {

        Document document = new Document();

        xmlWriterHelper = new XmlWriterHelper("randomPath");

        assertThrows(RuntimeException.class, () -> xmlWriterHelper.writeXmlToFileSystem("demo_name", document));

        verify(xmlOutputFactory, times(0)).createXMLStreamWriter(any(FileWriter.class));
        verify(jaxbContext, times(0)).createMarshaller();
        verify(marshaller, times(0)).setProperty(anyString(), any());
        verify(marshaller, times(0)).marshal(any(Document.class), any(XMLStreamWriter.class));
    }

    @Test
    @DisplayName("Junit: Details: Negative flow test. : XMLStreamWriter throws XMLStreamException")
    @SneakyThrows
    void testWriteXmlToFileSystemXMLSreamEx() {

        Document document = new Document();

        when(jaxbContext.createMarshaller()).thenReturn(marshaller);
        doNothing().when(marshaller).setProperty(anyString(), any());
        doNothing().when(marshaller).marshal(any(Document.class), any(XMLStreamWriter.class));

        when(XMLOutputFactory.newInstance()).thenReturn(xmlOutputFactory);
        XMLStreamException xmlEx = new XMLStreamException();
        when(xmlOutputFactory.createXMLStreamWriter(any(FileWriter.class))).thenThrow(xmlEx);

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> xmlWriterHelper.writeXmlToFileSystem("demo_name", document));

        assertEquals(xmlEx, ex.getCause());
        verify(xmlOutputFactory).createXMLStreamWriter(any(FileWriter.class));
        verify(jaxbContext, times(0)).createMarshaller();
        verify(marshaller, times(0)).setProperty(anyString(), any());
        verify(marshaller, times(0)).marshal(any(Document.class), any(XMLStreamWriter.class));
    }

    @Test
    @DisplayName("Junit: Details: Negative flow test. : Marshaller throws JAXBException")
    @SneakyThrows
    void testWriteXmlToFileSystemJAXBEx() {

        Document document = new Document();

        when(jaxbContext.createMarshaller()).thenReturn(marshaller);
        doNothing().when(marshaller).setProperty(anyString(), any());

        JAXBException jaxbEx = new JAXBException("Test");
        doThrow(jaxbEx).when(marshaller).marshal(any(Document.class), any(XMLStreamWriter.class));

        when(XMLOutputFactory.newInstance()).thenReturn(xmlOutputFactory);
        when(xmlOutputFactory.createXMLStreamWriter(any(FileWriter.class))).thenReturn(xmlStreamWriter);

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> xmlWriterHelper.writeXmlToFileSystem("demo_name", document));

        assertEquals(jaxbEx, ex.getCause());

        verify(xmlOutputFactory).createXMLStreamWriter(any(FileWriter.class));
        verify(jaxbContext).createMarshaller();
        verify(marshaller, times(3)).setProperty(anyString(), any());
        verify(marshaller).marshal(any(Document.class), any(XMLStreamWriter.class));
    }
}
