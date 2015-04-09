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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap {
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final static Path PATH = OS.getStorageLocation().resolve("v4");

    public static void main(String[] args) {
        String json = null;

        if (!Files.isDirectory(PATH)) {
            try {
                Files.createDirectories(PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            json = IOUtils.toString(URI.create("http://download.nodecdn.net/containers/atl/v4/app.json"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Application application = GSON.fromJson(json, Application.class);

        Dependency currentDependency = null;

        if (Files.exists(PATH.resolve("nwjs.json"))) {
            try {
                currentDependency = GSON.fromJson(FileUtils.readFileToString(PATH.resolve("nwjs.json").toFile()),
                        Dependency.class);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        for (Dependency dependency : application.getDependencies()) {
            if (dependency.shouldInstall() && !dependency.matches(currentDependency)) {
                if (Files.isDirectory(PATH.resolve("nwjs/"))) {
                    try {
                        FileUtils.deleteDirectory(PATH.resolve("nwjs/").toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    File zipFile = PATH.resolve("nwjs/temp.zip").toFile();
                    FileUtils.copyURLToFile(dependency.getUrl(), zipFile);
                    Utils.unzip(zipFile, PATH.resolve("nwjs/").toFile());
                    FileUtils.forceDelete(zipFile);

                    currentDependency = dependency;

                    FileUtils.writeStringToFile(PATH.resolve("nwjs.json").toFile(), GSON.toJson(dependency));

                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

        App currentApp = null;

        if (Files.exists(PATH.resolve("app.json"))) {
            try {
                currentApp = GSON.fromJson(FileUtils.readFileToString(PATH.resolve("app.json").toFile()), App.class);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (!application.getApp().matches(currentApp) || (currentApp != null && !Files.exists(PATH.resolve(currentApp
                .getFilename())))) {
            if (currentApp != null && Files.exists(PATH.resolve(currentApp.getFilename()))) {
                try {
                    FileUtils.forceDelete(PATH.resolve(currentApp.getFilename()).toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileUtils.copyURLToFile(application.getApp().getUrl(), PATH.resolve(application.getApp().getFilename
                        ()).toFile());

                currentApp = application.getApp();

                FileUtils.writeStringToFile(PATH.resolve("app.json").toFile(), GSON.toJson(application.getApp()));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (currentDependency == null || currentApp == null) {
            System.exit(1);
        }

        List<String> arguments = new ArrayList<>();
        arguments.add(PATH.resolve("nwjs/").toAbsolutePath() + currentDependency.getStartup());
        arguments.add(currentApp.getFilename());
        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        processBuilder.directory(PATH.toFile());
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
