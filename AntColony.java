package com.company;

import java.util.ArrayList;
import java.util.Random;
/**
 * Created by 卫智熠 on 2016/3/14.
 */
public class AntColony extends Main{


    static int antIDNum;
    int antID, antPlaceNow, antPlaceDes, antWait;
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
        this.antPath = new ArrayList();

    }

    static AntColony[] antColonies = new AntColony[antNumMax];

    public void antIni(DataStructure[] dataArrays, int pointStart, int pointEnd){
        int iC;
        int processCount; //蚁群算法运行时间计数器
        int timeMax = 10000; //蚁群算法运行的最多时间

        //初始化antNumMax数量的蚂蚁
        for(iC = 0; iC < antColonies.length; iC++){
            antColonies[iC] = new AntColony();
            antColonies[iC].antPlaceNow = pointStart;
        }
        System.out.println("Ant Colony has completed initialization.");

        //开始运行蚁群算法
        for(iC = 0; iC < timeMax; iC++){
            dataAntMethod.antProcess(dataArrays, pointEnd);
        }

        //输出蚁群算法的计算结果
        dataAntMethod.antResultShow(dataArrays);

    }

    public void antProcess(DataStructure[] dataArrays, int pointEnd){
        int iC;

        /*
        当前时刻，检查蚁群中每一只蚂蚁的状态。并根据检查结果更新单只蚂蚁的状态。
        每执行一次当前的方法，则表示经过了一个单位时间。
         */
        for(iC = 0; iC < antNumMax; iC++){
            dataAntMethod.antUpdata(dataArrays, antColonies[iC]);
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

    public void antUpdata(DataStructure[] dataArrays, AntColony antIndividual){
        int arrayNum = 0;

        //对于不处于等待状态的蚂蚁进行设置
        if(antIndividual.antWait == 0){

            if(!antFood) {
                //为ant选择一条路径，arrayNum指的是所选路径对应的map中的序号
                arrayNum = dataAntMethod.antPathChoose(dataArrays, antIndividual);

                //更改ant的状态
                dataAntMethod.antSet(dataArrays, antIndividual, arrayNum);
            }else{
                //ant释放信息素
                dataArrays[arrayNum].infoMount = dataArrays[arrayNum].infoMount + 100;
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
        int placeNow = antIndividual.antPlaceNow;
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
                        System.out.println("There is something wrong here");
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
            System.out.println("该节点没有子节点");
            return arrayNum;
        }

    }

    public void antSet(DataStructure[] dataArrays, AntColony antIndividual, int arrayNum){

    }

    public void antResultShow(DataStructure[] dataArrays){
        System.out.println("Here is the result of Ant Colony Optimization about this question");
    }
}
