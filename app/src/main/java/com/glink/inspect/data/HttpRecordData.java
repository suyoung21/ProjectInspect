package com.glink.inspect.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRecordData {

    /**
     * fileUpload : {"id":26,"url":"http://192.168.1.123:13104/GLink-Cloud-Image/order/show/image/26"}
     * file2 : null
     * file3 : null
     * file4 : null
     * file5 : null
     */

    private FileUploadBean fileUpload;
    private FileUploadBean file2;
    private FileUploadBean file3;
    private FileUploadBean file4;
    private FileUploadBean file5;

    public static class FileUploadBean {
        /**
         * id : 26
         * url : http://192.168.1.123:13104/GLink-Cloud-Image/order/show/image/26
         */

        private int id;
        private String url;
    }
}
