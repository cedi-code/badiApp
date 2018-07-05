package com.example.bgirac.badi;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *  Holt sich daten aus der der raw ordner heruas
 */
public class BadiData {
    private static ArrayList<ArrayList<String>> dataFromFile;
    private static ArrayList<ArrayList<String>> dataFromFile2;

    private BadiData(Context c) {
        Scanner scanner = new Scanner(c.getResources().openRawResource(R.raw.badi_ids_dataset));

        scanner.useDelimiter(";");

        dataFromFile = new ArrayList<ArrayList<String>>();

        while (scanner.hasNext()) {
            String dataInRow = scanner.nextLine();
            String[] dataInRowArray = dataInRow.split(";");

            ArrayList<String> rowDataFromFile = new ArrayList<String>(Arrays.asList(dataInRowArray));
            dataFromFile.add(rowDataFromFile);

        }
        scanner.close();

        Scanner scanner2 = new Scanner(c.getResources().openRawResource(R.raw.kantone));

        scanner2.useDelimiter(";");

        dataFromFile2 = new ArrayList<ArrayList<String>>();

        while (scanner2.hasNext()) {
            String dataInRow = scanner2.nextLine();
            String[] dataInRowArray = dataInRow.split(";");

            ArrayList<String> rowDataFromFile = new ArrayList<String>(Arrays.asList(dataInRowArray));
            dataFromFile2.add(rowDataFromFile);

        }
        scanner2.close();
    }
    public static ArrayList<ArrayList<String>> allBadis(Context c) {
        if(null == dataFromFile) {
            new BadiData(c);
        }
        return dataFromFile;
    }
    public static ArrayList<ArrayList<String>> allKantone(Context c) {
        if(null == dataFromFile2) {
            new BadiData(c);
        }
        return dataFromFile2;
    }

}
