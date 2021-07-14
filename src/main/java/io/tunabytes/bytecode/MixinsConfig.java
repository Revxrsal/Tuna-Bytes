package io.tunabytes.bytecode;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.util.Objects.requireNonNull;

final class MixinsConfig {

    private final List<MixinEntry> mixinEntries = new ArrayList<>();
    private final Map<String, Class<?>> neighbors = new LinkedHashMap<>();

    public MixinsConfig() {
        try {
            InputStream configStream = getClass().getResourceAsStream("/mixins.properties");
            requireNonNull(configStream, "mixins.properties not found. Did you add tuna-bytes as an annotation processor?");
            Properties properties = new Properties();
            properties.load(configStream);
            properties.forEach((key, value) -> mixinEntries.add(new MixinEntry((String) key, (String) value)));
            InputStream neighborsStream = getClass().getResourceAsStream("/mixins-neighbors.properties");
            requireNonNull(neighborsStream, "mixins-neighbors.properties not found. Did you add tuna-bytes as an annotation processor?");
            Properties neighborsProps = new Properties();
            neighborsProps.load(neighborsStream);
            neighborsProps.forEach((key, value) -> {
                try {
                    neighbors.put((String) key, Class.forName(String.valueOf(value)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNeighbor(String name) {
        return neighbors.get(name.substring(0, name.lastIndexOf('.')));
    }

    public List<MixinEntry> getMixinEntries() {
        return mixinEntries;
    }
}
