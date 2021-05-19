package org.basetools.util.info.jmx;

import java.io.IOException;

public interface SystemInfoManagementMBean {

    String getInfo() throws IOException;
}
