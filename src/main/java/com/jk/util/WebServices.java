package com.jk.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebServices {

    public WebServices(){ }

    public static void main(String args[]){
        Authenticate();
    }

    private static void Authenticate(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://www.cacuonlinebanking.com/tob/live/usp-core/sdp/app/ajax/history/accounts.json");
        //HttpPost httppost = new HttpPost("https://www.cacuonlinebanking.com/tob/live/usp-core/sdp/app/ajax/history/accounts.json?_dc=1504214592518&rftoken=1b8838f0-5f6b-4410-87c4-45315dbe843d&getxuseraccts=&getExportAccountNums=true&transfers_display_xuser_accounts=true&pageId=SDP&pageLoad=true&showTransfersOverlay=false&allowAccountsCall=true&recipients=true&locationId=");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("rftoken", "1b8838f0-5f6b-4410-87c4-45315dbe843d"));
            nameValuePairs.add(new BasicNameValuePair("accountId", "dWAGeoYTPhSqYwv5aPLCsj-ee8iIvM14ZAwHdItGAV4"));
            nameValuePairs.add(new BasicNameValuePair("dateRangeStart", "2017-08-22%2000%3A00%3A00"));
            nameValuePairs.add(new BasicNameValuePair("dateRangeEnd", "2017-08-31%2023%3A59%3A59"));
            nameValuePairs.add(new BasicNameValuePair("pageId", ""));
            nameValuePairs.add(new BasicNameValuePair("locationId", ""));
            nameValuePairs.add(new BasicNameValuePair("locationName", ""));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpResponse response = httpclient.execute(httppost);
            String jsonString = EntityUtils.toString(response.getEntity());
            if(response.getStatusLine().toString().contains("200")) {
                System.out.println("this is the response " + jsonString);
            }else{ System.out.println(response.getStatusLine().toString()); }

        } catch (ClientProtocolException e) { System.out.println("CPE"+e);
        } catch (IOException e) { System.out.println("IOE"+e);
        }
    }
}
