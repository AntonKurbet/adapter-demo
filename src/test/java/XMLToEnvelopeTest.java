import org.junit.Test;
import ru.bis.adapterdemo.server.Envelope;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;

public class XMLToEnvelopeTest {
    private Envelope envelope;

    @Test
    public void testXmlToObject() throws JAXBException, FileNotFoundException {
        File file = new File("test.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        envelope = (Envelope) unmarshaller.unmarshal(file);
        System.out.println(envelope);
    }
}

