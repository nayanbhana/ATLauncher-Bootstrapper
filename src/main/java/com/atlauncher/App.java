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

import java.net.MalformedURLException;
import java.net.URL;

public class App {
    private String version;
    private String md5;
    private String url;
    private String filename;

    public String getVersion() {
        return this.version;
    }

    public String getMd5() {
        return this.md5;
    }

    public URL getUrl() {
        try {
            return new URL(this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFilename() {
        return this.filename;
    }

    public boolean matches(App current) {
        if (current == null) {
            return false;
        }

        if (!current.getVersion().equalsIgnoreCase(this.getVersion())) {
            return false;
        }

        if (!current.getMd5().equalsIgnoreCase(this.getMd5())) {
            return false;
        }

        return true;
    }
}
