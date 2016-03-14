package com.company;

import java.util.ArrayList;

/**
 * Created by 卫智熠 on 2016/3/14.
 */
public class AntColony extends Main{


    static int antIDNum;
    int antID, antPlaceNow, antPlaceDes, antWait, antFood;
    ArrayList antPath;

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
        }周而复始
         */

        this.antID = antIDNum; // 蚂蚁编号
        antIDNum ++ ;
        this.antPlaceNow = 0; // 蚂蚁此刻所处的位置
        this.antPlaceDes = 0; //蚂蚁将要去的位置
        this.antWait =  0; // 蚂蚁此刻是否处于等待状态。0代表不需要等待，可以出发，大于零的任意整数 x 代表所需要等待的时间。
        this.antFood = 0; // 蚂蚁此刻是否拥有从终点取得的食物。有食物，则要在节点释放信息素，没有食物，则不在结点释放信息素。
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
        当前时刻，检查蚁群中每一只蚂蚁的状态。并根据检查结果更新蚂蚁的状态以及地图的信息
         */
        for(iC = 0; iC < antNumMax; iC++){
            antUpdata(dataArrays, antColonies[iC]);
        }

    }

    public void antUpdata(DataStructure[] dataArrays, AntColony antIndividual){

    }

    public void antResultShow(DataStructure[] dataArrays){
        System.out.println("Here is the result of Ant Colony Optimization about this question");
    }
}
