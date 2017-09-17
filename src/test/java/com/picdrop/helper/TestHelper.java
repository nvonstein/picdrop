/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import com.picdrop.model.Identifiable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.bouncycastle.util.Arrays;
import static org.junit.Assert.*;
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

    public static String readMockJson(String name) throws IOException {
        File f = new File("./test/json", name.concat(".json"));

        return new String(Files.readAllBytes(f.toPath()));
    }

    public static String[] quote(String[] in) {
        for (int i = 0; i < in.length; i++) {
            in[i] = quote(in[i]);
        }
        return in;
    }

    public static String quote(String in) {
        return String.format("\"%s\"", in);
    }

    public static void assertContains(String json, String[] fields) {
        for (String s : fields) {
            assertTrue(String.format("Doesn't contain '%s'; Actual: %s", s, json), json.contains(s));
        }
    }

    public static void assertNotContains(String json, String[] fields) {
        for (String s : fields) {
            assertFalse(String.format("Contains '%s'; Actual: %s", s, json), json.contains(s));
        }
    }

    public static Properties getTestConfig() {
        EnvHelper ehlp = new EnvHelper("");
        Properties p = new Properties(ehlp.getDefaultProperties());

        p.put("token.signer.alg", "HS256");
        p.put("token.cipher.alg", "dir");
        p.put("token.cipher.meth", "A128CBC-HS256");

        p.put("service.file.stores.active.test1", com.google.common.io.Files.createTempDir().getAbsolutePath());

        p.put("service.upload.store", com.google.common.io.Files.createTempDir().getAbsolutePath());

        p.put("service.tika.config", "");

        return p;
    }
}
