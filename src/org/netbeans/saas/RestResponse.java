/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.saas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * RestResponse
 *
 * @author ron
 */
public class RestResponse {

    private ByteArrayOutputStream os;
    private String contentType = "text/plain";
    private String contentEncoding;
    private int responseCode;
    private String responseMsg;
    private long lastModified;
    
    /**
     *
     */
    public RestResponse() {
        os = new ByteArrayOutputStream();
    }
    
    /**
     *
     * @param bytes
     * @throws IOException
     */
    public RestResponse(byte[] bytes) throws IOException {
        this();
        
        byte[] buffer = new byte[1024];
        int count = 0;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        while ((count = bis.read(buffer)) != -1) {
            write(buffer, 0, count);
        }
    }
    
    /**
     *
     * @param contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /**
     *
     * @return
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     *
     * @param contentEncoding
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }
    
    /**
     *
     * @param msg
     */
    public void setResponseMessage(String msg) {
        this.responseMsg = msg;
    }
    
    /**
     *
     * @return
     */
    public String getResponseMessage() {
        return responseMsg;
    }
    
    /**
     *
     * @param code
     */
    public void setResponseCode(int code) {
        this.responseCode = code;
    }
    
    /**
     *
     * @return
     */
    public int getResponseCode() {
        return responseCode;
    }
    
    /**
     *
     * @param lastModified
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    
    /**
     *
     * @return
     */
    public long getLastModified() {
        return lastModified;
    }
    
    /**
     *
     * @param bytes
     * @param start
     * @param length
     */
    public void write(byte[] bytes, int start, int length) {
        os.write(bytes, start, length);
    }
    
    /**
     *
     * @return
     */
    public byte[] getDataAsByteArray() {
        return os.toByteArray();
    }
    
    /**
     *
     * @return
     */
    public String getDataAsString() {
        try {
            return os.toString("UTF-8");
        } catch (Exception ex) {
            Logger.getLogger(RestConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    /**
     *
     * @return
     */
    public OutputStream getOutputStream() {
        return os;
    }
    
    /**
     *
     * @param <T>
     * @param jaxbClass
     * @return
     * @throws JAXBException
     */
    public <T> T getDataAsObject(Class<T> jaxbClass) throws JAXBException {
        return getDataAsObject(jaxbClass, jaxbClass.getPackage().getName());
    }
    
    /**
     *
     * @param <T>
     * @param clazz
     * @param packageName
     * @return
     * @throws JAXBException
     */
    public <T> T getDataAsObject(Class<T> clazz, String packageName) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        Object obj = u.unmarshal(new StreamSource(new StringReader(getDataAsString())));
        
        if (obj instanceof JAXBElement) {
            return (T) ((JAXBElement) obj).getValue();
        } else {
            return (T) obj;
        }        
    }
}
