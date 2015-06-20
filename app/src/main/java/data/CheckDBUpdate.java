package data;

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
	public String result=null;


	public String getTimstamp(){

		String url = ConferenceURL.CheckUpdate;
		HttpPost httpRequest = new HttpPost(url);


		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("eventID", Conference.id));
		try {

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				result = EntityUtils.toString(httpResponse.getEntity());
				int start = result.indexOf("<timestamp>");
				int end = result.indexOf("</timestamp>");
				result = result.substring(start+11, end);
			} else {
				result=null;
				// System.out.print("error: status code not 200");
			}
		} catch (Exception e) {
			System.out.println("exception" + e);
		}

		return result;
	}
	public boolean compare() {
		String result=getTimstamp();
		if (result.compareTo(Conference.timstamp) == 0) {
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
		if (result.compareTo(Conference.timstamp) == 0) {
			needUpdate = false;
		}
		else{
			needUpdate=true;
			Conference.timstamp =result;
		}
		return needUpdate;
	}


}

