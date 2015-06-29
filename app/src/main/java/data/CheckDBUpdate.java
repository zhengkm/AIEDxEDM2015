package data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class CheckDBUpdate {
	public boolean needUpdate;



	public String getTimstamp(){
		String result=null;
		try{
			URL Url=new URL(ConferenceURL.CheckUpdate+"eventID="+Conference.id);
			InputStream in=Url.openStream();
			result=convertToString(in);
			int start = result.indexOf("<timestamp>");
			int end = result.indexOf("</timestamp>");
			result = result.substring(start+11, end);

			//System.out.println("!!!!!!!!FFFFF"+result+Conference.id);
		}catch(Exception ee){
			System.out.println(ee.getStackTrace());
		}
		return result;
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
	public boolean compare() {
		String result=getTimstamp();
		if (result==null||!Character.isDigit(result.charAt(0))||result.compareTo(Conference.timstamp) == 0||!Character.isDigit(Conference.timstamp.charAt(0))) {
			needUpdate = false;
		}
		else{
			needUpdate=true;
			Conference.timstamp =result;
		}
		return needUpdate;
	}

	public boolean check() {
		String result=getTimstamp();
		if (result==null||!Character.isDigit(result.charAt(0))||result.compareTo(Conference.timstamp) == 0||!Character.isDigit(Conference.timstamp.charAt(0))) {
			needUpdate = false;
		}
		else{
			needUpdate=true;
			//Conference.timstamp =result;
		}
		return needUpdate;
	}


}

