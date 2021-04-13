package org.basetools.util.info.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;

public final class SystemInfoManagementAgent {

    public static void run() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.modelui:name=InfoManagementAgent");
            if (!server.isRegistered(name)) {
                server.registerMBean(new SystemInfoManagement(), name);
            }
        } catch (MalformedObjectNameException | MBeanRegistrationException | NotCompliantMBeanException | InstanceAlreadyExistsException e) {
            e.printStackTrace();
        }
    }
}
