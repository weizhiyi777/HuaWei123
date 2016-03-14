package com.company;

/**
 * Created by 卫智熠 on 2016/3/7.
 */

public class BFSStructure extends Main{
        int pointID, costTotal, pointPreID, pointName;


    public BFSStructure(){

        /*
        pointID 指的是当前点的标号；
        costTotal 指的是从出发点到当前点走的路径的总距离；
        pointPreArrayID 指的是在走过的路径中，当前节点作为子节点，其父节点所在的dataArrays中的位置。
            例如：dataArrays[pointPreArrayID].sourceID 为该子节点的父节点；
            dataArrays[pointPreArrayID].destinationID 为该子节点；
         */

        this.pointID = pointBFSNum;
        this.pointPreID = 0;
        this.pointName = 0;
        this.costTotal = 0;
        pointBFSNum ++;

    }

    public BFSStructure(int pointPreID){

        /*
        pointID 指的是当前点的标号；
        costTotal 指的是从出发点到当前点走的路径的总距离；
        pointPreArrayID 指的是在走过的路径中，当前节点作为子节点，其父节点所在的dataArrays中的位置。
            例如：dataArrays[pointPreArrayID].sourceID 为该子节点的父节点；
            dataArrays[pointPreArrayID].destinationID 为该子节点；
         */

        this.pointID = pointBFSNum;
        this.pointPreID = pointPreID;
        this.pointName = 0;
        this.costTotal = 0;
        pointBFSNum ++;

    }

}
