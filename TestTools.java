package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 卫智熠 on 2016/3/7.
 * 该文件为测试专用。
 */

public class TestTools {

    public static void main(String[] args){
        TestTools testing = new TestTools();
        testing.testArrayList();
    }

    public void testNewClass(){
        System.out.println("test for new class is successful!");
    }

    public void test(){
        int testPoint;
        Random rand = new Random();
        testPoint = rand.nextInt(100);
        System.out.println("testPoint print");
        System.out.println(testPoint);
    }

    public void testFileReading(){
        File file = new File("E:/readme.txt");

        try {

            if (!file.exists()) {
                System.out.println("Directory " + file + " doesn't exist. ");
                file.createNewFile();
            }

            InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader( reader );
            String line = null;

            while (( line = br.readLine()) != null){
                System.out.println(line);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void testFileWriting(){
        File file = new File("E:/writeme.txt");

        try{

            if (!file.exists()) {
                System.out.println("Directory " + file + " doesn't exist. ");
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("abcdefgh\r\n");
            writer.flush();
            writer.close();

        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("writing successfully");
    }

    public void testString(){
        String testStr = "1,23,456,7890";
        int[] testContainer = new int[4];
        int iC = 0;

        for(String retval : testStr.split(",", 4)){
            testContainer[iC] = Integer.parseInt(retval);
            iC++;
        }

        System.out.println("test String begins");
        for(iC = 0; iC < 4; iC++){
            System.out.println(testContainer[iC]);
        }
    }

    public void testArrayList(){
        ArrayList arrays = new ArrayList();
        arrays.add(1);
        arrays.add(2);
        arrays.add(3);
        System.out.println(" the 2nd number in arrays is: " + arrays.get(1));

        arrays.add(4);
        arrays.remove(0);
        System.out.println("the first number in array is: " + arrays.get(0));
    }


}
