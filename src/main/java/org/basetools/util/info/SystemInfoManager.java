package org.basetools.util.info;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to register data providers to generate system runtime informations. This informations can be used
 * at runtime to give explicit informations about the system to a jmx bean or rest endpoint. The list of providers is
 * backed by a Collections.synchronizedMap(new WeakHashMap<>()).
 */
public class SystemInfoManager {
    /**
     * The Info providers.
     */
    Map<String, SystemInfoProvider> infoProviders = Collections.synchronizedMap(new HashMap());

    //private Version version;

    private SystemInfoManager() {
        super();
    }

    public static SystemInfoManager getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Add a data provider to a ConcurrentHashMap by it's name.
     *
     * @param provider the provider
     */
    public void addProvider(SystemInfoProvider provider) {
        infoProviders.put(provider.getInfoProviderName(), provider);
    }

    /**
     * Remove a data provider by it's name.
     *
     * @param provider the provider
     */
    public void removeProvider(SystemInfoProvider provider) {
        infoProviders.remove(provider.getInfoProviderName());
    }

    /**
     * Gets provider.
     *
     * @param name the name
     * @return the provider
     */
    public SystemInfoProvider getProvider(String name) {
        return infoProviders.get(name);
    }

    /**
     * Returns information from all registered data provider as a JSON Builder. The name of the provider is used as JSON
     * Name
     *
     * @return All information provided by the registered providers.
     */
    public JsonObjectBuilder getInfos() {
        JsonObjectBuilder results = Json.createObjectBuilder();
        // results.add("versionInfo", JSONHelper.createFromPoJo(new Version()));
        // loop through each listener and pass on the event if needed
        for (Map.Entry<String, SystemInfoProvider> providerEntry : infoProviders.entrySet()) {
            SystemInfoProvider provider = providerEntry.getValue();
            if (provider != null) {
                results.add(provider.getInfoProviderName(), provider.getInfo());
            }
        }
        return results;
    }

    /**
     * Release the current instance and clear all providers.
     */
    public void release() {
        infoProviders.clear();
    }

    private static class InstanceHolder {
        public static SystemInfoManager instance = new SystemInfoManager();
    }
}
