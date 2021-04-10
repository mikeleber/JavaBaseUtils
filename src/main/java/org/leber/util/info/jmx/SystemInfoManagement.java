package org.leber.util.info.jmx;

import org.leber.util.info.SystemInfoManager;

import java.io.IOException;

public class SystemInfoManagement implements SystemInfoManagementMBean {

    public SystemInfoManagement() {
        super();
    }

    @Override
    public String getInfo() throws IOException {
        return SystemInfoManager.getInstance().getInfos().build().toString();
    }
}
