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

import java.util.List;

public class Application {
    private App app;
    private List<Dependency> dependencies;

    public App getApp() {
        return this.app;
    }

    public List<Dependency> getDependencies() {
        return this.dependencies;
    }
}
