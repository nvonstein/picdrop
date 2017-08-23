/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import com.picdrop.model.Identifiable;
import com.picdrop.model.Share;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.bouncycastle.util.Arrays;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author nvonstein
 */
public class TestHelper {
    
    static public byte[] createFileContent(byte[] data, String boundary, String contentType, String fileName) {
        String start = "--" + boundary + "\r\n Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n"
                + "Content-type: " + contentType + "\r\n"
                + "Content-Transfer-Encoding: binary\r\n\r\n";
        
        String end = "\r\n--" + boundary + "--"; // correction suggested @butfly 
        return Arrays.concatenate(start.getBytes(), data, end.getBytes());
    }
    
    static public HttpServletRequest generateFileRequest(MultipartFile file) throws IOException {
        MockMultipartHttpServletRequest lFileRequest = new MockMultipartHttpServletRequest();
        String boundary = "q1w2e3r4t5y6u7i8o9";
        lFileRequest.setContentType("multipart/form-data; boundary=" + boundary);
        lFileRequest.setMethod("POST");
        lFileRequest.setContent(TestHelper.createFileContent(file.getBytes(), boundary, file.getContentType(), file.getOriginalFilename()));
        lFileRequest.addFile(file);
        return lFileRequest;
    }
    
    public static <T> Answer<T> reflect(int i) {
        return new Answer<T>() {
            @Override
            public T answer(InvocationOnMock arg0) throws Throwable {
                return arg0.getArgument(i);
            }
        };
    }
    
    public static <T extends Identifiable> Answer<T> reflectWithId(int i, String id) {
        return new Answer<T>() {
            @Override
            public T answer(InvocationOnMock arg0) throws Throwable {
                T obj = arg0.getArgument(i);
                obj.setId(id);
                return obj;
            }
        };
    }
}
