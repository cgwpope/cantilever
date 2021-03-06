/*
 * DavMail POP/IMAP/SMTP/CalDav/LDAP Exchange Gateway
 * Copyright (C) 2012  Mickael Guessant
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package davmail.exchange.dav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import davmail.util.StringUtil;

/**
 * Custom Exchange PROPFIND method.
 * Does not load full DOM in memory.
 */
public class ExchangeSearchMethod extends ExchangeDavMethod {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ExchangeSearchMethod.class);

    protected final String searchRequest;

    /**
     * Create search method.
     *
     * @param uri method uri
     * @param searchRequest Exchange search request
     */
    public ExchangeSearchMethod(String uri, String searchRequest) {
        super(uri);
        this.searchRequest = searchRequest;
    }

    protected byte[] generateRequestContent() {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(baos, "UTF-8");
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<d:searchrequest xmlns:d=\"DAV:\">\n");
            writer.write("        <d:sql>");
            writer.write(StringUtil.xmlEncode(searchRequest));
            writer.write("</d:sql>\n");
            writer.write("</d:searchrequest>");
            writer.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getName() {
        return "SEARCH";
    }

}
