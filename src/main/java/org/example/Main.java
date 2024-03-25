package org.example;

import org.example.model.Task;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Map<String,String> config = loadConfig();

        String filename = config.get("inFile");
        Path inFilePath = Paths.get(filename);
        List<Task> tasks = getTasksFromFile(inFilePath.toFile().getAbsolutePath());

        String outFileName = config.get("fileTarget");
        Path outFilePath = Paths.get(outFileName);
        writeInFile(outFilePath.toFile().getAbsolutePath(), tasks);
    }


    private static Map<String,String> loadConfig(){
        URL url = Main.class.getResource("/dossier/config.keyvalue");

        if( url == null ){
            throw new RuntimeException("url invalid");
        }

        File configFile = new File(url.getFile());
        try( BufferedReader br = new BufferedReader(new FileReader(configFile)) ){
            return br.lines()
                    .collect(
                            HashMap::new,
                            (map, line) -> {
                                Map.Entry<String,String> entry = toEntry(line);
                                map.put(entry.getKey(), entry.getValue());
                            },
                            HashMap::putAll
                    );
        }
        catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    private static Map.Entry<String, String> toEntry(String line){
        String[] parts = line.split(":");
        return new AbstractMap.SimpleEntry<>(parts[0], parts[1]);
    }


    private static void writeInFile(String filename, Collection<Task> tasks) {
        File file = new File(filename);

        try( BufferedWriter bw = new BufferedWriter(new FileWriter(file)) ){
            bw.write("id,title,priority");
            bw.newLine();
            for (Task task : tasks) {
                bw.write(toCSVLine(task));
                bw.newLine();
            }
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }


    private static List<Task> getTasksFromFile(String filename){
        File file = new File(filename);

        try(
                FileReader reader = new FileReader(file);
                BufferedReader br = new BufferedReader(reader);
        ) {

            return br.lines()
                    .skip(1)
                    .map((line) -> fromCSVLine(line, ","))
                    .toList();
        }
        catch (IOException ex){
            throw new RuntimeException("lecture impossible");
        }
    }
    private static Task fromCSVLine(String line, String delimiter){
        String[] parts = line.split(delimiter);
        return new Task(
                Long.parseLong(parts[0]),
                parts[1],
                parts[2]
        );
    }

    private static String toCSVLine(Task task){
        return task.id()+","+task.title()+","+task.priority();
    }
}
