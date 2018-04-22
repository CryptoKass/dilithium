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

package org.dilithium.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * a JSON serializer, using GSON
 */
public class Json {
    
    private static Gson gson;
    private static Gson prettyGson;
    
    private static Gson getGson(){
        if(gson == null){
            gson = new GsonBuilder().create();
        }
        return gson;
    }
    
    private static Gson getPrettyGson(){
        if(prettyGson == null){
            prettyGson = new GsonBuilder().setPrettyPrinting().create();
        }
        return prettyGson;
    }
    
    public static String createJson(Object o){
        return getGson().toJson(o);
    }
    
    public static String createJsonPretty(Object o){
        return getPrettyGson().toJson(o);
    }
    
}
