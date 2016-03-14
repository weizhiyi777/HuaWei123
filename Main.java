package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    final static int lineNumMax = 100; // 600*8=4800
    final static int lineStartMax = 4;
    final static int pointMax = 25;
    final static int costMax = 10;
    final static int costIfi = 100000;
    final static int antNumMax = lineNumMax * 10;
    static int pointsQueueStartNum = 0;
    static ArrayList pointsQueue1 = new ArrayList();

    final static DFSStructure dataDFSMethod0 = new DFSStructure(); //用于执行dfs的各种方法
    final static Main dataMethod = new Main(); // 用于执行bfs的各种方法
    final static AntColony dataAntMethod = new AntColony(); // 用于执行蚁群算法的各种方法

    static DFSStructure dataDFSMethod = new DFSStructure();
    static ArrayList pointsResult = new ArrayList();
    static int pointBFSNum = 0;

    public static void main(String[] args) {

        int produceFlag = 0; // 决定是否产生数据
        int dataReadFlag = 1;// 决定是否读取数据并进行处理和计算

	    System.out.println("Welcome to  the Code Craft Competition.");
        System.out.println();


        if(produceFlag == 1){
            dataMethod.dataProducing(lineNumMax, pointMax, lineStartMax, costMax);
        }

        if(dataReadFlag ==1){
            dataMethod.dataFileReading(); // 数据读取和计算
        }

    }

    public void dataProducing(int lineNumMax, int pointMax, int lineStartMax, int costMax){

        /*
        随机产生数据，并保存在数组中；
        随后，输出到.txt的文件中
         */

        int iC, jC, lengthData, pointTemp;
        double xNum;
        Random rand = new Random();

        int[] linkRandom = new int[lineNumMax];
        for(iC = 0; iC < linkRandom.length; iC++){
            xNum  = Math.random();
            if(xNum < 0.5){
                linkRandom[iC] = 0;
            }
            else {
                linkRandom[iC] = 1;
            }
        }

        for(iC = 0, jC = 0; iC < linkRandom.length; iC++){
            if(linkRandom[iC] == 1){
                jC++;
            }
        }

        lengthData = jC;

        int[] linkID = new int[lengthData];
        int[] sourceID = new int[lengthData];
        int[] pointID = new int[pointMax];
        int[] destinationID =  new int[lengthData];
        int[] costID  = new int[lengthData];

        for(iC = 0, jC = 0; jC < linkRandom.length; jC++){
            if(linkRandom[jC] == 1){
                linkID[iC] = jC;
                iC++;
            }
        }

        for(iC = 0; iC < pointID.length; iC++){
            pointID[iC] = 0;
        }

        for(iC = 0; iC < lengthData; iC++){
            pointTemp = rand.nextInt(pointMax);
            if(pointID[pointTemp] < lineStartMax){
                pointID[pointTemp]++;
                sourceID[iC] = pointTemp;
            }
            else{
                iC--;
            }
        }

        for(iC = 0; iC < lengthData; iC++){
            pointTemp = rand.nextInt(pointMax);
            if(pointTemp != sourceID[iC]){
                destinationID[iC] = pointTemp;
            }
            else{
                iC--;
            }
        }

        for(iC = 0; iC < lengthData; iC++){
            costID[iC] = 1+ rand.nextInt(costMax);
        }

        dataMethod.dataPrintToFile(linkID, sourceID, destinationID, costID);
    }

    public void dataPrintToFile(int[] linkID, int[] sourceID, int[] destinationID, int[] costID){
        // Write produced data in one txt file, in certain consequence.

        File dataFile = new File("E:/datame.txt");
        int dataLength = linkID.length;
        int iC;

        try{
            if(!dataFile.exists()) {
                System.out.println("File " + dataFile + " doesn't exist.");
                dataFile.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile));

            for(iC = 0; iC < dataLength; iC++) {
                writer.write(linkID[iC] +"," + sourceID[iC] + "," + destinationID[iC] + "," +costID[iC] +"\r\n");
                writer.flush();
            }
            writer.write(sourceID[0] + "," + destinationID[dataLength - 1] + "\r\n");
            writer.close();

        }catch(IOException e){
            e.printStackTrace();

        }
    }

    public void dataFileReading(){

        /*
        从.txt文件中读取数据，然后存储在对象数组 dataArrays 中
         */

        File dataFile = new File("E:/datame.txt");
        int dataNumMax = 4800;
        int dataNum = 0;
        int iC, jC,bfsFlag, dfsFlag, antFlag;
        int[] dataContainer = new int[4];
        int pointStart;
        int pointEnd;

        bfsFlag = 0; //是否使用bfs算法进行计算
        dfsFlag = 0; //是否使用dfs算法进行计算
        antFlag = 1; //是否使用蚁群算法进行计算

        String[] dataLine = new String[dataNumMax];

        try{

            if(!dataFile.exists()){
                System.out.println(dataFile + " doesn't exist. ");
                dataFile.createNewFile();
            }

            InputStreamReader reader = new InputStreamReader(new FileInputStream(dataFile));
            BufferedReader br = new BufferedReader(reader);
            String line = null;

            iC = 0;
            while (( line = br.readLine()) != null){
                dataLine[iC] = line;
                iC++;
            }
            dataNum = iC;
        }catch(IOException e){
            e.printStackTrace();
        }

        DataStructure[] dataArrays = new DataStructure[dataNum - 1];
        for(iC = 0; iC < (dataNum - 1); iC++){
                dataArrays[iC] = new DataStructure();
        }

        for(iC = 0; iC < (dataNum - 1); iC++){
            jC = 0;

            for(String retval : dataLine[iC].split(",", 4)){
                dataContainer[jC] = Integer.parseInt(retval);
                jC++;
            }

            //数据记录
            dataArrays[iC].linkID = dataContainer[0];
            dataArrays[iC].sourceID = dataContainer[1];
            dataArrays[iC].destinationID = dataContainer[2];
            dataArrays[iC].costID = dataContainer[3];
        }

        jC = 0;
        for(String retval : dataLine[dataNum-1].split(",",2)){
            dataContainer[jC] = Integer.parseInt(retval);
            jC++;
        }
        pointStart = dataContainer[0];
        pointEnd = dataContainer[1];

        System.out.println("DFSProcess almost starts here");

        if(bfsFlag == 1){
            dataMethod.dataBFSIni(dataArrays, pointStart, pointEnd);
            System.out.println("BFS Process starts here");
        }

        if(dfsFlag == 1){
            dataDFSMethod0.dataDFSIni(dataArrays, pointStart, pointEnd);
            System.out.println("DFS Process starts here");
        }

        if(antFlag == 1){
            dataAntMethod.antIni(dataArrays, pointStart, pointEnd);
            System.out.println("Ant Colony Optimization starts here");
        }


    }

    public void dataBFSIni(DataStructure[] dataArrays, int pointStart, int pointEnd){

        /*
        该函数为BFS算法的初始化。
        ArrayList pointsQueue 用来代表BFS中用到的队列，先入先出，里面存放的是BFSStructure类型的数据；
         */

        ArrayList pointsQueue = new ArrayList();
        ArrayList pointsBFSResult = new ArrayList();
        ArrayList pointResSequence = new ArrayList();
        BFSStructure pointStartBFS = new BFSStructure();


        /*
        根据(int)起始点，并构造(BFSStructure)起始点，加在pointsQueue作为第一个点。
        然后判断起始点是否等于目标点，随后开始BFS算法进行路径的计算。
         */

        pointStartBFS.pointName = pointStart;
        pointStartBFS.costTotal = 0;
        pointStartBFS.pointPreID = -1; // 起始点的父节点为 -1 ;

        pointsQueue.add(pointStartBFS);

        if( pointEnd == ((BFSStructure)pointsQueue.get(pointsQueueStartNum)).pointName){
                        ;
        }else{

            dataMethod.dataProcessBFS(dataArrays, pointsQueue, pointsBFSResult, pointResSequence, pointEnd);

        }

    }

    public void dataProcessBFS(DataStructure[] dataArrays, ArrayList pointsQueue, ArrayList pointsBFSResult, ArrayList pointsResSequence, int pointEnd){

        /*

        ArrayList pointsQueue 是一个队列，先入先出，队列里面存放的是BFSStructure类型的对象数据；

        BFS算法的流程主要是：
        先找到pointsQueue中的第一个对象，然后根据对象的点的序号，看它是否有子节点。
            如果有子节点，判断其子节点是否是其父节点中pointsPre中的节点：
                如果是，则忽略掉该节点；
                如果不是，则将该子节点加入到pointsQueue的结尾，并修改各子节点的pointPre以及costTotal;随后删除pointsQueue中的第一个节点。
            如果没有子节点，则pointsQueue的起始指针加1，相当于删除pointsQueue中的第一个节点。
        pointsQueue的起始指针加1后：
            如果 起始指针 = pointsQueue.size(), 即 pointsQueue 为空，则打印："搜索完毕", 并判断pointsBFSResult是否为空：
                如果为空，则打印：“此问题无解”；
                如果不为空，找到pointsBFSResult中的costTotal最小的那个元素，打印其pointsPre，以及costTotal值。
            如果 起始指针 < pointsQueue.size(), 即 pointsQueue 不为空，则判断此时pointsQueue中的第一个节点（起始指针指示的节点）是否为目标结点：
                如果是目标结点，则将其添加到pointsBFSResult中，继续执行该BFS算法；
                如果不是目标节点，直接继续执行该BFS算法。

         */

        ArrayList pointAddr ;

        int iC;
        int pointTempAddr;
        int pointTempDes;
        int pointTempCost;
        int pointTempCostNum = 0;
        int pointStart;

        BFSStructure[] pointsAdded = new BFSStructure[lineStartMax + 1];


        pointStart = ((BFSStructure)pointsQueue.get(pointsQueueStartNum)).pointName;
        pointAddr = pointSearch(dataArrays, pointStart); // return an ArrayList, containing the (int)serial number of children points.

        System.out.println("The size of ArrayList pointsQueue is: " + (pointsQueue.size() - pointsQueueStartNum));

        if(pointAddr.size() != 0){

            if(!(pointStart == pointEnd)) {
                for (iC = 0; iC < pointAddr.size(); iC++) {

                    pointTempAddr = (int) pointAddr.get(iC);
                    pointTempDes = dataArrays[pointTempAddr].destinationID; // The serial number of children points.

                    if (dataMethod.pointPreJudge((BFSStructure) pointsQueue.get(pointsQueueStartNum), pointsQueue, pointTempDes)) {

                        pointsAdded[iC] = new BFSStructure();
                        pointsQueue.add(dataMethod.pointQueueAdded(pointsAdded[iC], (BFSStructure) pointsQueue.get(pointsQueueStartNum), dataArrays[pointTempAddr].costID, pointTempDes));

                    } else {

                    }
                }

                for(iC = 0; iC < pointsAdded.length; iC++){
                    pointsAdded[iC] = null;
                }
            }

            pointsQueueStartNum ++;

        }else{

            pointsQueueStartNum ++;

        }


        if(pointsQueueStartNum == pointsQueue.size()){ //if pointsQueue is empty


            if(pointsBFSResult.isEmpty()){

                System.out.println("No solution.");

            }else{

                iC = 0;
                pointTempCost = costIfi;
                while(iC < pointsBFSResult.size()){
                    if(pointTempCost > ((BFSStructure)pointsBFSResult.get(iC)).costTotal){

                        pointTempCost = ((BFSStructure)pointsBFSResult.get(iC)).costTotal;
                        pointTempCostNum = iC;

                    }

                    iC++;
                }


                pointsResSequence.add(0, ((BFSStructure)pointsBFSResult.get(pointTempCostNum)).pointName);
                dataMethod.pointResSequencePrint((BFSStructure)pointsBFSResult.get(pointTempCostNum), pointsQueue, pointsResSequence);

                dataMethod.pointResPrint(pointsBFSResult);
                dataMethod.pointBFSPrint((BFSStructure)pointsBFSResult.get(pointTempCostNum));

            }

        }else{

           if(pointEnd == ((BFSStructure)pointsQueue.get(pointsQueueStartNum)).pointName){

               pointsBFSResult.add(pointsQueue.get(pointsQueueStartNum));

           }

            dataMethod.dataProcessBFS(dataArrays, pointsQueue, pointsBFSResult, pointsResSequence, pointEnd);

        }



    }

    public ArrayList pointSearch(DataStructure[] dataArrays, int pointS){

        /*
        The function is to search the children points of one given point.
        Return one arraylist that contains the serial numbers of these children points.
         */

        ArrayList pointAddr = new ArrayList();
        int dataNum = dataArrays.length;
        int iC;

        for(iC = 0; iC < dataNum; iC++){
            if(dataArrays[iC].sourceID == pointS){
                pointAddr.add(iC);
            }
        }

        return pointAddr;
    }




    public BFSStructure pointQueueAdded(BFSStructure pointDes, BFSStructure pointIni, int costAdded, int pointTempDes){

        pointDes.pointName = pointTempDes;
        pointDes.costTotal = pointIni.costTotal + costAdded;
        pointDes.pointPreID = pointIni.pointID;

        return pointDes;
    }

    public void pointBFSPrint(BFSStructure pointResult){

        int iC;
        System.out.println("The result is as following:");
        System.out.println();



        System.out.println();
        System.out.println("The total cost is: " + pointResult.costTotal);

    }

    public void pointResPrint(ArrayList pointBFSResult){
        int iC = 0;
        int jC = 0;

        System.out.println("Now let us print ArrayList pointBFSResult");
        System.out.println();

        for(iC = 0; iC < pointBFSResult.size(); iC++){

            System.out.println(((BFSStructure)pointBFSResult.get(iC)).pointName + " , " + ((BFSStructure)pointBFSResult.get(iC)).costTotal);

        }

        System.out.println();

    }

    public void pointQueuePrint(ArrayList pointQueue){
        int iC = 0;

        System.out.println("Now let us print ArrayList pointQueue: ");

        for(iC = 0; iC < pointQueue.size(); iC++){
            System.out.print(((BFSStructure)pointQueue.get(iC)).pointID + " , ");
        }
        System.out.println();

    }

    public boolean pointPreJudge(BFSStructure pointStart, ArrayList pointsQueue, int pointJudge){

        /*
        pointJudge 是 pointStart 的子节点，所以 pointJudge 肯定和 pointStart.pointName 不同；
        因此，根据 pointStart.pointPreID 找到其父节点，然后判断 父节点.pointName 是否等于 pointJudge;
            如果等于，返回 false，代表该子节点在之前的路径中已经包含，故舍去；
            如果不等于，判断该父节点是否为最初节点：
                如果是，则返回trun，代表该子节点在之前的路径并没有包含，可以加入到 pointQueue 中；
                如果不是，则递归调用函数 pointPreJudge;
         */

        boolean flag;
        int iC = 0;
        boolean pointFoundFlag = false;

        while((!pointFoundFlag) && (iC < pointsQueue.size())){

            if(((BFSStructure)pointsQueue.get(iC)).pointID == pointStart.pointPreID){

            pointFoundFlag = true;

            }else{
                iC++;
            }
        }

        if(pointFoundFlag) {

            if (((BFSStructure) pointsQueue.get(iC)).pointName == pointJudge) {

                return false;

            } else if (((BFSStructure) pointsQueue.get(iC)).pointPreID == -1) {

                return true;

            } else {

                flag = dataMethod.pointPreJudge(((BFSStructure) pointsQueue.get(iC)), pointsQueue, pointJudge);
                return flag;

            }

        }else{

            return true;

        }
    }

    public void pointResSequencePrint(BFSStructure pointDes, ArrayList pointsQueue, ArrayList pointResSequence){

        int iC = 0;
        boolean pointFoundFlag = false;

        while((!pointFoundFlag) && (iC < pointsQueue.size())){

            if(((BFSStructure)pointsQueue.get(iC)).pointID == pointDes.pointPreID){

                pointFoundFlag = true;
                pointResSequence.add(0, ((BFSStructure)pointsQueue.get(iC)).pointName);

            }else{
                iC++;
            }
        }

        if(((BFSStructure)pointsQueue.get(iC)).pointPreID == -1){
            int jC;
            for (jC = 0; jC < (pointResSequence.size() - 1); jC++){
                System.out.print((int)pointResSequence.get(jC) + " , ");
            }
            System.out.print((int)pointResSequence.get(jC));
            System.out.println();
        }
        else{
            pointResSequencePrint(((BFSStructure)pointsQueue.get(iC)), pointsQueue, pointResSequence);
        }

    }

}
