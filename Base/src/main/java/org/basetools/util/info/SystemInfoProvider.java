package org.basetools.util.info;

import net.minidev.json.JSONObject;


/**
 * The interface System info provider.
 */
public interface SystemInfoProvider {
    /**
     * Gets info.
     *
     * @return the info
     */
    JSONObject getInfo();

    /**
     * Gets name.
     *
     * @return the name
     */
    String getInfoProviderName();
}
