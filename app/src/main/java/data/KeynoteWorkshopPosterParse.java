package data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class KeynoteWorkshopPosterParse {

    private ArrayList<Keynote> knList = new ArrayList<Keynote>();
    private ArrayList<Workshop> wsList = new ArrayList<Workshop>();
    private ArrayList<Poster> poList = new ArrayList<Poster>();
    private Hashtable<String, String> Datetrans, Dtrans;

    public void daytoDate() {
        Datetrans = new Hashtable<String, String>();
        Datetrans.put("2015-06-22", "Monday, Jun.22");
        Datetrans.put("2015-06-23", "Tuesday, Jun.23");
        Datetrans.put("2015-06-24", "Wednesday, Jun.24");
        Datetrans.put("2015-06-25", "Thursday, Jun.25");
        Datetrans.put("2015-06-26", "Friday, Jun.26");
        Datetrans.put("2015-06-27", "Saturday, Jun.27");
        Datetrans.put("2015-06-28", "Sunday, Jun.28");
        Datetrans.put("2015-06-29", "Monday, Jun.29");
    }

    public void daytoid() {
        Dtrans = new Hashtable<String, String>();
        Dtrans.put("2015-06-22", "1");
        Dtrans.put("2015-06-23", "2");
        Dtrans.put("2015-06-24", "3");
        Dtrans.put("2015-06-25", "4");
        Dtrans.put("2015-06-26", "5");
        Dtrans.put("2015-06-27", "6");
        Dtrans.put("2015-06-28", "7");
        Dtrans.put("2015-06-29", "8");
    }

    public KeynoteWorkshopPosterParse() {
        this.daytoDate();
        this.daytoid();
    }


    public ArrayList<Keynote> getKeynoteData(){
        return knList;
    }

    public ArrayList<Workshop> getwWorkshopData(){
        return wsList;
    }

    public ArrayList<Poster> getwPosterData(){
        return poList;
    }
    public void getData() {

        InputStreamReader isr = null;
        InputStream stream = null;
        try {
            //URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPresentations.jsp?eventid=86");

            //Use Post Method
            String urlString = new String("http://halley.exp.sis.pitt.edu/cn3mobile/allSessionsAndPapers.jsp?conferenceID=135&noAbstract=1");

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            stream = conn.getInputStream();


            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xr = saxParser.getXMLReader();

            DataParseHandler dhandler = new DataParseHandler();
            xr.setContentHandler(dhandler);
            isr = new InputStreamReader(stream, "iso-8859-1");
            //InputStreamReader isr = new InputStreamReader(entity.getContent(),"UTF-8");

            xr.parse(new InputSource(isr));
            stream.close();
            isr.close();
        } catch (Exception ee) {
            System.out.print(ee.toString());
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (isr != null)
                    isr.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    private class DataParseHandler extends DefaultHandler {
        private DataDescriptionParser descriptionParser = new DataDescriptionParser();
        private String contentID = "";
        private Keynote ke;
        private Workshop ws;
        private Poster ps;
        private boolean dataStart = false;
        private boolean isKeynote = false;
        private boolean isWorkshop = false;
        private boolean isPoster=false;

        private StringBuilder sb = new StringBuilder();

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts) throws SAXException {
            sb.setLength(0);
            if (localName.equals("Items")) {
                dataStart = true;
                return;
            }
            if (localName.equals("Item")) {
                ke = new Keynote();

                ke.speakerAffiliation = " ";
                ke.description = " ";

                ws=new Workshop();
                ws.content="";
                ws.eventSessionID="";

                ps=new Poster();
                return;
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (localName.equals("eventSessionID")) {
                ke.ID = sb.toString();
                ws.eventSessionID=sb.toString();
                ps.eventSessionID=sb.toString();
                return;
            }
            if(localName.equals("presentationID")){
                ws.ID = sb.toString();
            }
            if (localName.equals("sessionDate")) {
                String content = sb.toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                try {
                    date = new SimpleDateFormat("MM-dd-yyyy").parse(content);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String str = formatter.format(date);
                ke.date = Datetrans.get(str);
                ke.dayid = Dtrans.get(str);

                ws.date=ke.date;
                ws.day_id=ke.dayid;

                ps.date=ke.date;
                ps.day_id=ke.dayid;
                return;
            }
            if (localName.equals("contentID")) {
                contentID = sb.toString();
                ps.ID=contentID;
                return;
            }

            if (localName.equals("paperTitle")) {
                ke.title = sb.toString();
                ws.name=sb.toString();
                ps.name=sb.toString();
                return;
            }
            if (localName.equals("contentType")) {
                if ("Keynote".equals(sb.toString()))
                    isKeynote = true;
                else if ("Workshop Paper".equals(sb.toString()))
                    isWorkshop = true;
                else if ("Poster".equals(sb.toString()))
                    isPoster = true;
                else {
                    isKeynote = false;
                    isWorkshop = false;
                    isPoster=false;
                }
                return;
            }
            if (localName.equals("begintime") && dataStart) {
                ke.beginTime = sb.toString();
                ws.beginTime= ke.beginTime;
                ps.beginTime=ke.beginTime;
                return;
            }
            if (localName.equals("endtime") && dataStart) {
                ke.endTime = sb.toString();
                ws.endTime=ke.endTime;
                ps.endTime=ke.endTime;
                return;
            }
            if (localName.equals("location")) {
                ke.room = sb.toString();
                ws.room=ke.room;
                ps.room=ke.room;
                return;
            }
            if (localName.equals("authors")) {
                ke.speakerName = sb.toString();
                return;
            }
            if (localName.equals("Item")) {
                if (isKeynote) {
                    ke.description = descriptionParser.getDescription(contentID);
                    knList.add(ke);
                }else if(isWorkshop){
                    ws.content = descriptionParser.getDescription(contentID);
                    wsList.add(ws);
                }else if(isPoster){
                    poList.add(ps);
                }
                isKeynote = false;
                isWorkshop=false;
                isPoster=false;
                return;
            }
            if (localName.equals("Items")) {
                dataStart = false;
                return;
            }
        }

        public void characters(char ch[], int start, int length) {
            sb.append(ch, start, length);
        }
    }

    private class DataDescriptionParser {

        public String getDescription(String ID) {
            String data = "";
            try {
                URL url = new URL("http://halley.exp.sis.pitt.edu/cn3mobile/contentAbstract.jsp?contentID=" + ID);

                InputStream in = url.openStream();
                data = convertToString(in);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        public String convertToString(InputStream is) {
            if (is != null) {
                StringBuilder sb = new StringBuilder();
                String line;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append(" ");
                    }
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                } finally {
                    try {
                        is.close();
                    } catch (Exception e) {

                    }
                }
                return sb.toString();
            } else {
                return "";
            }
        }
    }
}
