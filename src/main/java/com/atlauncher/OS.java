/*
 * ATLauncher Bootstrapper - https://github.com/ATLauncher/ATLauncher-Bootstrapper
 * Copyright (C) 2015 ATLauncher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.atlauncher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public enum OS {
    WINDOWS("win"),
    MAC("mac"),
    UNIX("nix:nux:aix"),
    SOLARIS("sunos");

    public static final String STRING = System.getProperty("os.name").toLowerCase();

    private final String key;

    private OS(String key) {
        this.key = key;
    }

    public static OS getCurrent() {
        for (OS os : OS.values()) {
            if (os.isCurrent()) {
                return os;
            }
        }

        return null;
    }

    public static Path getStorageLocation() {
        switch (getCurrent()) {
            case WINDOWS:
                return Paths.get(System.getenv("APPDATA"), ".atlauncher");
            case MAC:
                return Paths.get(System.getProperty("user.home"), "/Library/Application Support/.atlauncher");
            case SOLARIS:
            case UNIX:
            default:
                return Paths.get(System.getProperty("user.home"), ".atlauncher");
        }
    }

    public static String getDependencyName() {
        switch (getCurrent()) {
            case WINDOWS:
                return "windows";
            case MAC:
                return "mac";
            case SOLARIS:
            case UNIX:
            default:
                return "linux";
        }
    }

    public String[] getKeys() {
        return this.key.split(":");
    }

    public boolean isCurrent() {
        for (String key : this.key.split(":")) {
            if (OS.STRING.contains(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * From: https://github.com/Slowpoke101/FTBLaunch/blob/master/src/main/java/net/ftb/util/OSUtils.java#L255
     */
    public static boolean is64BitWindows() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
        return (arch.endsWith("64") || (wow64Arch != null && wow64Arch.endsWith("64")));
    }

    /**
     * From: https://github.com/Slowpoke101/FTBLaunch/blob/master/src/main/java/net/ftb/util/OSUtils.java#L255
     */
    public static boolean is64BitPosix() {
        String line, result = "";
        try {
            Process command = Runtime.getRuntime().exec("uname -m");
            BufferedReader in = new BufferedReader(new InputStreamReader(command.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += (line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 32-bit Intel Linuces, it returns i[3-6]86. For 64-bit Intel, it says x86_64
        return result.contains("_64");
    }

    /**
     * From: https://github.com/Slowpoke101/FTBLaunch/blob/master/src/main/java/net/ftb/util/OSUtils.java#L255
     */
    public static boolean is64BitOSX() {
        String line, result = "";
        if (!(System.getProperty("os.version").startsWith("10.6") || System.getProperty("os.version").startsWith("10"
                + ".5"))) {
            return true;//10.7+ only shipped on hardware capable of using 64 bit java
        }
        try {
            Process command = Runtime.getRuntime().exec("/usr/sbin/sysctl -n hw.cpu64bit_capable");
            BufferedReader in = new BufferedReader(new InputStreamReader(command.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += (line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.equals("1");
    }

    /**
     * From: https://github.com/Slowpoke101/FTBLaunch/blob/master/src/main/java/net/ftb/util/OSUtils.java#L255
     */
    public static boolean is64BitOS() {
        switch (getCurrent()) {
            case WINDOWS:
                return is64BitWindows();
            case MAC:
                return is64BitOSX();
            case UNIX:
            case SOLARIS:
            default:
                return is64BitPosix();
        }
    }

    @Override
    public String toString() {
        StringTokenizer t = new StringTokenizer(this.name());
        StringBuilder builder = new StringBuilder();

        while (t.hasMoreTokens()) {
            String token = t.nextToken();
            builder.append(token.toUpperCase().charAt(0)).append(token.toLowerCase().substring(1));
            if (t.hasMoreTokens()) {
                builder.append(" ");
            }
        }

        return builder.toString();
    }
}