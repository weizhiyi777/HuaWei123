package com.company;


import java.util.ArrayList;

/**
 * Created by 卫智熠 on 2016/3/11.
 */

public class DFSStructure extends Main{

    int pointID, costTotal, pointPreID, pointName;

    public DFSStructure(){

        this.pointID = pointsQueue1.size();
        this.costTotal = 0;
        this.pointName = 0;
        this.pointPreID = 0;
    }

    public void dataDFSIni(DataStructure[] dataArrays, int pointStart, int pointEnd){

        boolean dfsProcessFlag = true; // judge if the dfs process is completed
        int countProcess;
        System.out.println("dataDFSIni starts.");
        if(pointStart == pointEnd){
            System.out.println("Destination is same to start point. Plese check your produced data");
        }else{

            //Initial the first point.

            DFSStructure pointDFSStart = new DFSStructure();
            pointDFSStart.pointName = pointStart;
            pointDFSStart.pointPreID = -2;
            pointsQueue1.add(pointDFSStart);

            countProcess = 1;
            while(dfsProcessFlag == true){ // when dfsProcessFlag == false, means that our search has completed.

                if(countProcess % 10000000 ==0){
                    System.out.println("The time of dfs processing is: " + countProcess);

                }
                dfsProcessFlag = dataDFSMethod.dataDFSProcess(dataArrays, pointEnd, countProcess);
                countProcess++;
            }

        }

    }

    public boolean dataDFSProcess(DataStructure[] dataArrays, int pointEnd, int count){

        int pointStart;
        int iC;
        int pointTempChild;
        int pointTempLast;
        int pointChildAddNum;
        int pointResultAddr;
        boolean dfsProcessFlag;

        ArrayList pointAddr;
        DFSStructure pointChild;

        if(pointsQueue1.isEmpty() || count >= 100000000){

            //搜索结束，考虑打印搜索结果；
            System.out.println("DFS search has been completed");

            if(pointsResult.isEmpty()){
                System.out.println("DFS method didn't find an solution");
            }else{

                //打印pointsResult中的元素，并且对每个元素
                System.out.println("DFS method have found an optimistic solution");
            }
            dfsProcessFlag = false;

        }else{

            // 搜索未结束，，递归调用dataDFSProcess方法，继续搜索；
            if(((DFSStructure)pointsQueue1.get(pointsQueue1.size() - 1)).pointName == pointEnd){

                //搜索到一个目标结点，将其存储到pointResult中
                pointsResultAdd((DFSStructure)(pointsQueue1.get(pointsQueue1.size() - 1)));

                //在pointsQueue的队尾删除该节点。并判断该点是否还有兄弟节点。如果有，则访问其兄弟节点，如果没有的话，则删除其父节点。
                while(((DFSStructure)pointsQueue1.get(pointsQueue1.size() - 1)).pointPreID == pointsQueue1.size() - 2) {
                    pointsQueue1.remove(pointsQueue1.size() - 1);
                }
                pointsQueue1.remove(pointsQueue1.size() - 1);
                dfsProcessFlag = true;

            }else{

                /*判断该节点是否有子节点：
                      如果有子节点，将子节点依次加入到pointsQueue的队尾，然后访问队尾的那个节点。
                      如果没有子节点，则删除该点。然后判断其是否有兄弟节点。如果有，则访问其兄弟节点，如果没有的话，则删除其父节点。
                 */

                //判断是否有子节点：
                pointTempLast = pointsQueue1.size() - 1;
                pointStart = ((DFSStructure)pointsQueue1.get(pointTempLast)).pointName;
                pointAddr = dataDFSMethod.pointStartSearch(dataArrays, pointStart);

                if(!(pointAddr.size() == 0)) {
                    //如果有子节点，则通过回溯的方法判断子节点是否存在于之前的路径中，否则不予理会


                    pointChildAddNum = 0;
                    for (iC = 0; iC < pointAddr.size(); iC++) {
                        pointTempChild = dataArrays[(int) pointAddr.get(iC)].destinationID;
                        if (dataDFSMethod.pointPreJudge(((DFSStructure) pointsQueue1.get(pointTempLast)), pointTempChild)) {

                            //如果子节点不存在于之前的路径中
                            pointChild = new DFSStructure();
                            pointChild.costTotal = ((DFSStructure)pointsQueue1.get(pointTempLast)).costTotal + dataArrays[(int) pointAddr.get(iC)].costID;
                            pointChild.pointPreID = pointTempLast;
                            pointChild.pointName = pointTempChild;
                            pointsQueue1.add(pointChild);
                            pointChildAddNum ++ ;
                            pointChild = null;
                        }
                    }

                    if(pointChildAddNum > 0){

                        //有新节点加入，则继续递归调用函数访问队尾的节点。
                        dfsProcessFlag = true;

                    }else{

                        //如果没有子节点，则判断pointsQueue的最后的元素是否是倒数第二的元素的子节点
                        while(((DFSStructure)pointsQueue1.get(pointsQueue1.size() - 1)).pointPreID == (pointsQueue1.size() - 2)){

                            //是子节点
                            pointsQueue1.remove(pointsQueue1.size() - 1);

                        }

                        pointsQueue1.remove(pointsQueue1.size() - 1);

                        dfsProcessFlag = true;

                    }

                    pointAddr = null;

                }else{

                    //如果没有子节点，则判断pointsQueue的最后的元素是否是倒数第二的元素的子节点


                    while (((DFSStructure)pointsQueue1.get(pointsQueue1.size() - 1)).pointPreID == (pointsQueue1.size() - 2)){


                        pointsQueue1.remove(pointsQueue1.size() - 1);

                    }

                    pointsQueue1.remove(pointsQueue1.size() - 1);

                    dfsProcessFlag = true;
                }
            }
        }

        if(count >= 100000000){
            System.out.println("the process count is over 100000000.");
            dfsProcessFlag = false;
        }

        return dfsProcessFlag;
    }

    public ArrayList pointStartSearch(DataStructure[] dataArrays, int pointStart){

        int iC;
        ArrayList pointAddr = new ArrayList();

        for(iC = 0; iC < dataArrays.length; iC++){
            if(dataArrays[iC].sourceID == pointStart){
                pointAddr.add(iC);
            }
        }

        return pointAddr;
    }

    public boolean pointPreJudge(DFSStructure pointStart, int pointChild){
        boolean preFlag;
        int pointStartPre;

        //已知pointChild 是 pointStart 的子节点，所以 pointChild ！= pointStart.pointName.
        //所以要判断pointChild是否等于pointStart的父节点，即((DFSStructure)pointsQueue.get(pointStart.pointPreID)).pointName

        if(pointStart.pointPreID != -2) {
            //如果父节点不是第一个最初的节点
            pointStartPre = ((DFSStructure) pointsQueue1.get(pointStart.pointPreID)).pointName;
            if (pointChild == pointStartPre) {
                preFlag = false;
                return preFlag;
            } else {
                preFlag = dataDFSMethod.pointPreJudge(((DFSStructure) pointsQueue1.get(pointStart.pointPreID)), pointChild);
                return preFlag;
            }
        }else{
            //如果父节点是最初的节点,直接返回ture
            preFlag = true;
            return preFlag;
        }


    }

    public void resultPrint(int pointResultAddr){

    }

    public void pointsResultAdd(DFSStructure pointAdd){

        int pointPreCost;
        int pointAddCost;

        if(pointsResult.size() == 0){
            pointsResult.add(pointAdd);
        }else{

            pointAddCost = pointAdd.costTotal;
            pointPreCost = ((DFSStructure)pointsResult.get(pointsResult.size() - 1)).costTotal;
            if(pointPreCost < pointAddCost){
                pointsResult.add(pointAdd);
            }else if(pointPreCost == pointAddCost){
                pointsResult.add(pointAdd);
            }else{
                ;
            }

        }

    }


}
