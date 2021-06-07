import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.bis.adapterdemo.server.Envelope;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;

public class EnvelopeToXMLTest {
    private Envelope envelope;

    @Before
    public void setUp() {
        envelope = new Envelope("MyName", 123456, "AllInfo");
    }

    @After
    public void tearDown() {
        envelope = null;
    }

    @Test
    public void testObjectToXml() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(envelope, new File("envelope.xml"));
        marshaller.marshal(envelope, System.out);
    }
}

