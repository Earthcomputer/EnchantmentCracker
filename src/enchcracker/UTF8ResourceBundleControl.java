package enchcracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Scanner;

public class UTF8ResourceBundleControl extends ResourceBundle.Control {
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {
        // https://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle

        // The below is a copy of the default implementation.
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            // This section is changed to allow for reading UTF-8. Non-ASCII characters are converted to escape sequences.
            // Read input stream to string https://stackoverflow.com/a/5445161/11071180
            String utf8Text;
            try (Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A")) {
                utf8Text = scanner.hasNext() ? scanner.next() : "";
            }
            // Replace non-ASCII with escape sequences
            StringBuilder escapedText = new StringBuilder(utf8Text.length());
            for (int i = 0; i < utf8Text.length(); i++) {
                char c = utf8Text.charAt(i);
                if (c < 128) {
                    escapedText.append(c);
                } else {
                    escapedText.append(String.format("\\u%04x", (int)c));
                }
            }
            bundle = new PropertyResourceBundle(new StringReader(escapedText.toString()));
        }
        return bundle;
    }
}
