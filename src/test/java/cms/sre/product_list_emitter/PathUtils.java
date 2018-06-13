package cms.sre.product_list_emitter;

import org.apache.commons.lang3.SystemUtils;

public class PathUtils {
    public static String getAbsolutePathForClasspathResource(String resource){
        String keyStoreLocation = ClassLoader.getSystemClassLoader()
                .getResource(resource)
                .getPath();


        if(SystemUtils.IS_OS_WINDOWS){
            keyStoreLocation = keyStoreLocation.substring(1)
                    .replace('/', '\\');
        }

        return keyStoreLocation;
    }
}
