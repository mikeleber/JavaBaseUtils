package org.basetools.util.info;

/**
 * The type Default system info provider.
 */
public abstract class DefaultSystemInfoProvider implements SystemInfoProvider {
    private String name;

    /**
     * Instantiates a new Default system info provider.
     *
     * @param name the name
     */
    public DefaultSystemInfoProvider(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getInfoProviderName() {
        return name;
    }
}
