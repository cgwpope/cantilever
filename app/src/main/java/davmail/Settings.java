/*
 * DavMail POP/IMAP/SMTP/CalDav/LDAP Exchange Gateway
 * Copyright (C) 2009  Mickael Guessant
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package davmail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;

import org.slf4j.Logger;

/**
 * Settings facade.
 * DavMail settings are stored in the .davmail.properties file in current
 * user home directory or in the file specified on the command line.
 */
public final class Settings {
    private Settings() {
    }

    private static final Properties SETTINGS = new Properties() {
        @Override
        public synchronized Enumeration<Object> keys() {
            Enumeration keysEnumeration = super.keys();
            TreeSet<String> sortedKeySet = new TreeSet<String>();
            while (keysEnumeration.hasMoreElements()) {
                sortedKeySet.add((String) keysEnumeration.nextElement());
            }
            final Iterator<String> sortedKeysIterator = sortedKeySet.iterator();
            return new Enumeration<Object>() {

                public boolean hasMoreElements() {
                    return sortedKeysIterator.hasNext();
                }

                public Object nextElement() {
                    return sortedKeysIterator.next();
                }
            };
        }

    };
    private static String configFilePath;
    private static boolean isFirstStart;

    /**
     * Set config file path (from command line parameter).
     *
     * @param path davmail properties file path
     */
    public static synchronized void setConfigFilePath(String path) {
        configFilePath = path;
    }

    /**
     * Detect first launch (properties file does not exist).
     *
     * @return true if this is the first start with the current file path
     */
    public static synchronized boolean isFirstStart() {
        return isFirstStart;
    }

    /**
     * Load properties from provided stream (used in webapp mode).
     *
     * @param inputStream properties stream
     * @throws IOException on error
     */
    public static synchronized void load(InputStream inputStream) throws IOException {
        SETTINGS.load(inputStream);
    }

    /**
     * Load properties from current file path (command line or default).
     */
    public static synchronized void load() {
        FileInputStream fileInputStream = null;
        try {
//            if (configFilePath == null) {
//                //noinspection AccessOfSystemProperties
//                configFilePath = System.getProperty("user.home") + "/.davmail.properties";
//            }
//            File configFile = new File(configFilePath);
//            if (configFile.exists()) {
//                fileInputStream = new FileInputStream(configFile);
//                load(fileInputStream);
//            } else {
                isFirstStart = true;

                // first start : set default values, ports above 1024 for unix/linux
                setDefaultSettings();
                save();
//            }
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                	//XXX - UI Listner                    DavGatewayTray.debug(new BundleMessage("LOG_ERROR_CLOSING_CONFIG_FILE"), e);
                }
            }
        }
    }

    /**
     * Set all settings to default values.
     * Ports above 1024 for unix/linux
     */
    public static void setDefaultSettings() {
        SETTINGS.put("davmail.url", "https://mail.telus.com");
        SETTINGS.put("davmail.popPort", "11110");
        SETTINGS.put("davmail.imapPort", "11143");
        SETTINGS.put("davmail.smtpPort", "11025");
        SETTINGS.put("davmail.caldavPort", "11080");
        SETTINGS.put("davmail.ldapPort", "11389");
        SETTINGS.put("davmail.clientSoTimeout", "");
        SETTINGS.put("davmail.keepDelay", "30");
        SETTINGS.put("davmail.sentKeepDelay", "90");
        SETTINGS.put("davmail.caldavPastDelay", "90");
        SETTINGS.put("davmail.imapIdleDelay", "");
        SETTINGS.put("davmail.folderSizeLimit", "200");
        SETTINGS.put("davmail.enableKeepAlive", Boolean.FALSE.toString());
        SETTINGS.put("davmail.allowRemote", Boolean.FALSE.toString());
        SETTINGS.put("davmail.bindAddress", "");
        SETTINGS.put("davmail.useSystemProxies", Boolean.FALSE.toString());
        SETTINGS.put("davmail.enableProxy", Boolean.FALSE.toString());
        SETTINGS.put("davmail.enableEws", "true");
        SETTINGS.put("davmail.enableKerberos", "false");
        SETTINGS.put("davmail.disableUpdateCheck", "false");
        SETTINGS.put("davmail.proxyHost", "");
        SETTINGS.put("davmail.proxyPort", "");
        SETTINGS.put("davmail.proxyUser", "");
        SETTINGS.put("davmail.proxyPassword", "");
        SETTINGS.put("davmail.noProxyFor", "");
        SETTINGS.put("davmail.server", Boolean.FALSE.toString());
        SETTINGS.put("davmail.server.certificate.hash", "");
        SETTINGS.put("davmail.caldavAlarmSound", "");
        SETTINGS.put("davmail.forceActiveSyncUpdate", Boolean.FALSE.toString());
        SETTINGS.put("davmail.showStartupBanner", Boolean.TRUE.toString());
        SETTINGS.put("davmail.disableGuiNotifications", Boolean.FALSE.toString());
        SETTINGS.put("davmail.imapAutoExpunge", Boolean.TRUE.toString());
        SETTINGS.put("davmail.popMarkReadOnRetr", Boolean.FALSE.toString());
        SETTINGS.put("davmail.smtpSaveInSent", Boolean.TRUE.toString());
        SETTINGS.put("davmail.ssl.keystoreType", "");
        SETTINGS.put("davmail.ssl.keystoreFile", "");
        SETTINGS.put("davmail.ssl.keystorePass", "");
        SETTINGS.put("davmail.ssl.keyPass", "");
        SETTINGS.put("davmail.ssl.clientKeystoreType", "");
        SETTINGS.put("davmail.ssl.clientKeystoreFile", "");
        SETTINGS.put("davmail.ssl.clientKeystorePass", "");
        SETTINGS.put("davmail.ssl.pkcs11Library", "");
        SETTINGS.put("davmail.ssl.pkcs11Config", "");
        SETTINGS.put("davmail.ssl.nosecurepop", Boolean.FALSE.toString());
        SETTINGS.put("davmail.ssl.nosecureimap", Boolean.FALSE.toString());
        SETTINGS.put("davmail.ssl.nosecuresmtp", Boolean.FALSE.toString());
        SETTINGS.put("davmail.ssl.nosecurecaldav", Boolean.FALSE.toString());
        SETTINGS.put("davmail.ssl.nosecureldap", Boolean.FALSE.toString());
        
        SETTINGS.put("davmail.enableKeepAlive", Boolean.TRUE.toString());

        // logging
        SETTINGS.put("davmail.logFilePath", "");
    }

    /**
     * Return DavMail log file path
     *
     * @return full log file path
     */
    public static String getLogFilePath() {
        String logFilePath = Settings.getProperty("davmail.logFilePath");
        // set default log file path
        if ((logFilePath == null || logFilePath.length() == 0)) {
            if (Settings.getBooleanProperty("davmail.server")) {
                logFilePath = "davmail.log";
            } else if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
                // store davmail.log in OSX Logs directory
                logFilePath = System.getProperty("user.home") + "/Library/Logs/DavMail/davmail.log";
            } else {
                // store davmail.log in user home folder
                logFilePath = System.getProperty("user.home") + "/davmail.log";
            }
        } else {
            File logFile = new File(logFilePath);
            if (logFile.isDirectory()) {
                logFilePath += "/davmail.log";
            }
        }
        return logFilePath;
    }

    /**
     * Return DavMail log file directory
     *
     * @return full log file directory
     */
    public static String getLogFileDirectory() {
        String logFilePath = getLogFilePath();
        if (logFilePath == null || logFilePath.length() == 0) {
            return ".";
        }
        int lastSlashIndex = logFilePath.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            lastSlashIndex = logFilePath.lastIndexOf('\\');
        }
        if (lastSlashIndex >= 0) {
            return logFilePath.substring(0, lastSlashIndex);
        } else {
            return ".";
        }
    }



    /**
     * Save settings in current file path (command line or default).
     */
    public static synchronized void save() {
        // configFilePath is null in some test cases
        if (configFilePath != null) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(configFilePath);
                SETTINGS.store(fileOutputStream, "DavMail settings");
            } catch (IOException e) {
            	//XXX - UI Listner                DavGatewayTray.error(new BundleMessage("LOG_UNABLE_TO_STORE_SETTINGS"), e);
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                    	//XXX - UI Listner                        DavGatewayTray.debug(new BundleMessage("LOG_ERROR_CLOSING_CONFIG_FILE"), e);
                    }
                }
            }
        }
    }

    /**
     * Get a property value as String.
     *
     * @param property property name
     * @return property value
     */
    public static synchronized String getProperty(String property) {
        String value = SETTINGS.getProperty(property);
        // return null on empty value
        if (value != null && value.length() == 0) {
            value = null;
        }
        return value;
    }

    /**
     * Get property value or default.
     *
     * @param property     property name
     * @param defaultValue default property value
     * @return property value
     */
    public static synchronized String getProperty(String property, String defaultValue) {
        String value = SETTINGS.getProperty(property);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }


    /**
     * Get a property value as char[].
     *
     * @param property property name
     * @return property value
     */
    public static synchronized char[] getCharArrayProperty(String property) {
        String propertyValue = Settings.getProperty(property);
        char[] value = null;
        if (propertyValue != null) {
            value = propertyValue.toCharArray();
        }
        return value;
    }

    /**
     * Set a property value.
     *
     * @param property property name
     * @param value    property value
     */
    public static synchronized void setProperty(String property, String value) {
        if (value != null) {
            SETTINGS.setProperty(property, value);
        } else {
            SETTINGS.setProperty(property, "");
        }
    }

    /**
     * Get a property value as int.
     *
     * @param property property name
     * @return property value
     */
    public static synchronized int getIntProperty(String property) {
        return getIntProperty(property, 0);
    }

    /**
     * Get a property value as int, return default value if null.
     *
     * @param property     property name
     * @param defaultValue default property value
     * @return property value
     */
    public static synchronized int getIntProperty(String property, int defaultValue) {
        int value = defaultValue;
        try {
            String propertyValue = SETTINGS.getProperty(property);
            if (propertyValue != null && propertyValue.length() > 0) {
                value = Integer.parseInt(propertyValue);
            }
        } catch (NumberFormatException e) {
        	//XXX - UI Listner            DavGatewayTray.error(new BundleMessage("LOG_INVALID_SETTING_VALUE", property), e);
        }
        return value;
    }

    /**
     * Get a property value as boolean.
     *
     * @param property property name
     * @return property value
     */
    public static synchronized boolean getBooleanProperty(String property) {
        String propertyValue = SETTINGS.getProperty(property);
        return Boolean.parseBoolean(propertyValue);
    }

    /**
     * Get a property value as boolean.
     *
     * @param property     property name
     * @param defaultValue default property value
     * @return property value
     */
    public static synchronized boolean getBooleanProperty(String property, boolean defaultValue) {
        boolean value = defaultValue;
        String propertyValue = SETTINGS.getProperty(property);
        if (propertyValue != null && propertyValue.length() > 0) {
            value = Boolean.parseBoolean(propertyValue);
        }
        return value;
    }

    /**
     * Build logging properties prefix.
     *
     * @param category logging category
     * @return prefix
     */
    private static String getLoggingPrefix(String category) {
        String prefix;
        if ("rootLogger".equals(category)) {
            prefix = "log4j.";
        } else {
            prefix = "log4j.logger.";
        }
        return prefix;
    }



    /**
     * Get all properties that are in the specified scope, that is, that start with '&lt;scope&gt;.'.
     *
     * @param scope     start of property name
     * @return properties
     */
    public static synchronized Properties getSubProperties(String scope) {
        final String keyStart;
        if (scope == null || scope.length() == 0) {
            keyStart = "";
        } else if (scope.endsWith(".")) {
            keyStart = scope;
        } else {
            keyStart = scope + '.';
        }
        Properties result = new Properties();
        for (Map.Entry entry : SETTINGS.entrySet()) {
            String key = (String)entry.getKey();
            if (key.startsWith(keyStart)) {
                String value = (String)entry.getValue();
                result.setProperty(key.substring(keyStart.length()), value);
            }
        }
        return result;
    }



    /**
     * Change and save a single property.
     *
     * @param property property name
     * @param value    property value
     */
    public static synchronized void saveProperty(String property, String value) {
        Settings.load();
        Settings.setProperty(property, value);
        Settings.save();
    }

}
