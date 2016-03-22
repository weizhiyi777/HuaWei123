package com.company;

import java.util.ArrayList;
import java.util.Random;
/**
 * Created by 卫智熠 on 2016/3/14.
 */
public class AntColony extends Main{


    static int antIDNum;
    static int startPoint;
    static int endPoint;
    static ArrayList antResult;
    static ArrayList antResult1 = new ArrayList();
    static ArrayList antResult2 = new ArrayList();
    int antID, antPlaceNow, antPlaceDes, antWait, antPreFlag, antReturnFlag;
    boolean antFood;
    ArrayList antPath;
    Random rand = new Random();


    public AntColony(){

        /*
        假设蚂蚁单位时间内移动的距离为1。设A-->B的距离为x，B-->C的距离为y, 则一直蚂蚁ant从A移动到B所需要x的时间。其等价为：
        ant 0时刻在A点，antPlaceNow = A, antWait = 0；然后根据要求，选择ant的目的地为B点，然后令 antWait = x， 表示ant还需要等待x的时间才能到达B点，即现在距离B的距离为x；
        ant 1时刻在A点，antPlaceNow = A, antWait = x - 1;表示ant还处于等待状态，需要等待x-1的时间才能到达B点。
        ……
        ant x时刻在A点，antPlaceNow = A, antWait = 0;
        因为：antWait == 0 {
            antPlaceNow = B; antPlaceDes = C;
            antWait = y
        } 周而复始
         */

        this.antID = antIDNum; // 蚂蚁编号
        antIDNum ++ ;
        this.antPlaceNow = 0; // 蚂蚁此刻所处的位置
        this.antPlaceDes = 0; //蚂蚁将要去的位置
        this.antWait =  0; // 蚂蚁此刻是否处于等待状态。0代表不需要等待，可以出发，大于零的任意整数 x 代表所需要等待的时间。
        this.antFood = false; // 蚂蚁此刻是否拥有从终点取得的食物。有食物，则要在节点释放信息素，没有食物，则不在结点释放信息素。
        this.antPreFlag = 0; // antPath 共分为两部分，第一部分是经过的点，第二部分是搜索过程中遇到的死胡同。所以antPreFlag是分界线,其值表示的是antPath中经过点的个数。
        this.antReturnFlag = 0; // 如果是遇到无子节点原路返回的情况，则antReturnFlag++,其值表示连续原路返回经过的点的个数。
        this.antPath = new ArrayList();

    }

    static AntColony[] antColonies = new AntColony[antNumMax];

    public void antIni(DataStructure[] dataArrays, int pointStart, int pointEnd){
        int iC;
        int timeMax = 10000; //蚁群算法运行的最多时间

        startPoint = pointStart;
        endPoint = pointEnd;

        //初始化antNumMax数量的蚂蚁
        for(iC = 0; iC < antColonies.length; iC++){
            antColonies[iC] = new AntColony();
            antColonies[iC].antPlaceNow = pointStart;
            antColonies[iC].antPlaceDes = pointStart;
        }
        System.out.println("PointStart is: " + startPoint + ", and pointEnd is: " + endPoint);

        //开始运行蚁群算法
        for(iC = 0; iC < timeMax; iC++){
//            System.out.println("Ant colony algorithm has run " + (iC + 1) + " times.");
            dataAntMethod.antProcess(dataArrays);
        }

        //输出蚁群算法的计算结果
//        System.out.println("Now show the result of ant colony algorithm");
        dataAntMethod.antResultShow(dataArrays);
    }

    public void antProcess(DataStructure[] dataArrays){
        int iC;

        /*
        当前时刻，检查蚁群中每一只蚂蚁的状态。并根据检查结果更新单只蚂蚁的状态。
        每执行一次当前的方法，则表示经过了一个单位时间。
         */
        for(iC = 0; iC < antNumMax; iC++){
//            System.out.println("This is the " + iC + "th ant.");
            dataAntMethod.antUpdate(dataArrays, antColonies[iC]);
        }

        /*
        更新地图信息：
        如果路径中有信息素，则每个单位时间内-1，如果没有，则一直保持在0，不会变成负数。
         */
        for(iC = 0; iC < dataArrays.length; iC++){
            if(dataArrays[iC].infoMount > 0){
                dataArrays[iC].infoMount -- ;
            }
        }
    }

    public void antUpdate(DataStructure[] dataArrays, AntColony antIndividual){
        int arrayNum;

        //对于不处于等待状态的蚂蚁进行设置
        if(antIndividual.antWait == 0){

            if(!antFood) {
                //为ant选择一条路径，arrayNum指的是所选路径对应的map中的序号
                arrayNum = dataAntMethod.antPathChoose(dataArrays, antIndividual);
//                System.out.println("ArrayNum is: " + arrayNum);

                //更改ant的状态
                dataAntMethod.antSet(dataArrays, antIndividual, arrayNum);
            }else{
                //ant释放信息素
                arrayNum = -2;
                dataAntMethod.antSet(dataArrays, antIndividual, arrayNum);
//                dataArrays[arrayNum].infoMount = dataArrays[arrayNum].infoMount + 100;
            }

        }else{

            //更改ant的等待状态；
            antIndividual.antWait -- ;
        }

    }

    public int antPathChoose(DataStructure[] dataArrays, AntColony antIndividual){
        int arrayNum = 0;
        ArrayList pointChildAddr;

        //计算得到antIndivitial.antPlaceNow的子节点。
        pointChildAddr = dataAntMethod.pointChildSearch(dataArrays, antIndividual);

        //在得到的子节点中“随机”选择下一个目标结点，返回的是所选路线对应的map中的序号
        arrayNum = dataAntMethod.antDesChoose(dataArrays, antIndividual,pointChildAddr);

        //返回选择结果
        return arrayNum;
    }

    public ArrayList pointChildSearch(DataStructure[] dataArrays, AntColony antIndividual){
        ArrayList pointChildAddr = new ArrayList();
        //获取ant当前的位置
        int placeNow = antIndividual.antPlaceDes;
        int iC;

        /*
        扫描地图dataArrays, 当发现sourceID == placeNow时，查找destinationID.
        如果ant.antPath 包含 destinationID，则舍去，不予理会；
        如果不包含，则将对应的dataArrays的序号添加到pointChildAddr。
         */

        for(iC = 0; iC < dataArrays.length; iC++){
            if(dataArrays[iC].sourceID == placeNow){
                if(!antIndividual.antPath.contains(dataArrays[iC].destinationID)){
                    pointChildAddr.add(iC);
                }
            }
        }

        return pointChildAddr;
    }

    public int antDesChoose(DataStructure[] dataArrays, AntColony antIndividual, ArrayList pointChildAddr){
        int arrayNum = 0;

        if(!pointChildAddr.isEmpty()){

            //当前位置有子节点,并获取子节点的数量
            int iC;
            int pointChildNum = pointChildAddr.size();

            if(pointChildNum > 1){

                //如果不只有一个子节点
                int infoTotal = 0;
                //取出pointChildAddr中的元素
                int addrNum;
                //存放各个子节点的信息素的量，以便后续进行随机选择目标结点
                int[] pointChilds = new int[pointChildNum];

                for(iC = 0; iC < pointChildNum; iC++){
                    addrNum = (int)pointChildAddr.get(iC);
                    pointChilds[iC] = dataArrays[addrNum].infoMount + infoTotal;
                    infoTotal = infoTotal + dataArrays[addrNum].infoMount;
                }

                //信息素总量为0
                if(infoTotal == 0){
                    //信息素总量为0
                    arrayNum = rand.nextInt(pointChildNum);
                    arrayNum = (int)pointChildAddr.get(arrayNum);

                }else{

                    //信息素总量大于0
                    int mountTemp = 1 + rand.nextInt(infoTotal);
                    iC = 0;
                    boolean flagLoop = false;
                    while(iC < pointChildNum && (!flagLoop)){
                        if(mountTemp <= pointChilds[iC]){
                            flagLoop = true;
                        }
                        iC++;
                    }
                    if(!flagLoop){
                        System.out.println("There is something wrong in antDesChoose");
                    }else{
                        arrayNum = iC - 1;
                        arrayNum = (int)pointChildAddr.get(arrayNum);
                    }
                }

            }else{

                //如果只有一个子节点
                arrayNum = (int)pointChildAddr.get(0);
            }

            return arrayNum;

        }else{

            //当前位置没有子节点，返回-1
            arrayNum = -1;
            return arrayNum;
        }

    }

    public void antSet(DataStructure[] dataArrays, AntColony antIndividual, int arrayNum){

        int pointNext;
        int pathFlag;
        int costTemp;

        //check if this ant carries food from destination.
        if(!antIndividual.antFood) {
            //This ant doesn't carry food from destination.

            //check if the antIndividual.antPlaceDes is the endPoint
            if (antIndividual.antPlaceDes != endPoint) {
                //check the value of arrayNum.
                if (arrayNum > (-1)) {
                    //arrayNum > -1, means that current point has children point.
                    //更新ant的各项状态
                    pointNext = antIndividual.antPlaceDes;

                    antIndividual.antPath.add(antIndividual.antPreFlag, pointNext);
                    antIndividual.antPreFlag++;
                    antIndividual.antPlaceNow = antIndividual.antPlaceDes;
                    antIndividual.antPlaceDes = dataArrays[arrayNum].destinationID;
                    antIndividual.antReturnFlag = 0;
                    antIndividual.antWait = dataArrays[arrayNum].costID - 1;

                } else {
                    //arrayNum == -1, means that current point doesn't have children point.
                    pointNext = antIndividual.antPlaceDes;

                    antIndividual.antPath.add(antIndividual.antPreFlag, pointNext);
                    antIndividual.antPlaceNow = pointNext;
                    pathFlag = antIndividual.antPreFlag - 1;
                    antIndividual.antPlaceDes = (int) antIndividual.antPath.get(pathFlag);
                    antIndividual.antReturnFlag++;

                    costTemp = dataAntMethod.costSearch(dataArrays, antIndividual.antPlaceDes, antIndividual.antPlaceNow);
                    if (costTemp > -1) {
                        antIndividual.antWait = 20;
                    } else {
                        antIndividual.antWait = costTemp - 1;
                    }

                    antIndividual.antPath.remove(pathFlag);
                    antIndividual.antPreFlag--;

                    if (antIndividual.antReturnFlag > 1) {
                        dataAntMethod.pathRemove(antIndividual);
                        antIndividual.antReturnFlag = 1;
                    }
                }
            } else {
                //antPlaceDes is the endPoint

                pointNext = antIndividual.antPlaceDes;
                antIndividual.antPlaceNow = pointNext;
                antIndividual.antPlaceDes = (int) antIndividual.antPath.get(antIndividual.antPreFlag - 1);

                costTemp = dataAntMethod.costSearch(dataArrays, antIndividual.antPlaceDes, antIndividual.antPlaceNow);
                if (costTemp > -1) {
                    antIndividual.antWait = 20;
                } else {
                    antIndividual.antWait = costTemp - 1;
                }

                antIndividual.antPath.remove(antIndividual.antPreFlag - 1);
                antIndividual.antPreFlag--;

                antIndividual.antFood = true;
                dataAntMethod.pathRemove(antIndividual);
                antIndividual.antReturnFlag = 0;

            }
        }else{
            // This ant carries food from destination.

            int pointNow;
            int infoLineNum;
            if(antIndividual.antPlaceDes != startPoint){

                //The coming place is not startPoint. Keep going.
                pointNext = antIndividual.antPlaceDes;
                pointNow = antIndividual.antPlaceNow;

                infoLineNum = lineSearch(dataArrays, pointNext, pointNow);
                dataAntMethod.infoAdd(dataArrays, infoLineNum, pointNow, pointNext);

                antIndividual.antPlaceNow = pointNext;
                antIndividual.antPlaceDes = (int) antIndividual.antPath.get(antIndividual.antPreFlag - 1);

                costTemp = dataAntMethod.costSearch(dataArrays, antIndividual.antPlaceDes, antIndividual.antPlaceNow);
                if (costTemp > -1) {
                    antIndividual.antWait = 20;
                } else {
                    antIndividual.antWait = costTemp - 1;
                }

                antIndividual.antPath.remove(antIndividual.antPreFlag - 1);
                antIndividual.antPreFlag--;
            }else{
                //The coming place is startPoint. Initial the ant's factors.

                pointNext = antIndividual.antPlaceDes;
                pointNow = antIndividual.antPlaceNow;
                infoLineNum = lineSearch(dataArrays, pointNext, pointNow);
                dataAntMethod.infoAdd(dataArrays, infoLineNum, pointNow, pointNext);

                antIndividual.antPlaceDes = startPoint;
                antIndividual.antPlaceNow = startPoint;
                antIndividual.antWait =  0;
                antIndividual.antFood = false;
                antIndividual.antPreFlag = 0;
                antIndividual.antReturnFlag = 0;
                antIndividual.antPath.clear();
            }

        }
    }

    public int costSearch(DataStructure[] dataArrays, int pointStart, int pointEnd){
        int lineNum;
        int cost;

        lineNum = lineSearch(dataArrays, pointStart, pointEnd);

        if(lineNum > (-1)){
            cost = dataArrays[lineNum].costID;
            return cost;
        }else{
            cost = -1;
            return cost;
        }
    }

    public int lineSearch(DataStructure[] dataArrays, int pointStart, int pointEnd){
        int lineNum = -1;
        int iC;

        for(iC = 0; iC < dataArrays.length; iC++){
            if((dataArrays[iC].sourceID == pointStart) && (dataArrays[iC].destinationID == pointEnd)){
                lineNum = iC;
                return lineNum;
            }
        }

        return lineNum;
    }

    public void pathRemove(AntColony antIndividual){
        int preFlag = antIndividual.antPreFlag;
        int pathLength = antIndividual.antPath.size();
        int iC;

        for(iC = pathLength - 1; iC > preFlag; iC --){
            antIndividual.antPath.remove(iC);
        }
    }

    public void infoAdd(DataStructure[] dataArrays, int infoLineNum, int pointNow, int pointNext){

        dataArrays[infoLineNum].infoMount = dataArrays[infoLineNum].infoMount + infoMountAdd;
    }

    public void antResultShow(DataStructure[] dataArrays){
        int iC;
        int infoLimit;

        int detailShowFlag = 0;
        int arraylistShowFlag = 1;

        System.out.println("Here is the result of Ant Colony Optimization about this question");
        System.out.println();

        dataAntMethod.antResultSort(dataArrays);

        if(!dataAntMethod.antResultJudge(dataArrays)){

            System.out.println("There is no solution.");

        }else {

            if (detailShowFlag == 1) {
                infoLimit = dataArrays[0].infoMount / 50;
                for (iC = 0; iC < dataArrays.length; iC++) {
                    if (dataArrays[iC].infoMount > infoLimit) {
                        System.out.println("LinkID: " + dataArrays[iC].linkID + ", " + dataArrays[iC].sourceID + " - > " + dataArrays[iC].destinationID + ", cost: " + dataArrays[iC].costID + ", infoMount: " + dataArrays[iC].infoMount);
                    }
                }
            }

            if(arraylistShowFlag == 1){
                int numTemp;
                int costTotal = 0;
                for(iC = 0; iC < antResult.size(); iC++){
                    numTemp = (int)antResult.get(iC);
                    costTotal = costTotal + dataArrays[numTemp].costID;
                    System.out.println("LinkID: " + dataArrays[numTemp].linkID + ", " + dataArrays[numTemp].sourceID + " - > " + dataArrays[numTemp].destinationID + ", cost: " + dataArrays[numTemp].costID + ", infoMount: " + dataArrays[numTemp].infoMount);
                }
                System.out.println("CostTotal is: " + costTotal);
            }

        }
    }

    public void antResultSort(DataStructure[] dataArrays){
        DataStructure dataTemp;
        int iC, jC;

        for(iC = 0; iC < dataArrays.length; iC++){
            for(jC = iC + 1; jC < dataArrays.length; jC++){
                if(dataArrays[iC].infoMount < dataArrays[jC].infoMount){
                    dataTemp = dataArrays[jC];
                    dataArrays[jC] = dataArrays[iC];
                    dataArrays[iC] = dataTemp;
                }
            }
        }
    }

    public boolean antResultJudge(DataStructure[] dataArrays){
        boolean resultFlag;

        if(dataArrays[0].infoMount == 0){
            resultFlag = false;
        }else{
            dataAntMethod.antResultFindIni(dataArrays);
            resultFlag = true;
        }
        return resultFlag;
    }

    public void antResultFindIni(DataStructure[] dataArrays){
        int resultStart;
        int resultEnd;
        int iC;
        boolean searchFlag;
        boolean resultFlag;
        int cost1, cost2;

        resultFlag = false;
        antResult1.add(0);
        dataArrays[0].chosenPath = 1;
        while(!resultFlag) {
            resultStart = dataArrays[(int)antResult1.get(0)].sourceID;
            resultEnd = dataArrays[(int)antResult1.get(antResult1.size() - 1)].destinationID;
            if (resultStart == startPoint && resultEnd == endPoint) {
                resultFlag = true;
            } else {
                dataAntMethod.antResultFind1(dataArrays);
            }
        }

        cost1 = 0;
        for(iC = 0; iC < antResult1.size(); iC++){
            cost1 = cost1 + dataArrays[(int)antResult1.get(iC)].costID;
        }

        searchFlag = false;
        resultFlag = false;

        iC = 0;
        while(!searchFlag){
            if(dataArrays[iC].chosenPath == 0){
                searchFlag = true;
            }
            iC++;
        }
        iC--;
        antResult2.add(iC);
        dataArrays[iC].chosenPath = 1;
        while(!resultFlag) {
            resultStart = dataArrays[(int)antResult2.get(0)].sourceID;
            resultEnd = dataArrays[(int)antResult2.get(antResult2.size() - 1)].destinationID;
            if (resultStart == startPoint && resultEnd == endPoint) {
                resultFlag = true;
            } else {
                dataAntMethod.antResultFind2(dataArrays);
            }
        }

        cost2 = 0;
        for(iC = 0; iC < antResult2.size(); iC++){
            cost2 = cost2 + dataArrays[(int)antResult2.get(iC)].costID;
        }

        if(cost2 >= cost1){
            antResult = antResult1;
        }else{
            antResult = antResult2;
        }
    }

    public void antResultFind1(DataStructure[] dataArrays){
        int resultStart = dataArrays[(int)antResult1.get(0)].sourceID;
        int resultEnd = dataArrays[(int)antResult1.get(antResult1.size() - 1)].destinationID;
        int iC;

        if(resultStart != startPoint){
            for(iC = 0; iC < dataArrays.length; iC++){
                if(dataArrays[iC].infoMount > 0){
                    if(dataArrays[iC].destinationID == resultStart){
                        antResult1.add(0,iC);
                        dataArrays[iC].chosenPath = 1;
                        break;
                    }
                }
            }
        }

        if(resultEnd != endPoint){
            for(iC = 0; iC < dataArrays.length; iC++){
                if(dataArrays[iC].infoMount > 0){
                    if(dataArrays[iC].sourceID == resultEnd){
                        antResult1.add(iC);
                        dataArrays[iC].chosenPath = 1;
                        break;
                    }
                }
            }
        }
    }

    public void antResultFind2(DataStructure[] dataArrays){
        int resultStart = dataArrays[(int)antResult2.get(0)].sourceID;
        int resultEnd = dataArrays[(int)antResult2.get(antResult2.size() - 1)].destinationID;
        int iC;

        if(resultStart != startPoint){
            for(iC = 0; iC < dataArrays.length; iC++){
                if(dataArrays[iC].infoMount > 0){
                    if(dataArrays[iC].destinationID == resultStart){
                        antResult2.add(0,iC);
                        dataArrays[iC].chosenPath = 1;
                        break;
                    }
                }
            }
        }

        if(resultEnd != endPoint){
            for(iC = 0; iC < dataArrays.length; iC++){
                if(dataArrays[iC].infoMount > 0){
                    if(dataArrays[iC].sourceID == resultEnd){
                        antResult2.add(iC);
                        dataArrays[iC].chosenPath = 1;
                        break;
                    }
                }
            }
        }
    }



}
