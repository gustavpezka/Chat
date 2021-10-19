package Client;

import java.io.*;
import java.util.ArrayList;

public class Historian {

//1. Добавить в сетевой чат запись локальной истории в текстовый файл на клиенте.
// Для каждой учетной записи файл с историей должен называться history_[login].txt. (Например, history_login1.txt, history_user111.txt)
//2.** После загрузки клиента показывать ему последние 100 строк истории чата.

       private static FileWriter chatWriter;

       private static String getFilePath(String name){
           return "history_" + name + ".txt";
       }

       public static void historianInstance (String login){
           try {
               chatWriter = new FileWriter(getFilePath(login), true);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       public static void closeStream(){
          if (chatWriter != null){
              try {
                  chatWriter.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }

       }

       public static void writeToHistory(String message){
           try {
               chatWriter.write(message);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

       public static String getHistory(String login) throws IOException {
           BufferedReader historyReader = new BufferedReader(new FileReader(getFilePath(login)));
           ArrayList <String> history = new ArrayList<>();
           String str;
           while ((str = historyReader.readLine()) != null){
               history.add(str);
           }
           String listString = String.join(" \n", history);
           return listString;
       }
}
