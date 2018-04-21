/* 
 * Copyright (C) 2018 Dilithium Team .
 *
 * The Dilithium library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package org.dilithium.config;

import java.io.File;

/**
 * This class contains paths to directories as well as creating them if they don't exists;
 */
public class DirectoryManagement {
    
    private static String internalParentDirectory = "dilithium-files/";    
    
    /* The safest way to get an internal path */
    public static String getInternalPath(String path){
        File file = new File(getInternalPathRaw(path));
        boolean success = file.mkdirs();
        return file.getAbsolutePath();
    }
    
    /* Adds internal parent directory to file path */
    public static String getInternalPathRaw(String path){
        return internalParentDirectory + path;
    }
    
}
