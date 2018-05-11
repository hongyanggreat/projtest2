package com.gk.htc.ahp.brand.bootstrap;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NullResolver implements EntityResolver {

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
            IOException {
        return new InputSource(new StringReader(""));
    }
}
