package com.bjut.printer.ThreadManager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bjut.printer.Consts.PublicConsts;
import com.bjut.printer.Operator.SendBroadCast;
import com.bjut.printer.bean.UpdateInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

/**
 * Created by johnsmac on 9/13/16.
 */
public class CheckVersionThread extends Thread {


    private Context mContext;
    private boolean isDone = false;
    private Handler handler;
    public CheckVersionThread(Context context,Handler mHandler) {

        this.mContext = context;
        this.handler=mHandler;
    }
//    public void cancel(boolean isCancel){
//        this.isDone=isCancel;
//    }
    @Override
    public void run() {

        Log.i("SerialPort","CheckOrDownloadIntent Service--->CheckVersionThread is running ");
        while (!isDone) {

            HttpParams httpParameters;
            boolean startEntryElementFlag = false;
            UpdateInfo updateInfo = new UpdateInfo();
            try {
                httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, PublicConsts.timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, PublicConsts.timeoutSocket);
                HttpClient client = new DefaultHttpClient(httpParameters);
                String urlstr = PublicConsts.WEBADDRESS + "version";
                HttpGet myget = new HttpGet(urlstr);
                HttpResponse response = client.execute(myget);


                int code = response.getStatusLine().getStatusCode();
                if (code != 200) {

                    isDone=true;//if fail we do nothing in loop
                    SendBroadCast.sendError(mContext, "检测版本更新连接失败");

                } else {
                    XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();
                    java.io.InputStream inStream = response.getEntity().getContent();
                    XmlPullParser xmlPullParser = pullFactory.newPullParser();
                    xmlPullParser.setInput(inStream, "UTF-8");


                    int eventType = xmlPullParser.getEventType();

                    while ((eventType != XmlPullParser.END_DOCUMENT)) {
                        String localName;
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT: {

                            }
                            break;
                            case XmlPullParser.START_TAG: {
                                localName = xmlPullParser.getName().trim();
                                if (localName.equalsIgnoreCase(PublicConsts.kEntryVersionName)) {

                                    startEntryElementFlag = true;

                                    updateInfo.setDownloadAddress(getValue("download_address", xmlPullParser));
                                    updateInfo.setSoftwareVersion(getValue("software_version", xmlPullParser));
                                    updateInfo.setVersionDesc(getValue("software_version_desc", xmlPullParser));
                                    updateInfo.setVersionForce(getValue("software_version_force", xmlPullParser));
                                    updateInfo.setVersionNum(getValue("software_version_num", xmlPullParser));

                                } else if (startEntryElementFlag == true) {
                                    localName = xmlPullParser.getName().trim();
                                    if (localName.equalsIgnoreCase(PublicConsts.kEntryRatioName)) {
                                        //TODO
                                        /**I DON'T KNOW THE DOC EXACTLY , SO ABSENCE HERE.*/
                                    }
                                }
                            }
                            break;
                            case XmlPullParser.END_TAG: {

                            }
                            break;
                            default:
                                break;
                        }
                        try {
                            eventType = xmlPullParser.next();
                        } catch (IOException e) {
                            isDone=true;
                            SendBroadCast.sendError(mContext, e.getLocalizedMessage());
                        }
                    }
                }
            } catch (Exception e) {
                isDone=true;
                SendBroadCast.sendError(mContext, e.getLocalizedMessage());
            }

            Message message=handler.obtainMessage();
            Bundle bundle=new Bundle();
            bundle.putParcelable(PublicConsts.UPDATE_INFO,updateInfo);
            message.setData(bundle);
            message.what=PublicConsts.UPDATE_INFO_WHAT;
            handler.sendMessage(message);//in the other hand ,we should interrupt this thread
            isDone=true;
        }
    }

    private String getValue(String key, XmlPullParser xmlPullParser) {

        return xmlPullParser.getAttributeValue("", key).trim();

    }
}
