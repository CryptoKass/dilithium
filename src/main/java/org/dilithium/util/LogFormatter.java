/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.dilithium.util;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This class 
 */
public class LogFormatter extends Formatter {
    
    @Override
    public String format(LogRecord record){
    if(record.getLevel() == Level.INFO){
      return record.getMessage() + "\r\n";
    }else{
      return "[" + record.getLevel() + "]" + record.getMessage() + "\r\n";
    }
  }
}
