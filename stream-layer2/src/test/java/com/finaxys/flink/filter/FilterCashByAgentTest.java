package com.finaxys.flink.filter;


import com.finaxys.flink.function.filter.FilterCashByAgent;
import com.google.common.collect.Lists;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class FilterCashByAgentTest {

    ExecutionEnvironment env ;

    @Before
    public void initialiser() throws Exception {
         env = ExecutionEnvironment.getExecutionEnvironment();
         }

    @Test
    public void testSum() throws Exception {



        // instantiate your function
        Row row1 = new Row(2);
        row1.setField(0,"Khalil");
        row1.setField(1,10);

        Row row2 = new Row(2);
        row2.setField(0,"Denise");
        row2.setField(1,20);

        Row row3 = new Row(2);
        row3.setField(0,"Atef");
        row3.setField(1,30);

        Tuple2<Boolean, Row> tuple1 = new Tuple2<>(true, row1);
        Tuple2<Boolean, Row> tuple2 = new Tuple2<>(false, row2);
        Tuple2<Boolean, Row> tuple3 = new Tuple2<>(true, row3);

        DataSet< Tuple2<Boolean, Row>> dataSet = env.fromElements(tuple1,tuple2,tuple3);

        DataSet< Tuple2<Boolean, Row>> dataSetResult = dataSet.filter(new FilterCashByAgent().getCashByAgent());

        // call the methods that you have implemented
        assertEquals(Lists.newArrayList(
                new   Tuple2<Boolean, Row>(true,row1),
                new   Tuple2<Boolean, Row>(true,row3)
        ),dataSetResult.collect());

    }


    @After
    public void nettoyer() throws Exception {
       env = null ;
    }
}
