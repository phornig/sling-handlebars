package org.phms.sling.mvp.impl.presenter;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class PresenterBundleListener implements BundleTrackerCustomizer {

    static final String HEADER = "Sling-MVP-Presenter-Packages";
    private final ConcurrentMap<String, Class<?>> presenters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<String>> bundleResourceTypeMap = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(PresenterBundleListener.class);

    private final BundleTracker bundleTracker;

    public PresenterBundleListener(BundleContext bundleContext) {
        this.bundleTracker = new BundleTracker(bundleContext, Bundle.ACTIVE, this);
        this.bundleTracker.open();
    }

    public ConcurrentMap<String, Class<?>> getPresenters() {
        return presenters;
    }

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent event) {
        List<ServiceRegistration> regs = new ArrayList<>();
        List<String> resourceTypesManagedByBundle = new ArrayList<>();

        Dictionary<?, ?> headers = bundle.getHeaders();
        String packageList = PropertiesUtil.toString(headers.get(HEADER), null);
        if (packageList != null) {

            packageList = StringUtils.deleteWhitespace(packageList);
            String[] packages = packageList.split(",");
            for (String singlePackage : packages) {
                @SuppressWarnings("unchecked")
                Enumeration<URL> classUrls = bundle.findEntries("/" + singlePackage.replace('.', '/'), "*.class",
                        true);

                if (classUrls == null) {
                    LOG.warn("No adaptable classes found in package {}, ignoring", singlePackage);
                    continue;
                }

                while (classUrls.hasMoreElements()) {
                    URL url = classUrls.nextElement();
                    String className = toClassName(url);
                    try {
                        Class<?> implType = bundle.loadClass(className);
                        Presenter annotation = implType.getAnnotation(Presenter.class);
                        if (annotation != null) {
                            String[] resourceTypes = annotation.resourceTypes();
                            for (String resourceType : resourceTypes) {
                                presenters.put(resourceType, implType);
                                resourceTypesManagedByBundle.add(resourceType);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        LOG.warn("Unable to load class", e);
                    }

                }
                bundleResourceTypeMap.put(bundle.getSymbolicName(), resourceTypesManagedByBundle);
            }
        }
        return regs.toArray(new ServiceRegistration[0]);
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        List<String> resourceTypesManagedByBundle = bundleResourceTypeMap.get(bundle.getSymbolicName());
        if (resourceTypesManagedByBundle != null) {
            for (String resourceType : resourceTypesManagedByBundle) {
                presenters.remove(resourceType);
            }
        }
    }

    public synchronized void unregisterAll() {
        this.bundleTracker.close();
    }

    /**
     * Convert class URL to class name
     */
    private String toClassName(URL url) {
        final String f = url.getFile();
        final String cn = f.substring(1, f.length() - ".class".length());
        return cn.replace('/', '.');
    }


}
