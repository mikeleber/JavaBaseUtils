package org.basetools.util.info.jmx;

import org.basetools.util.info.SystemInfoManager;

public class SystemInfoManagement implements SystemInfoManagementMBean {

    public SystemInfoManagement() {
        super();
    }

    @Override
    public String getInfo() {
        return SystemInfoManager.getInstance().getInfos().toString();
    }
}
