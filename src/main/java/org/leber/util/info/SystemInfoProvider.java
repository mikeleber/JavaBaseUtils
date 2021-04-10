package org.leber.util.info;

import javax.json.JsonObjectBuilder;

/**
 * The interface System info provider.
 */
public interface SystemInfoProvider {
    /**
     * Gets info.
     *
     * @return the info
     */
    JsonObjectBuilder getInfo();

    /**
     * Gets name.
     *
     * @return the name
     */
    String getInfoProviderName();
}
