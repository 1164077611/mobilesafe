package com.csx.mobilesafe.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by csx on 2016/4/22.
 */
public class StreamUtils {
    public static String stream2String(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int len=0;
        byte[] buffer=new byte[1024];
        while ((len=inputStream.read(buffer))!=-1){
            out.write(buffer,0,len);
        }
        inputStream.close();
        out.close();

        return out.toString();
    }

}
