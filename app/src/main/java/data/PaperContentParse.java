package data;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class PaperContentParse {

    public ArrayList<PaperContent> List = new ArrayList<PaperContent>();
    private HashMap<String, Author> authorMap=new HashMap<String, Author>();


    public ArrayList<PaperContent> getPaperContent(){
        return List;
    }

    public HashMap<String, Author> getAuthorMap(){
        return authorMap;
    }

    public void getData() {

        try {
            URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/allContentsAndAuthors.jsp?conferenceID=135&noAbstract=1");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xr = saxParser.getXMLReader();

            SessionParseHandler shandler = new SessionParseHandler();
            xr.setContentHandler(shandler);

            InputStreamReader isr = new InputStreamReader(url.openStream(), "iso-8859-1");

            xr.parse(new InputSource(isr));
            isr.close();

        } catch (Exception ee) {
            System.out.print(ee.toString());
        }

    }

    private class SessionParseHandler extends DefaultHandler {
        private PaperContent se;
        private boolean contentStart = false;
        private Author author;
        private StringBuilder sb = new StringBuilder();

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            sb.setLength(0);
            if (localName.equals("contents")) {
                contentStart = true;
                return;
            }
            if (localName.equals("content")) {
                se = new PaperContent();
                return;
            }
            if (localName.equals("authorpresenters") && contentStart) {
                se.authors = "";
                return;
            }
            if(localName.equals("authorpresenter")){
                author=new Author();
                return;
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (localName.equals("contentID")) {
                String content = sb.toString();
                se.id = content;
                se.paperAbstract = getAbstract(content);
                return;
            }
            if (localName.equals("title")) {
                se.title = sb.toString();
                return;
            }
            if (localName.equals("contentType")) {
                se.type = sb.toString();
                return;
            }
            if (localName.equals("contentTrack")) {
                se.track = sb.toString();
                return;
            }
            if(localName.equals("authorID")&& contentStart){
                author.ID=sb.toString();
                se.authorIDList.add(author.ID);
                return;
            }
            if (localName.equals("name") && contentStart) {
                se.authors = se.authors + sb.toString() + ", ";
                author.name=sb.toString();
                return;
            }
            if(localName.equals("authorpresenter")){
                if(!authorMap.containsKey(author.ID)){
                   authorMap.put(author.ID,author);
                }
                return;
            }
            if (localName.equals("content")) {
//                if (se.paperAbstract != null)
                    List.add(se);
                return;
            }
            if (localName.equals("contents")) {
                contentStart = false;
                return;
            }



        }

        public void characters(char ch[], int start, int length) {
            sb.append(ch, start, length);
        }

        private String getAbstract(String id) {
            String ab;
            PaperAbstractParse pap = new PaperAbstractParse();
            ab = pap.getPaperAbstract(id);
            return ab;
        }
    }
}
