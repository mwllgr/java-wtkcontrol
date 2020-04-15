package at.mwllgr.wtkcontrol.model;

import java.io.File;
import java.util.HashMap;

public class Repository {
    HashMap<String, DataField> fields;
    private File addressList;

    public File getAddressList() {
        return addressList;
    }

    public void setAddressList(File addressList) {
        if(fields == null) {
            fields = new HashMap<>();
        }
        this.addressList = addressList;
    }


}
