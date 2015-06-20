package data;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class WorkshopParse {
//
//    private ArrayList<Workshop> wsList = new ArrayList<Workshop>();
//    private Hashtable<String, String> Datetrans, Dtrans;
//
//    public void daytoDate() {
//        Datetrans = new Hashtable<String, String>();
//        Datetrans.put("2015-06-22", "Monday, Jun.22");
//        Datetrans.put("2015-06-23", "Tuesday, Jun.23");
//        Datetrans.put("2015-06-24", "Wednesday, Jun.24");
//        Datetrans.put("2015-06-25", "Thursday, Jun.25");
//        Datetrans.put("2015-06-26", "Friday, Jun.26");
//        Datetrans.put("2015-06-27", "Saturday, Jun.27");
//        Datetrans.put("2015-06-28", "Sunday, Jun.28");
//        Datetrans.put("2015-06-29", "Monday, Jun.29");
//    }
//
//    public void daytoid() {
//        Dtrans = new Hashtable<String, String>();
//        Dtrans.put("2015-06-22", "1");
//        Dtrans.put("2015-06-23", "2");
//        Dtrans.put("2015-06-24", "3");
//        Dtrans.put("2015-06-25", "4");
//        Dtrans.put("2015-06-26", "5");
//        Dtrans.put("2015-06-27", "6");
//        Dtrans.put("2015-06-28", "7");
//        Dtrans.put("2015-06-29", "8");
//    }
//
//    public WorkshopParse() {
//        this.daytoDate();
//        this.daytoid();
//    }
//
//    public ArrayList<Workshop> getWorkshopData() {
//
//        InputStreamReader isr = null;
//        InputStream stream = null;
//        try {
//            //URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPresentations.jsp?eventid=86");
//
//            //Use Post Method
//            String urlString = new String("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPapers.jsp?conferenceID=135&noAbstract=1");
//
//            URL url = new URL(urlString);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(10000 /* milliseconds */);
//            conn.setConnectTimeout(15000 /* milliseconds */);
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            // Starts the query
//            conn.connect();
//            stream = conn.getInputStream();
//
//
//            SAXParserFactory spf = SAXParserFactory.newInstance();
//            SAXParser saxParser = spf.newSAXParser();
//            XMLReader xr = saxParser.getXMLReader();
//
//            WorkshopParseHandler khandler = new WorkshopParseHandler();
//            xr.setContentHandler(khandler);
//            isr = new InputStreamReader(stream, "iso-8859-1");
//            //InputStreamReader isr = new InputStreamReader(entity.getContent(),"UTF-8");
//
//            xr.parse(new InputSource(isr));
//            stream.close();
//            isr.close();
//        } catch (Exception ee) {
//            System.out.print(ee.toString());
//        } finally {
//            try {
//                if (stream != null)
//                    stream.close();
//                if (isr != null)
//                    isr.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//        return wsList;
//    }
//
//
//    private class WorkshopParseHandler extends DefaultHandler {
//        private WorkshopDescriptionParser descriptionParser = new WorkshopDescriptionParser();
//        private String contentID = "";
//        private Workshop ws;
//        private boolean WorkshopStart = false;
//        private boolean isKeynote = false;
//        private StringBuilder sb = new StringBuilder();
//
//        public void startDocument() throws SAXException {
//        }
//
//        public void endDocument() throws SAXException {
//        }
//
//        public void startElement(String namespaceURI, String localName,
//                                 String qName, Attributes atts) throws SAXException {
//            sb.setLength(0);
//            if (localName.equals("Items")) {
//                WorkshopStart = true;
//                return;
//            }
//            if (localName.equals("Item")) {
//                ws = new Workshop();
//                return;
//            }
//        }
//
//        public void endElement(String namespaceURI, String localName,
//                               String qName) throws SAXException {
//            if (localName.equals("eventSessionID")) {
//                ws.ID = sb.toString();
//                return;
//            }
//            if (localName.equals("sessionDate")) {
//                String content = sb.toString();
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                Date date = new Date();
//                try {
//                    date = new SimpleDateFormat("MM-dd-yyyy").parse(content);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                String str = formatter.format(date);
//                ws.date = Datetrans.get(str);
//                ws.dayid = Dtrans.get(str);
//                return;
//            }
//            if (localName.equals("contentID")) {
//                contentID = sb.toString();
//                return;
//            }
//            if (localName.equals("paperTitle")) {
//                ws.title = sb.toString();
//                return;
//            }
//            if (localName.equals("contentType")) {
//                if ("Keynote".equals(sb.toString()))
//                    isKeynote = true;
//                else
//                    isKeynote = false;
//                return;
//            }
//            if (localName.equals("begintime") && keynoteStart) {
//                ke.beginTime = sb.toString();
//                return;
//            }
//            if (localName.equals("endtime") && keynoteStart) {
//                ke.endTime = sb.toString();
//                return;
//            }
//            if (localName.equals("location")) {
//                ke.room = sb.toString();
//                return;
//            }
//            if (localName.equals("authors")) {
//                ke.speakerName = sb.toString();
//                return;
//            }
//            if (localName.equals("Item")) {
//                if (isKeynote) {
//                    ke.description = descriptionParser.getDescription(contentID);
//                    wsList.add(ke);
//                }
//                isKeynote = false;
//                return;
//            }
//            if (localName.equals("Items")) {
//                keynoteStart = false;
//                return;
//            }
//        }
//
//        public void characters(char ch[], int start, int length) {
//            sb.append(ch, start, length);
//        }
//    }
//
//    private class WorkshopDescriptionParser {
//
//        public String getDescription(String ID) {
//            String data = "";
//            try {
//                URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/contentAbstract.jsp?contentID=" + ID);
//
//                InputStream in = url.openStream();
//                data = convertToString(in);
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return data;
//        }
//
//        public String convertToString(InputStream is) {
//            if (is != null) {
//                StringBuilder sb = new StringBuilder();
//                String line;
//                try {
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//
//                    while ((line = reader.readLine()) != null) {
//                        sb.append(line).append(" ");
//                    }
//                } catch (Exception e) {
//                    System.out.print(e.getMessage());
//                } finally {
//                    try {
//                        is.close();
//                    } catch (Exception e) {
//
//                    }
//                }
//                return sb.toString();
//            } else {
//                return "";
//            }
//        }
//    }
}
