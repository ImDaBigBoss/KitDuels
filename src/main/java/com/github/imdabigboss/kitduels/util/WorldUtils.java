package com.github.imdabigboss.kitduels.util;

import com.github.imdabigboss.kitduels.KitDuels;

import org.bukkit.World;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WorldUtils {
    public static boolean copyWorld(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists()) {
                        target.mkdirs();
                    }

                    String[] files = source.list();
                    if (files == null) {
                        return false;
                    }

                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean deleteWorld(File path) {
        if(path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return false;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }

    public static void unloadWorld(World world) {
        KitDuels.getInstance().getServer().unloadWorld(world, true);
    }
}
