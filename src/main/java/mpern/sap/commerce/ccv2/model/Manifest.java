package mpern.sap.commerce.ccv2.model;

import static mpern.sap.commerce.ccv2.model.util.ParseUtils.emptyOrSet;
import static mpern.sap.commerce.ccv2.model.util.ParseUtils.validateNullOrWhitespace;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Manifest {
    public final String commerceSuiteVersion;
    public final boolean useCloudExtensionPack;
    public final boolean enableImageProcessingService;

    public final List<ExtensionPack> extensionPacks;

    public final Set<String> extensions;

    public final List<Addon> storefrontAddons;

    public final List<Property> properties;

    public final List<Aspect> aspects;

    public final TestConfiguration tests;

    public final TestConfiguration webTests;

    public Manifest(String commerceSuiteVersion, boolean useCloudExtensionPack, boolean enableImageProcessingService,
            List<ExtensionPack> extensionPacks, Set<String> extensions, List<Addon> storefrontAddons,
            List<Property> properties, List<Aspect> aspects, TestConfiguration tests, TestConfiguration webTests) {
        this.commerceSuiteVersion = commerceSuiteVersion;
        this.useCloudExtensionPack = useCloudExtensionPack;
        this.enableImageProcessingService = enableImageProcessingService;
        this.extensionPacks = extensionPacks;
        this.extensions = Collections.unmodifiableSet(extensions);
        this.storefrontAddons = Collections.unmodifiableList(storefrontAddons);
        this.properties = Collections.unmodifiableList(properties);
        this.aspects = Collections.unmodifiableList(aspects);
        this.tests = tests;
        this.webTests = webTests;
    }

    public static Manifest fromMap(Map<String, Object> jsonMap) {
        String version = validateNullOrWhitespace((String) jsonMap.get("commerceSuiteVersion"),
                "Manifest.commerceSuiteVersion must have a value");
        boolean useExtensionPack = false;
        Object rawBool = jsonMap.get("useCloudExtensionPack");
        if (rawBool != null) {
            if (!(rawBool instanceof Boolean)) {
                throw new IllegalArgumentException("Manifest.useCloudExtensionPack must be a boolean value");
            }
            useExtensionPack = (boolean) rawBool;
        }
        boolean enableImageProcessingService = false;
        rawBool = jsonMap.get("enableImageProcessingService");
        if (rawBool != null) {
            if (!(rawBool instanceof Boolean)) {
                throw new IllegalArgumentException("Manifest.enableImageProcessingService must be a boolean value");
            }
            enableImageProcessingService = (boolean) rawBool;
        }

        List<Map<String, Object>> raw = (List<Map<String, Object>>) jsonMap.get("extensionPacks");
        List<ExtensionPack> extensionPacks;
        if (raw == null) {
            extensionPacks = Collections.emptyList();
        } else {
            extensionPacks = raw.stream().map(ExtensionPack::fromMap).collect(Collectors.toList());
        }

        // TODO: check useConfig

        Set<String> extensions = emptyOrSet((List<String>) jsonMap.get("extensions"));

        raw = (List<Map<String, Object>>) jsonMap.get("storefrontAddons");
        List<Addon> addons;
        if (raw == null) {
            addons = Collections.emptyList();
        } else {
            addons = raw.stream().map(Addon::fromMap).collect(Collectors.toList());
        }

        raw = (List<Map<String, Object>>) jsonMap.get("properties");
        List<Property> properties;
        if (raw == null) {
            properties = Collections.emptyList();
        } else {
            properties = raw.stream().map(Property::fromMap).collect(Collectors.toList());
        }

        raw = (List<Map<String, Object>>) jsonMap.get("aspects");
        List<Aspect> aspects;
        if (raw == null) {
            aspects = Collections.emptyList();
        } else {
            aspects = raw.stream().map(Aspect::fromMap).collect(Collectors.toList());
        }

        Map<String, Object> rawConfig = (Map<String, Object>) jsonMap.get("tests");
        TestConfiguration tests = Optional.ofNullable(rawConfig).map(TestConfiguration::fromMap)
                .orElse(TestConfiguration.NO_VALUE);

        rawConfig = (Map<String, Object>) jsonMap.get("webTests");
        TestConfiguration webTests = Optional.ofNullable(rawConfig).map(TestConfiguration::fromMap)
                .orElse(TestConfiguration.NO_VALUE);

        return new Manifest(version, useExtensionPack, enableImageProcessingService, extensionPacks, extensions, addons,
                properties, aspects, tests, webTests);
    }

    public List<Property> getProperties() {
        return properties;
    }
}
